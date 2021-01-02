package com.avrgaming.civcraft.config;

import java.util.List;
import java.util.Map;

import org.bukkit.configuration.file.FileConfiguration;

import com.avrgaming.civcraft.main.CivLog;



public class ConfigMineLevel {
    public int level;	/* Current level number */
    public int amount; /* Number of redstone this mine consumes */
    public int count; /* Number of times that consumes must be met to level up */
    public double hammers; /* hammers generated each time hour */
    
    public static void loadConfig(FileConfiguration cfg, Map<Integer, ConfigMineLevel> levels) {
        levels.clear();
        List<Map<?, ?>> mine_levels = cfg.getMapList("mine_levels");
        for (Map<?, ?> level : mine_levels) {
            ConfigMineLevel mine_level = new ConfigMineLevel();
            mine_level.level = (Integer) level.get("level");
            mine_level.amount = (Integer) level.get("amount");
            mine_level.hammers = (Double) level.get("hammers");
            mine_level.count = (Integer) level.get("count");
            levels.put(mine_level.level, mine_level);
        }
        CivLog.info("Loaded " + levels.size() + " mine levels.");
    }
}