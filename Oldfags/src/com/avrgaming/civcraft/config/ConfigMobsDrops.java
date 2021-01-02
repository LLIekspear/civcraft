package com.avrgaming.civcraft.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.file.FileConfiguration;

import com.avrgaming.civcraft.main.CivLog;

public class ConfigMobsDrops {
	public String id;
	public int CoinsMin;
	public int CoinsMax;
	public double modifySpeed;
	public double Attack;
	public double MaxHealth;
	
	public String[] drop;
	public Double[] dropC;
	
	public static void loadConfig(FileConfiguration cfg, Map<String, ConfigMobsDrops> mobs){
		mobs.clear();
		List<Map<?, ?>> configMobs = cfg.getMapList("drops");
		for (Map<?, ?> m : configMobs) {
			ConfigMobsDrops mob = new ConfigMobsDrops();
			mob.id = (String)m.get("id");
			mob.CoinsMax = (Integer)m.get("CoinsMax");
			mob.CoinsMin = (Integer)m.get("CoinsMin");
			mob.modifySpeed =  (Double)m.get("modifySpeed");
			mob.Attack = (Double)m.get("Attack");
			mob.MaxHealth = (Double)m.get("MaxHealth");
			
			List<?> ConfigDrop = (List<?>)m.get("drop");
			if(ConfigDrop != null){
				String[] drop = new String[ConfigDrop.size()/2];
				Double[] dropC = new Double[ConfigDrop.size()/2];	
				int i = 0;
				int j = 0;
				for (Object obj : ConfigDrop) {
					if (obj instanceof Double) {
						dropC[j] = (Double)obj;
						j++;
					}
					if (obj instanceof String) {
						drop[i] = (String)obj;
						i++;
					}
				}
				mob.drop = drop;
				mob.dropC = dropC;
			}
			
			
			mobs.put(mob.id, mob);
		}
		
		CivLog.info("Loaded "+mobs.size()+" MobDrops.");
	}	
}