package com.example.myapplication.ui.transform

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.Nullable
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentTransformBinding
import com.example.myapplication.databinding.ItemTransformBinding

import android.content.Context
import android.content.res.Resources
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.findNavController
import com.example.myapplication.MainActivity
import com.example.myapplication.ui.reflow.ReflowFragment
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

/**
 * Fragment that demonstrates a responsive layout pattern where the format of the content
 * transforms depending on the size of the screen. Specifically this Fragment shows items in
 * the [RecyclerView] using LinearLayoutManager in a small screen
 * and shows items using GridLayoutManager in a large screen.
 */

class TransformViewHolder(binding: ItemTransformBinding) : RecyclerView.ViewHolder(binding.root) {
//    val fa: ItemTransformBinding = binding
    val icon: ImageView = binding.imageViewItemTransform
    val textView: TextView = binding.textViewItemTransform
    val hb: ConstraintLayout = binding.hitbox!!

}


class TransformFragment : Fragment() {

    companion object{
        val transta = Stack<TransformFragment>()
        fun getFrontTransformFragment(): TransformFragment? {
            return transta.lastElement()
        }
    }

    lateinit var transformViewModel: TransformViewModel
    private var _binding: FragmentTransformBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        transformViewModel = ViewModelProvider(this).get(TransformViewModel::class.java)
        _binding = FragmentTransformBinding.inflate(inflater, container, false)
        transta.push(this)
        val root: View = binding.root

        val recyclerView = binding.recyclerviewTransform
        val adapter = TransformAdapter()
        recyclerView.adapter = adapter
        transformViewModel.texts.observe(viewLifecycleOwner, {
            adapter.submitList(it)
        })
        transformViewModel.cur.observe(viewLifecycleOwner, {

            binding.curWeatherIcon?.let {  bi->
                bi.setImageResource(
                bi.context.resIdByName(
                    "_${it.icon}", "drawable"
                )
            )}
            binding.temperature?.let { bi->
                bi.text = "${it.temp}°"
            }

            binding.weatherState?.let { bi ->
                bi.text = "${it.text}"
            }
        })

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        transta.pop()
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun getWeekOfDate(dt: LocalDate): Char {
    val weekDays = arrayOf('日', '一', '二', '三', '四', '五', '六','日')
    var w = dt.dayOfWeek.value

    return weekDays[w]
}

fun Context.resIdByName(resIdName: String?, resType: String): Int {
    resIdName?.let {
        return resources.getIdentifier(it, resType, packageName)
    }
    throw Resources.NotFoundException()
}



class TransformAdapter : ListAdapter<weatherItem, TransformViewHolder>(object : DiffUtil.ItemCallback<weatherItem>() {

    override fun areItemsTheSame(oldItem: weatherItem, newItem: weatherItem): Boolean =
        oldItem == newItem

    override fun areContentsTheSame(oldItem: weatherItem, newItem: weatherItem): Boolean =
        oldItem == newItem
}) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransformViewHolder {
        val binding = ItemTransformBinding.inflate(LayoutInflater.from(parent.context))
        return TransformViewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: TransformViewHolder, position: Int) {
        val item = getItem(position)
        val da = LocalDate.parse(item.fxDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"))

        val weathertext = if(item.textDay==item.textNight)item.textDay else "${item.textDay}转${item.textNight}"

        holder.textView.text = "周${getWeekOfDate(da)} "+weathertext+" ${item.tempMin}~${item.tempMax}°"
        Log.d("icon", item.iconDay.toString())

        val drawableResourceId: Int = holder.icon.context.resIdByName(
            "_${item.iconDay}", "drawable"
        )
        holder.icon.setImageResource(
            drawableResourceId
        )

        holder.icon.setOnClickListener {

            val bck = MainActivity.getFrontActivity()!!
            Log.d("[click]", "clicked!")
            ReflowFragment.detailedItem = item
            bck.toDetail()
        }
        holder.textView.setOnClickListener{
            val tf = TransformFragment.getFrontTransformFragment()
            if (tf != null) {
                tf.binding.curWeatherIcon?.let {  bi->
                    bi.setImageResource(
                        bi.context.resIdByName(
                            "_${item.iconDay}", "drawable"
                        )
                    )}
                tf.binding.temperature?.let { bi->
                    bi.text = "${item.tempMax}°"
                }
                tf.binding.weatherState?.let { bi ->
                    bi.text = "${item.textDay}"
                }
            }
        }
    }
}


