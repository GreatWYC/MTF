package com.greatwyc.MTF;
import android.app.*;
import android.os.*;
import android.view.*;

public class AboutActivity extends Activity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.about_layout);
		
		findViewById(R.id.menu).setVisibility(View.GONE);
	}
	
}
