package com.greatwyc.MTF;

import android.app.*;
import android.os.*;
import java.io.*;
import android.widget.*;
import android.view.View.*;
import android.view.*;
import android.view.inputmethod.*;
import android.widget.AdapterView.*;
import android.content.*;
import com.greatwyc.G_Midi;
import android.graphics.*;
import android.support.v4.*;
import android.support.v4.widget.*;
import android.content.pm.*;
import android.widget.CompoundButton.*;
import java.util.*;

public class MainActivity extends Activity 
{
	ProgressBar bar;
	Button go;
	Button help;
	Button about;
	EditText path;
	EditText speed;
	EditText name;
	String ins;
	Spinner instrument;
	Switch use_particle_switch;
	View waitText;
	Thread workThread;
	DrawerLayout drawerMenu;
	View menu;
	TextView ver;
	PackageManager pm;
	
	boolean use_particle = false;
	final String[] instrument_list = new String[]{"harp","bass","bell","flute","chime","guitar","xylophone","iron_xylophone","cow_bell","didgeridoo","bit","banjo","pling","piano"};
	File dir;
	
	private Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg)
		{
			// TODO: Implement this method
			if(msg.what == 114){
				bar.setVisibility(View.GONE);
				waitText.setVisibility(View.GONE);
				go.setClickable(true);
				Toast.makeText(getApplicationContext(),"Finish.",3).show();
			}
			super.handleMessage(msg);
		}
		
	};
	
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);
		
		dir = new File(Environment.getExternalStorageDirectory().getPath()+"/MTF");
		drawerMenu = findViewById(R.id.drawer_menu);
		go = (Button) findViewById(R.id.Go);
		help = (Button)findViewById(R.id.help);
		about = (Button)findViewById(R.id.about);
		bar = (ProgressBar) findViewById(R.id.mainProgressBar1);
		path = (EditText) findViewById(R.id.path);
		name = (EditText) findViewById(R.id.name);
		instrument = (Spinner) findViewById(R.id.instrument);
		use_particle_switch = (Switch)findViewById(R.id.use_particle);
		speed = (EditText) findViewById(R.id.speed);
		waitText = (View) findViewById(R.id.waitText);
		menu = findViewById(R.id.menu);
		ver = (TextView) findViewById(R.id.ver);
		
		pm = getPackageManager();
		
		bar.setVisibility(View.GONE);
		waitText.setVisibility(View.GONE);
		
		try{
			ver.setText("MTF"+pm.getPackageInfo(getPackageName(),0).versionName);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		AlertDialog.Builder notice= new AlertDialog.Builder(MainActivity.this);
		
		notice.setTitle("使用须知");
		notice.setMessage("1.本软件只能正常转换部分midi\n2.生成的文件位于手机储存中的MTF/package文件夹下");
		notice.setPositiveButton("确定",new DialogInterface.OnClickListener()
			{
				@Override
				public void onClick(DialogInterface dialog, int which)
				{
					//emmm
				}
			});
		notice.create().show();
		new Thread(new Runnable(){
			@Override
			public void run(){
				try{
    				if(!dir.exists()){
    					dir.mkdir();
    				}
    				if(!(new File(dir,"particle").exists())){
    					new File(dir,"particle").mkdir();
    				}
    				if(!(new File(dir,"package").exists())){
    					new File(dir,"package").mkdir();
    				}
    				if(!(new File(dir,"particle/textures").exists())){
    					new File(dir,"particle/textures").mkdir();
    				}
    				if(!(new File(dir,"particle/particles").exists())){
    					new File(dir,"particle/particles").mkdir();
    				}
					if(!(new File(dir,"particle/particles/link.json").exists())){
    					InputStream fis = getApplicationContext().getAssets().open("link.json");
						byte[] data = new byte[fis.available()];
						fis.read(data);
						OutputStream fos = new FileOutputStream(new File(dir,"particle/particles/link.json"));
						fos.write(data);
						fos.close();
    				}
					if(!(new File(dir,"particle/particles/base.json").exists())){
    					InputStream fis = getApplicationContext().getAssets().open("base.json");
						byte[] data = new byte[fis.available()];
						fis.read(data);
						OutputStream fos = new FileOutputStream(new File(dir,"particle/particles/base.json"));
						fos.write(data);
						fos.close();
    				}
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}).start();
    }

	@Override
	protected void onStart()
	{
		// TODO: Implement this method
		super.onStart();
		instrument.setOnItemSelectedListener(new OnItemSelectedListener(){

				@Override
				public void onItemSelected(AdapterView<?> parent,View view,int p,long id){
					ins = instrument_list[p];
				}

				@Override
				public void onNothingSelected(AdapterView<?> parent){
					ins = "harp";
				}
			});

		go.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v){
					final File f = new File(path.getText().toString());
					if(f.exists()){
						try{
							bar.setVisibility(View.VISIBLE);
							waitText.setVisibility(View.VISIBLE);
							go.setClickable(false);
							workThread = new Thread(new Runnable(){
								public void run(){
									try{
										List<List> data  = G_Midi.parseMidi(f);
										FileCreate.createPackage(getApplicationContext(),new File(dir,"package"),name.getText().toString());
										if(ins.equals("piano")){
												G_Midi.messageToFunction((name.getText().toString()),data,Double.valueOf(speed.getText().toString()));
										}else{
												G_Midi.messageToCommand((name.getText().toString()),data,Double.valueOf(speed.getText().toString()),ins);
										}
										if(use_particle){
											FileCreate.copyDir(new File(dir,"particle/particles"),new File(dir,"package/"+name.getText()+"/r"));
											FileCreate.copyDir(new File(dir,"particle/textures"),new File(dir,"package/"+name.getText()+"/r"));
											List d = Particle.createStrack(data,new File(dir,"package/"+name.getText()+"/b/functions"),Double.valueOf(speed.getText().toString()));
											Particle.messageToParticle(d,new File(dir,"particle/particles/link.json"),dir,name.getText().toString());
										}
										if(ins.equals("piano")){
											FileCreate.createSoundPack(getApplicationContext(),name.getText().toString(),new File(dir,"package"));
										}
									}catch(Exception e){
										e.printStackTrace();
									}
									Message message=Message.obtain();
									message.what = 114;
									handler.sendMessage(message);
								}
							});
							workThread.start();
						}catch(Exception e){
							AlertDialog.Builder err = new AlertDialog.Builder(MainActivity.this);
							err.setMessage(e.toString());
							err.create().show();
						}
					}else{
						Toast.makeText(getApplicationContext(),"Files not found.",3).show();
					}
				}
			});
			
		use_particle_switch.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(CompoundButton compoundButton, boolean b){
				if(!use_particle){
					use_particle = true;
				}else{
					use_particle = false;
				}
			}
		});
		
		help.setOnClickListener(new OnClickListener(){
			
			@Override
			public void onClick(View v){
				startActivity(new Intent(MainActivity.this,HelpActivity.class));
			}
			
		});
		
		about.setOnClickListener(new OnClickListener(){
			
			@Override
			public void onClick(View v){
				startActivity(new Intent(MainActivity.this,AboutActivity.class));
			}
			
		});
		
		menu.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				drawerMenu.openDrawer(Gravity.LEFT);
			}
		});
	}
	
	

	@Override
	protected void onResume()
	{
		// TODO: Implement this method
		super.onResume();
	}

	@Override
	protected void onDestroy()
	{
		// TODO: Implement this method
		super.onDestroy();
	}
	
}
