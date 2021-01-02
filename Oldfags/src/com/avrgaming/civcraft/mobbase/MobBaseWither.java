package com.avrgaming.civcraft.mobbase;

import gpl.NMSUtil;
import net.minecraft.server.v1_8_R3.Entity;
import net.minecraft.server.v1_8_R3.EntityLiving;
import net.minecraft.server.v1_8_R3.EntityWither;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.World;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

public class MobBaseWither extends EntityWither implements ISpawnable {
    public ICustomMob customMob = null;
    
    public MobBaseWither(World world) {
        super(world);
        die();
    }
    
    public MobBaseWither(World world, ICustomMob customMob) {
        super(world);
        this.customMob = customMob;
        NMSUtil.clearPathfinderGoals(this.goalSelector);
        NMSUtil.clearPathfinderGoals(this.targetSelector);
    }
    
    @Override
    public void b(NBTTagCompound compound) {
        super.b(compound);
        compound.setString("customMobClass", this.customMob.getClassName());
        compound.setString("customMobData", this.customMob.getSaveString());
    }
    
    @Override
    public void a(NBTTagCompound compound) {
        super.a(compound);
        
        try {
            String className = compound.getString("customMobClass");
            Class<?> customClass = Class.forName(className);
            this.customMob = (ICustomMob) customClass.newInstance();
            this.customMob.loadSaveString(compound.getString("customMobData"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void a(EntityLiving entityliving, float f) {
        
    }
    
    public void e() {
    }
    
    public static Entity spawnCustom(Location loc, ICustomMob iCustom) {
        CraftWorld world = (CraftWorld) loc.getWorld();
        World mcWorld = world.getHandle();
        MobBaseWither pigzombie = new MobBaseWither(mcWorld, iCustom);
        iCustom.setEntity(pigzombie);
        
        pigzombie.setPosition(loc.getX(), loc.getY(), loc.getZ());
        mcWorld.addEntity(pigzombie, SpawnReason.CUSTOM);
        
        return pigzombie;
    }
    
    @Override
    public ICustomMob getCustomMobInterface() {
        return customMob;
    }
    
}