package com.avrgaming.civcraft.config;

import java.util.List;
import java.util.Map;

import org.bukkit.configuration.file.FileConfiguration;

import com.avrgaming.civcraft.main.CivLog;

public class ConfigMobs {
	public String id;
	public String name;
	public String mobclass;
	
	public static void loadConfig(FileConfiguration cfg, Map<String, ConfigMobs> mobs){
		mobs.clear();
		List<Map<?, ?>> configMobs = cfg.getMapList("mobs");
		for (Map<?, ?> m : configMobs) {
			ConfigMobs mob = new ConfigMobs();
			mob.id = (String)m.get("id");
			mob.name = (String)m.get("name");
			mob.mobclass =(String)m.get("class");
			
			mobs.put(mob.id, mob);
		}
		
		CivLog.info("Loaded "+mobs.size()+" Mobs.");
	}	
}