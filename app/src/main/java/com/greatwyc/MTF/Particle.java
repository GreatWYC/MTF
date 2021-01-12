package com.greatwyc.MTF;
import com.alibaba.fastjson.*;
import java.util.*;
import java.io.*;
import android.content.res.*;
import android.content.*;
import java.nio.file.*;
import org.xml.sax.*;

public class Particle
{
	
	public static void messageToParticle(List<List<Map>> d,File f,File dir,String name) throws Exception{
		FileWriter fw = new FileWriter(new File(dir,"package/"+name+"/b/functions/"+name+".mcfunction"),true);
		BufferedReader fr = new BufferedReader(new InputStreamReader(new FileInputStream(f),"UTF-8"));
		String data = fr.readLine();
		double dt;
		double t = 0.0;
		int dx;
		int dz;
		int tag = 0;
		JSONObject p = JSON.parseObject(data);
		String cmd = "function destroyBlock\n";
		String dcmd = "";
		fw.write("function p_"+name);
		fw.close();
		for(int i = 0;i < d.size()-1 ; i++){
			dt = Double.parseDouble(d.get(i+1).get(0).get("time").toString())-Double.parseDouble(d.get(i).get(0).get("time").toString());
			t+= dt;
			if(d.get(i).size()<=d.get(i+1).size()){
				for(int j = 0;j < d.get(i+1).size();j++){
					OutputStream pfos = new FileOutputStream(new File(dir.getPath()+"/package/"+name+"/r/particles","link"+tag+".json"));
					dx = Integer.parseInt(d.get(i+1).get(j).get("x").toString())-Integer.parseInt(d.get(i).get(j>d.get(i).size()-1?d.get(i).size()-1:j).get("x").toString());
					dz = Integer.parseInt(d.get(i+1).get(j).get("z").toString())-Integer.parseInt(d.get(i).get(j>d.get(i).size()-1?d.get(i).size()-1:j).get("z").toString());
					
					p.getJSONObject("particle_effect").getJSONObject("components").getJSONObject("minecraft:emitter_initialization").put("creation_expression","variable.t="+dt+";variable.dx="+dx+";variable.dz="+dz+";");
					p.getJSONObject("particle_effect").getJSONObject("components").getJSONObject("minecraft:emitter_lifetime_expression").put("activation_expression","variable.emitter_age<"+dt+"?1:0");
					p.getJSONObject("particle_effect").getJSONObject("description").put("identifier","pa:link"+tag);
					cmd+="execute @p[scores={GMidiPlayer="+(int)(t*20)+"}] ~~~ particle pa:link"+tag+" "+d.get(i).get(j>d.get(i).size()-1?d.get(i).size()-1:j).get("x")+" 50 "+d.get(i).get(j>d.get(i).size()-1?d.get(i).size()-1:j).get("z")+"\n";
					dcmd+="execute @p[scores={GMidiPlayer="+(int)(t*20)+"}] ~~~ setblock "+d.get(i).get(j>d.get(i).size()-1?d.get(i).size()-1:j).get("x")+" 49 "+d.get(i).get(j>d.get(i).size()-1?d.get(i).size()-1:j).get("z")+" air\n";
					
					pfos.write(JSON.toJSONString(p).getBytes());
					tag++;
					pfos.close();
				}
			}else{
				for(int j = 0;j < d.get(i).size();j++){
					OutputStream pfos = new FileOutputStream(new File(dir.getPath()+"/package/"+name+"/r/particles","link"+tag+".json"));
					dx = Integer.parseInt(d.get(i+1).get(j>d.get(i+1).size()-1?d.get(i+1).size()-1:j).get("x").toString())-Integer.parseInt(d.get(i).get(j).get("x").toString());
					dz = Integer.parseInt(d.get(i+1).get(j>d.get(i+1).size()-1?d.get(i+1).size()-1:j).get("z").toString())-Integer.parseInt(d.get(i).get(j).get("z").toString());
					
					p.getJSONObject("particle_effect").getJSONObject("components").getJSONObject("minecraft:emitter_initialization").put("creation_expression","variable.t="+dt+";variable.dx="+dx+";variable.dz="+dz+";");
					p.getJSONObject("particle_effect").getJSONObject("components").getJSONObject("minecraft:emitter_lifetime_expression").put("activation_expression","variable.emitter_age<"+dt+"?1:0");
					p.getJSONObject("particle_effect").getJSONObject("description").put("identifier","pa:link"+tag);
					cmd+="execute @p[scores={GMidiPlayer="+(int)(t*20)+"}] ~~~ particle pa:link"+tag+" "+d.get(i).get(j).get("x")+" 50 "+d.get(i).get(j).get("z")+"\n";
					dcmd+="execute @p[scores={GMidiPlayer="+(int)(t*20)+"}] ~~~ setblock "+d.get(i).get(j).get("x")+" 49 "+d.get(i).get(j).get("z")+" air\n";
					
					pfos.write(JSON.toJSONString(p).getBytes());
					tag++;
					pfos.close();
				}
			}
		}
		OutputStream cfos = new FileOutputStream(new File(dir,"package/"+name+"/b/functions/p_"+name+".mcfunction"));
		cfos.write(cmd.getBytes());
		cfos = new FileOutputStream(new File(dir,"package/"+name+"/b/functions/destroyBlock.mcfunction"));
		cfos.write(dcmd.getBytes());
		cfos.close();
	}
	
	public static List createStrack(List<List> gm,File dir,double speed) throws Exception{
		int p = 0;
		int i = 0;
		double dt = 0;
		int x;
		List<List<Map>> result = new ArrayList();
		OutputStream fos = new FileOutputStream(new File(dir,"create.mcfunction"));
		Map pos;
		List group;
		String cmd = "scoreboard objectives add MTFBuilder dummy MTFBuilder\nscoreboard players add @a MTFBuilder 1\nexecute @p[scores={MTFBuilder=1}] ~~~ tickingarea add ~-8 ~-8 ~-8 ~8 ~8 ~8\n";
		for(List l : gm){
			p = 0;
			dt = 0;
			cmd+="execute @p[scores={MTFBuilder="+p+"}] ~~~ tp @p 0 50 0\n";
			for(Map m : l){
				dt += ((int)m.get("delta_time") / speed );
				if(m.get("event").equals("note_on")||m.get("event").equals("note_after_touch")||m.get("event").equals("channel_aftertouch")){
					x = (int)Math.floor(dt*10.89);
					p++;
					cmd+="execute @p[scores={MTFBuilder="+p+"}] ~~~ tp @p[scores={MTFBuilder="+p+"}] "+ x +" 50 "+m.get("note")+"\n";
					p++;
					cmd+="execute @p[scores={MTFBuilder="+p+"}] ~~~ setblock ~ ~-1 ~ concrete "+i+"\n";
					pos = new HashMap();
					pos.put("x",x);
					pos.put("z",m.get("note"));
					pos.put("time",dt);
					if(result.size()==0||Double.parseDouble(result.get(result.size()-1).get(0).get("time").toString())!=dt){
						group = new ArrayList();
						group.add(pos);
						result.add(group);
					}else{
						result.get(result.size()-1).add(pos);
					}
				}
			}
			dt = 0;
			i++;
		}
		fos.write(cmd.getBytes());
		fos.close();
		return result;
	}
	
}

