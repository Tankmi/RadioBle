package com.example.radio.radioble.utils;


import android.widget.Toast;

import com.example.radio.radioble.context.ApplicationData;


public class ToastUtils{
	
	/** Toast的对象！ */
	public static Toast toast = null; 
	
	public ToastUtils() {
		
	}
	
	public ToastUtils(String id) {
		toast = Toast.makeText(ApplicationData.context, id, Toast.LENGTH_SHORT);
	}

	public static void showToast(String id) {
		if (toast == null) { 
            toast = Toast.makeText(ApplicationData.context, id, Toast.LENGTH_SHORT); 
        } else { 
            toast.setText(id); 
        } 
        toast.show(); 
    }
}
