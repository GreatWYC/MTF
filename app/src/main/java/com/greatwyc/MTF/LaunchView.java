package com.greatwyc.MTF;

import android.os.*;
import android.view.*;
import android.content.*;
import android.app.Activity;
import android.content.pm.*;
import android.widget.*;
import android.text.Html;

public class LaunchView extends Activity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getActionBar().hide();
		
		setContentView(R.layout.launchview);
		TextView codeinfo = (TextView)findViewById(R.id.version);
		PackageManager pm = getPackageManager();
		PackageInfo inf = null;
		try{
			inf = pm.getPackageInfo(getPackageName(),0);
		}catch(Exception e){
			e.printStackTrace();
		}
		codeinfo.setText(Html.fromHtml("Version "+inf.versionName));
		Thread myThread=new Thread(){//创建子线程
			@Override
			public void run() {
				try{
					sleep(3000);//使程序休眠五秒
					Intent itent=new Intent(getApplicationContext(),MainActivity.class);//启动MainActivity
					startActivity(itent);
					finish();//关闭当前活动
				}catch (Exception e){
					e.printStackTrace();
				}
			}
		};
		myThread.start();
		}
	
}
