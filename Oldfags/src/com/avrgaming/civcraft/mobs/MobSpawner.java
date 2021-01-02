package com.avrgaming.civcraft.mobs;



import static com.avrgaming.civcraft.main.CivCraft.civRandom;

import java.util.LinkedList;
import java.util.Random;

import org.bukkit.Location;

import com.avrgaming.civcraft.config.CivSettings;
import com.avrgaming.civcraft.config.ConfigMobs;
import com.avrgaming.civcraft.config.ConfigMobsDrops;
import com.avrgaming.civcraft.exception.CivException;
import com.avrgaming.civcraft.mobbase.ICustomMob;
import com.avrgaming.civcraft.populators.MobLib;
import com.avrgaming.civcraft.util.CivColor;



public class MobSpawner {
	static ConfigMobs yobo = CivSettings.mobs.get("yobo");
	static ConfigMobs behemoth = CivSettings.mobs.get("Behemoth");
	static ConfigMobs savage = CivSettings.mobs.get("Savage");
	static ConfigMobs ruffian = CivSettings.mobs.get("Ruffian");
	static ConfigMobs angry = CivSettings.mobs.get("angry");
	static ConfigMobs lesser = CivSettings.mobs.get("Lesser");
	static ConfigMobs greater = CivSettings.mobs.get("Greater");
	static ConfigMobs elite = CivSettings.mobs.get("Elite");
	static ConfigMobs brutal = CivSettings.mobs.get("Brutal");
	static ConfigMobs extra = CivSettings.mobs.get("Extra");
	static ConfigMobs Super = CivSettings.mobs.get("Super");
	

	
	
    public enum CustomMobLevel {
        LESSER(lesser.name),
        GREATER(greater.name),
        ELITE(elite.name),
        BRUTAL(brutal.name),
        EXTRA(extra.name),
        SUPER(Super.name);
        
        private final String name;
        
        CustomMobLevel(String name) {
            this.name = name;
        }
        
        public String getName() {
            return name;
        }
    }
    
    public enum CustomMobType {
        YOBO(yobo.name, yobo.mobclass),
        ANGRYYOBO(angry.name, angry.mobclass),
        BEHEMOTH(behemoth.name, behemoth.mobclass),
        SAVAGE(savage.name, savage.mobclass),
        RUFFIAN(ruffian.name, ruffian.mobclass);
        
        
        private final String name;
        private final String className;
        
        CustomMobType(String name, String className) {
            this.name = name;
            this.className = className;
        }
        
        public String getName() {
            return name;
        }
        
        public String getClassName() {
            return className;
        }
        
        public boolean equalsIgnoreCase() {
            return false;
        }
    }
    
    public static void register() {
   
        Yobo.register();
        Behemoth.register();
        Savage.register();
        Ruffian.register();
    }
    
    public static void despawnAll() {
        CommonCustomMob.customMobs.values().forEach((mob) -> {
            mob.entity.getBukkitEntity().remove();
        });
    }
    
    public static CommonCustomMob spawnCustomMob(CustomMobType type, CustomMobLevel level, Location loc) throws CivException {
        
        ICustomMob custom = MobLib.spawnCustom(type.className, loc);
        if (custom == null) {
            throw new CivException("Couldn't spawn custom mob type:" + type.toString());
        }
        
        custom.setData("type", type.toString().toUpperCase());
        custom.setData("level", level.toString().toUpperCase());
        
        custom.onCreate();
        custom.onCreateAttributes();
        
        CommonCustomMob common = (CommonCustomMob) custom;
        CommonCustomMob.customMobs.put(common.entity.getUniqueID(), common);
        return common;
    }
    
    public static void spawnRandomCustomMob(Location location) {
        LinkedList<TypeLevel> validMobs = CommonCustomMob.getValidMobsForBiome(location.getBlock().getBiome());
        if (validMobs.isEmpty()) {
            return;
        }
        
        LinkedList<TypeLevel> removeUs = new LinkedList<>();
        validMobs.stream().filter((v) -> (CommonCustomMob.disabledMobs.contains(v.type.toString()))).forEachOrdered((v) -> {
            removeUs.add(v);
        });
        
        validMobs.removeAll(removeUs);
    
        Random random = civRandom;
        int idx = random.nextInt(validMobs.size());
        
        CustomMobType type = validMobs.get(idx).type;
        CustomMobLevel level = validMobs.get(idx).level;
        
        try {
            spawnCustomMob(type, level, location);
        } catch (CivException e) {
            e.printStackTrace();
        }
    }
}