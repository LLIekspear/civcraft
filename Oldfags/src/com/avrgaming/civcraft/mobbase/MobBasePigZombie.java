package com.avrgaming.civcraft.mobbase;


import gpl.NMSUtil;
import net.minecraft.server.v1_8_R3.DamageSource;
import net.minecraft.server.v1_8_R3.Entity;
import net.minecraft.server.v1_8_R3.EntityHuman;
import net.minecraft.server.v1_8_R3.EntityPigZombie;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.World;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.event.CraftEventFactory;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

import java.util.ArrayList;

public class MobBasePigZombie extends EntityPigZombie implements ISpawnable {
    public ICustomMob customMob = null;
    
    // private static final UUID bq = UUID.fromString("49455A49-7EC5-45BA-B886-3B90B23A1718");
    // private static final UUID bq = UUID.fromString("B9766B59-9566-4402-BC1F-2EE2A276D836");
    
    public MobBasePigZombie(World world) {
        super(world);
        die();
    }
    
    public MobBasePigZombie(World world, ICustomMob customMob) {
        super(world);
        NMSUtil.clearPathfinderGoals(this.goalSelector);
        NMSUtil.clearPathfinderGoals(this.targetSelector);
        this.customMob = customMob;
    }
    
    /* Setting and loading custom NBT data. */
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
    
    
    public boolean bk() {
        return false;
    }
    
    @Override
    public boolean damageEntity(DamageSource damagesource, float f) {
        try {
            if (!super.damageEntity(damagesource, f)) {
                return false;
            }
            
            if (customMob != null) {
                customMob.onDamage(this, damagesource,
                    this.goalSelector, this.targetSelector);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }
    
    protected Entity findTarget() {
        EntityHuman entityhuman = this.world.findNearbyPlayer(this, 3.0D);
        //this.n(arg0)
        return (entityhuman != null) && (hasLineOfSight(entityhuman)) ? entityhuman : null;
    }
    
    protected void getRareDrop(int i) {
        return;
    }
    
    public static Entity spawnCustom(Location loc, ICustomMob iCustom) {
        CraftWorld world = (CraftWorld) loc.getWorld();
        World mcWorld = world.getHandle();
        MobBasePigZombie pigzombie = new MobBasePigZombie(mcWorld, iCustom);
        iCustom.setEntity(pigzombie);
        
        pigzombie.setPosition(loc.getX(), loc.getY(), loc.getZ());
        mcWorld.addEntity(pigzombie, SpawnReason.CUSTOM);
        
        return pigzombie;
    }
    
    public void e() {
        try {
            super.E();
            if (customMob != null) {
                customMob.onTick();
            } else {
                System.out.println("Ticking without custom  Mob..");
                this.world.removeEntity(this);
            }
        } catch (Exception e) {
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
    public ICustomMob getCustomMobInterface() {
        return customMob;
    }
}