package com.greatwyc.MTF;
import com.alibaba.fastjson.*;
import java.util.*;
import java.io.*;
import android.content.res.*;
import android.content.*;

public class FileCreate
{
	public static void createPackage(Context context,File dir,String name) throws Exception{
		File pd = new File(dir,name);
		AssetManager am = context.getAssets();
		byte[] data;
		InputStream fis;
		FileOutputStream fos;
		BufferedReader rmfis = new BufferedReader(new InputStreamReader(am.open("r_manifest.json")));
		BufferedReader bmfis = new BufferedReader(new InputStreamReader(am.open("b_manifest.json")));

		String r_uuid0 = UUID.randomUUID().toString();
		String r_uuid1 = UUID.randomUUID().toString();
		String b_uuid0 = UUID.randomUUID().toString();
		String b_uuid1 = UUID.randomUUID().toString();
		JSONObject rm = JSON.parseObject(rmfis.readLine());
		JSONObject bm = JSON.parseObject(bmfis.readLine());
		rm.getJSONObject("header").put("name",name);
		rm.getJSONObject("header").put("uuid",r_uuid0);
		rm.getJSONArray("modules").getJSONObject(0).put("uuid",r_uuid1);
		bm.getJSONObject("header").put("name",name);
		bm.getJSONObject("header").put("uuid",b_uuid0);
		bm.getJSONArray("modules").getJSONObject(0).put("uuid",b_uuid1);
		bm.getJSONArray("dependencies").getJSONObject(0).put("uuid",r_uuid1);

		pd.mkdir();
		new File(pd,"b").mkdir();
		new File(pd,"r").mkdir();
		new File(pd.getPath()+"/b","functions").mkdir();
		new File(pd.getPath()+"/b","manifest.json").createNewFile();
		//new File(pd.getPath()+"/r","particles").mkdir();
		//new File(pd.getPath()+"/r","textures").mkdir();
		new File(pd.getPath()+"/r","manifest.json").createNewFile();

		FileOutputStream bmfos = new FileOutputStream(pd.getPath()+"/b/manifest.json");
		bmfos.write(JSON.toJSONString(bm).getBytes());
		FileOutputStream rmfos = new FileOutputStream(pd.getPath()+"/r/manifest.json");
		rmfos.write(JSON.toJSONString(rm).getBytes());

		fis = am.open("pack_icon.png");
		data = new byte[fis.available()];
		fis.read(data);
		fos = new FileOutputStream(pd.getPath()+"/r/pack_icon.png");
		fos.write(data);
		fos = new FileOutputStream(pd.getPath()+"/b/pack_icon.png");
		fos.write(data);
		
		fis.close();
		fos.close();
		bmfis.close();
		rmfis.close();
		bmfos.close();
		rmfos.close();
	}
	
	public static void createSoundPack(Context context,String name,File dir) throws Exception{
			File pd = new File(dir,name);
            AssetManager am = context.getAssets();
			String list[] = am.list("sounds/piano");
			InputStream fis;
			FileOutputStream fos;
			new File(pd.getPath()+"/r","sounds").mkdir();
			new File(pd.getPath()+"/r/sounds","piano").mkdir();
			for(String i:list){
				fis = am.open("sounds/piano/"+(i));
				byte[] data = new byte[fis.available()];
				fis.read(data);
				fos = new FileOutputStream(pd.getPath()+"/r/sounds/piano/"+i);
				fos.write(data);
				fis.close();
				fos.close();
			}
			fis = am.open("sounds/sound_definitions.json");
			byte[] data = new byte[fis.available()];
			fis.read(data);
			fos = new FileOutputStream(pd.getPath()+"/r/sounds/sound_definitions.json");
			fos.write(data);
			fis.close();
			fos.close();
	}
	
	public static void copyDir(File dir,File target) throws Exception{
		InputStream fis = null;
		OutputStream fos = null;
		byte[] data;
		File outFile;
		File tf = new File(target,dir.getName());
		if(!tf.exists())tf.mkdir();
			for(File f : dir.listFiles()){
				if(f.isFile()){
					outFile = new File(tf,f.getName());
					outFile.createNewFile();
					fis = new FileInputStream(f);
					data = new byte[fis.available()];
					fis.read(data);
					fos = new FileOutputStream(outFile);
					fos.write(data);
					fis.close();
					fos.close();
				}else{
					outFile = new File(tf,f.getName());
					outFile.mkdir();
					copyDir(outFile,new File(tf,outFile.getName()));
			}
		}
	}
}
