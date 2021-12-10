package com.example.myapplication.ui.reflow

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myapplication.ui.transform.weatherItem

class ReflowViewModel : ViewModel() {

    private val _text = MutableLiveData<weatherItem>().apply {
//        value = "This is reflow Fragment"
    }

    fun ch(w: weatherItem){
        _text.value = w
    }
    val text: LiveData<weatherItem> = _text
}