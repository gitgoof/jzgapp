package com.jzg.jzgoto.phone.view.common;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

public class InterceptedEventLinearLayout extends LinearLayout {
	private boolean interceptTouchEvent = true;
	private View.OnTouchListener touchActionListen = null;
	
    public InterceptedEventLinearLayout(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    public InterceptedEventLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    public InterceptedEventLinearLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
    }
	
    @Override  
    public boolean onInterceptTouchEvent(MotionEvent ev) {
		if (touchActionListen != null){
    		touchActionListen.onTouch(this, ev);
    	}
		return interceptTouchEvent;
    }  
	
	@Override
    public boolean onTouchEvent(MotionEvent ev) {
		return super.onTouchEvent(ev);
    }
	
	public void setInterceptEvent(boolean handle){
		interceptTouchEvent = handle;
	}
	
	public void setOnTouchListener(View.OnTouchListener listen){
		touchActionListen = listen;
	}

}
