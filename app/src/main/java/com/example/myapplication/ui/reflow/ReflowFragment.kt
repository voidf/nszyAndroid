package com.example.myapplication.ui.reflow

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.MainActivity
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentReflowBinding
import com.example.myapplication.ui.transform.cur_loc
import com.example.myapplication.ui.transform.cur_wea
import com.example.myapplication.ui.transform.resIdByName
import com.example.myapplication.ui.transform.weatherItem

class ReflowFragment : Fragment() {

    private lateinit var reflowViewModel: ReflowViewModel
    private var _binding: FragmentReflowBinding? = null

    companion object{
        lateinit var detailedItem: weatherItem
    }

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        reflowViewModel =
                ViewModelProvider(this).get(ReflowViewModel::class.java)



        _binding = FragmentReflowBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.locButton.text = "位置:${cur_loc.name}"

        binding.locButton.setOnClickListener {
            MainActivity.getFrontActivity()!!.toMap()
        }

        binding.shareButton.setOnClickListener {
            val intent= Intent()
            intent.action=Intent.ACTION_SEND
            intent.putExtra(Intent.EXTRA_TEXT,"${cur_loc.name}的天气：${cur_wea.text}")
            intent.type="text/plain"
            startActivity(Intent.createChooser(intent,"Share To:"))
        }

        reflowViewModel.text.observe(viewLifecycleOwner, Observer {
            binding.dateHint.text = it.fxDate
            binding.imageView2.setImageResource(
                binding.imageView2.context.resIdByName(
                    "_${it.iconDay}", "drawable"
                )
            )
            binding.tempMx.text = "${it.tempMax}°"
            binding.tempMn.text = "${it.tempMin}°"
            binding.otherHint.text = "湿度：${it.humidity}%\n气压：${it.pressure} hPa"

        })
        try{
            if(reflowViewModel.text.value!=detailedItem)
                reflowViewModel.ch(detailedItem)
        }
        catch (e:Exception){
            e.printStackTrace()
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}