package com.avrgaming.civcraft.mobbase;

import net.minecraft.server.v1_8_R3.DamageSource;
import net.minecraft.server.v1_8_R3.Entity;
import net.minecraft.server.v1_8_R3.EntityCreature;
import net.minecraft.server.v1_8_R3.EntityLiving;
import net.minecraft.server.v1_8_R3.PathfinderGoalSelector;

public interface ICustomMob {
    
    void onCreate();
    
    void onCreateAttributes();
    
    void onTick();
    
    String getBaseEntity();
    
    void onDamage(EntityCreature e, DamageSource source,
                  PathfinderGoalSelector goalSelector, PathfinderGoalSelector targetSelector);
    
    void onDeath(EntityCreature e);
    
    void onRangedAttack(Entity target);
    
    void setData(String key, String value);
    
    String getData(String key);
    
    void setEntity(EntityLiving e);
    
    String getSaveString();
    
    void loadSaveString(String str);
    
    String getClassName();
}
