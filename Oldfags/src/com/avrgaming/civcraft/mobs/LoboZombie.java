package com.avrgaming.civcraft.mobs;

import com.avrgaming.civcraft.mobbase.ICustomMob;
import com.avrgaming.civcraft.mobbase.MobBaseZombie;

import net.minecraft.server.v1_8_R3.DamageSource;
import net.minecraft.server.v1_8_R3.Entity;
import net.minecraft.server.v1_8_R3.EntityCreature;
import net.minecraft.server.v1_8_R3.PathfinderGoalSelector;


public class LoboZombie extends CommonCustomMob implements ICustomMob {
    
    @Override
    public void onCreate() {
        
    }
    
    @Override
    public void onTick() {
        
    }
    
    @Override
    public String getBaseEntity() {
        return MobBaseZombie.class.getName();
    }
    
    @Override
    public void onDamage(EntityCreature e, DamageSource damagesource, PathfinderGoalSelector goalSelector, PathfinderGoalSelector targetSelector) {
        
    }
    
    @Override
    public void onDeath(EntityCreature e) {
        
    }
    
    @Override
    public void onCreateAttributes() {
    }
    
    @Override
    public void onRangedAttack(Entity target) {
        
    }
    
    @Override
    public String getClassName() {
        return LoboZombie.class.getName();
    }
    
}
