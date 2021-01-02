package com.avrgaming.civcraft.mobbase;

import gpl.NMSUtil;
import net.minecraft.server.v1_8_R3.Entity;
import net.minecraft.server.v1_8_R3.EntityLiving;
import net.minecraft.server.v1_8_R3.EntityWitch;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.World;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.event.CraftEventFactory;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

import java.util.ArrayList;

public class MobBaseWitch extends EntityWitch implements ISpawnable {
    public ICustomMob customMob = null;
    
    public MobBaseWitch(World world) {
        super(world);
        die();
    }
    
    public MobBaseWitch(World world, ICustomMob custom) {
        super(world);
        NMSUtil.clearPathfinderGoals(this.goalSelector);
        NMSUtil.clearPathfinderGoals(this.targetSelector);
        this.customMob = custom;
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
        
        if (!compound.hasKey("customMobClass")) {
            System.out.println("NO CUSTOM CLASS FOUND REMOVING ENTITY.");
            this.world.removeEntity(this);
            return;
        }
        
        try {
            String className = compound.getString("customMobClass");
            Class<?> customClass = Class.forName(className);
            this.customMob = (ICustomMob) customClass.newInstance();
            this.customMob.loadSaveString(compound.getString("customMobData"));
        } catch (Exception e) {
            this.world.removeEntity(this);
            e.printStackTrace();
        }
    }
    
    @Override
    protected void dropDeathLoot(boolean flag, int i) {
        try {
            if (customMob != null) {
                customMob.onDeath(this);
                CraftEventFactory.callEntityDeathEvent(this, new ArrayList<org.bukkit.inventory.ItemStack>());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void a(EntityLiving entityliving, float f) {
        try {
            if (customMob != null) {
                customMob.onRangedAttack(entityliving);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static Entity spawnCustom(Location loc, ICustomMob iCustom) {
        CraftWorld world = (CraftWorld) loc.getWorld();
        World mcWorld = world.getHandle();
        MobBaseWitch witch = new MobBaseWitch(mcWorld, iCustom);
        iCustom.setEntity(witch);
        
        witch.setPosition(loc.getX(), loc.getY(), loc.getZ());
        mcWorld.addEntity(witch, SpawnReason.CUSTOM);
        
        return witch;
    }
    
    @Override
    public ICustomMob getCustomMobInterface() {
        return customMob;
    }
    
}