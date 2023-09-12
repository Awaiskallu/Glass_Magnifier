package com.example.glassmagnifier

import android.content.Context
import android.widget.Toast

class Toaster {
    companion object{
        var toast: Toast?=null
        fun showToast(c:Context,msg:String){
            clearToasts()

            toast = Toast.makeText(c,msg,Toast.LENGTH_SHORT)
            toast?.show()
        }
        fun clearToasts() {
            toast?.cancel()
            toast = null
        }
    }
}