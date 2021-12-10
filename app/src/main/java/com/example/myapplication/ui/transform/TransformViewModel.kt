package com.example.myapplication.ui.transform

import android.content.Context
import android.util.Log
import androidx.annotation.NonNull
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.preference.PreferenceManager
import androidx.room.*
import com.example.myapplication.MainActivity
import com.example.myapplication.ktorClient
import io.ktor.client.request.*
import kotlinx.coroutines.launch

import kotlinx.serialization.*
import kotlinx.serialization.json.Json

@Serializable
data class weatherItem(
    val cloud: Int,
    val fxDate: String,
    val humidity: Int,
    val iconDay: Int,
    val iconNight: Int,
    val moonPhase: String,
    val moonPhaseIcon: Int,
    val moonrise: String,
    val moonset: String,
    val precip: Float,
    val pressure: Int,
    val sunrise: String,
    val sunset: String,
    var tempMax: Int,
    var tempMin: Int,
    val textDay: String,
    val textNight: String,
    val uvIndex: Int,
    val vis: Int,
    val wind360Day: Int,
    val wind360Night: Int,
    val windDirDay: String,
    val windDirNight: String,
    val windScaleDay: String,
    val windScaleNight: String,
    val windSpeedDay: String,
    val windSpeedNight: String,
)
{
//    override fun equals(other: Any?):Boolean{
//        return (other is weatherItem) && degree == other.degree && msg == other.msg
//    }
}

@Serializable
data class weather7dResponse(
    var daily: List<weatherItem>
)

@Serializable
data class nowWeather(
    val cloud: Int,
    val dew:Int,
    val feelsLike:Int,
    val humidity: Int,
    val icon:Int,
    val obsTime: String,
    val precip: Float,
    val pressure: Int,
    var temp: Int,
    val text:String,
    val vis:Int,
    val wind360: Int,
    val windDir: String,
    val windScale:Int,
    val windSpeed:Int,
)

@Serializable
data class weatherNowResponse(
    var now: nowWeather
)


@Serializable
data class LocationResult(
    val name:String,
    val id:String,
    val rank:Int,
    val lat:Float,
    val lon:Float
)

@Serializable
data class GeoapiResponse(
    val location:List<LocationResult>
)

@Entity
data class CachedJson(
    @PrimaryKey val id: String,
    @NonNull @ColumnInfo(name="ct") val content: String
)

@Dao
interface CachedJsonDao{
    @Query("SELECT * FROM cachedjson where id = :qid")
    fun getCachedJson(qid: String): List<CachedJson>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCachedJson(vararg p: CachedJson)
}

@Database(entities = arrayOf(CachedJson::class), version=1)
abstract class AppDatabase: RoomDatabase(){
    abstract fun gDao(): CachedJsonDao
    companion object{
        @Volatile
        private var INSTANCE: AppDatabase? = null
        fun getDB(context: Context):AppDatabase{
            return INSTANCE?: synchronized(this){
                val instance = Room.databaseBuilder(
                    context,
                    AppDatabase::class.java,
                    "app_database"
                ).allowMainThreadQueries()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}



//var citycode = "101010100"
lateinit var cur_loc: LocationResult
lateinit var cur_wea: nowWeather

val your_api_key = "114514ccf_nima_shenme_shihou_si_a" // 填和风api key

suspend fun getgeoapi(): String {
    val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.getFrontActivity())
    val loc = sharedPreferences.getString("location_preference", "长沙")
    return ktorClient.get<String>("https://geoapi.qweather.com/v2/city/lookup?key=${your_api_key}&location=${loc}")
}

suspend fun get7d(lid: String): String{
    return ktorClient.get<String>("https://devapi.qweather.com/v7/weather/7d?location=${lid}&key=${your_api_key}")
}

fun decode7d(r: String): weather7dResponse{
    val ret = Json { ignoreUnknownKeys = true }.decodeFromString<weather7dResponse>(r)
    val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.getFrontActivity())
    val f = sharedPreferences.getBoolean("temp_format", false)
    if(f)
    {
        ret.daily.forEach { it->
            it.tempMax=(1.8*it.tempMax+32).toInt()
            it.tempMin=(1.8*it.tempMin+32).toInt()
        }
    }
    return ret
}

fun decodenow(r: String): weatherNowResponse{
    val ret = Json { ignoreUnknownKeys = true }.decodeFromString<weatherNowResponse>(r)
    val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.getFrontActivity())
    val f = sharedPreferences.getBoolean("temp_format", false)
    if(f)
    {
        ret.now.temp=(1.8*ret.now.temp+32).toInt()
    }
    return ret
}

suspend fun getnow(lid: String):String{
    return ktorClient.get<String>("https://devapi.qweather.com/v7/weather/now?key=${your_api_key}&location=${lid}")
}

class TransformViewModel : ViewModel() {
    private val _texts = MutableLiveData<List<weatherItem>>()
    private val _cur = MutableLiveData<nowWeather>()
    init {
        getWeather()
    }

    fun getWeather(){
        viewModelScope.launch {
            var retrytime = 0

            val gdb = AppDatabase.getDB(MainActivity.getFrontActivity()!!.applicationContext)
            val dao = gdb.gDao()

            while(true) {
                try {
                    val geoapistr = getgeoapi()
                    val locobj = Json{ignoreUnknownKeys=true}.decodeFromString<GeoapiResponse>(
                        geoapistr
                    ).location
                    cur_loc = locobj[0]
                    dao.insertCachedJson(CachedJson(
                        "${cur_loc.name}_loc", geoapistr
                    ))
                    Log.d("[geoapi]", locobj[0].toString())
                    val lid = locobj[0].id

                    val j = get7d(lid)
                    val w = decode7d(j)
                    _texts.value = w.daily
                    dao.insertCachedJson(CachedJson(
                        "${cur_loc.name}_7d", j
                    ))
                    val k = getnow(lid)
                    val v = decodenow(k)
                    _cur.value = v.now
                    dao.insertCachedJson(CachedJson(
                        "${cur_loc.name}_now", k
                    ))
                    cur_wea = v.now
                    break
                } catch (e: Exception) {
                    e.printStackTrace()
                    if(++retrytime>5)
                    {
                        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.getFrontActivity())
                        val loc = sharedPreferences.getString("location_preference", "长沙")
                        val qloc = dao.getCachedJson("${loc}_loc")
                        if(qloc.isNotEmpty())
                        {
                            val locobj = Json{ignoreUnknownKeys=true}.decodeFromString<GeoapiResponse>(
                                qloc[0].content
                            ).location
                            cur_loc = locobj[0]
                        }
                        val q7d = dao.getCachedJson("${loc}_7d")
                        if(q7d.isNotEmpty())
                        {
                            val w = decode7d(q7d[0].content)
                            _texts.value = w.daily
                        }
                        val qnow = dao.getCachedJson("${loc}_now")
                        if(qnow.isNotEmpty())
                        {
                            val v = decodenow(qnow[0].content)
                            _cur.value = v.now
                            cur_wea = v.now
                        }
                        break
                    }
                }
            }

        }
    }




    val texts: LiveData<List<weatherItem>> = _texts
    val cur: LiveData<nowWeather> = _cur
}