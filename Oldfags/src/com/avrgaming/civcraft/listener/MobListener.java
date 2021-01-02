package com.avrgaming.civcraft.listener;


import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.PlayerLeashEntityEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

import com.avrgaming.civcraft.main.CivMessage;
import com.avrgaming.civcraft.mobs.AngryYobo;
import com.avrgaming.civcraft.mobs.CommonCustomMob;
import com.avrgaming.civcraft.mobs.Ruffian;
import com.avrgaming.civcraft.mobs.Savage;
import com.avrgaming.civcraft.mobs.Yobo;
import com.avrgaming.civcraft.populators.MobLib;


public class MobListener implements Listener {
    
    @EventHandler(priority = EventPriority.NORMAL)
    public void onChunkLoad(ChunkLoadEvent event) {
        
        for (Entity e : event.getChunk().getEntities()) {
            if (e instanceof Monster) {
                e.remove();
                return;
            }
            
            if (e instanceof Zombie) {
                e.remove();
                return;
            }
            
            if (e instanceof IronGolem) {
                e.remove();
                return;
            }
            
            if (e instanceof PigZombie) {
                e.remove();
                return;
            }
        }
        
    }
    
    @EventHandler(priority = EventPriority.NORMAL)
    public void onChunkUnload(ChunkUnloadEvent event) {
        for (Entity e : event.getChunk().getEntities()) {
            if (e instanceof Yobo) {
                e.remove();
                return;
            }
            if (e instanceof AngryYobo) {
                e.remove();
                return;
            }
            if (e instanceof Savage) {
                e.remove();
                return;
            }
            if (e instanceof Ruffian) {
                e.remove();
                return;
            }
        }
    }
    
    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityTarget(EntityTargetEvent event) {
        if (!(event.getEntity() instanceof LivingEntity)) {
            return;
        }
        
        if (!MobLib.isMobLibEntity((LivingEntity) event.getEntity())) {
            return;
        }
        
        CommonCustomMob mob = CommonCustomMob.getCCM(event.getEntity());
        if (mob != null) {
            mob.onTarget(event);
        }
    }
    
    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof LivingEntity)) {
            return;
        }
        
        if (!MobLib.isMobLibEntity((LivingEntity) event.getEntity())) {
            return;
        }
        
        switch (event.getCause()) {
            case SUFFOCATION:
                Location loc = event.getEntity().getLocation();
                int y = loc.getWorld().getHighestBlockAt(loc.getBlockX(), loc.getBlockZ()).getY() + 4;
                loc.setY(y);
                event.getEntity().teleport(loc);
            case CONTACT:
            case FALL:
            case FIRE:
            case FIRE_TICK:
            case LAVA:
            case MELTING:
            case DROWNING:
            case FALLING_BLOCK:
            case BLOCK_EXPLOSION:
            case ENTITY_EXPLOSION:
            case LIGHTNING:
            case MAGIC:
                event.setCancelled(true);
                break;
            default:
                break;
        }
    }
    
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerLeash(PlayerLeashEntityEvent event) {
        if (event.getEntity() instanceof LivingEntity) {
            if (MobLib.isMobLibEntity((LivingEntity) event.getEntity())) {
                CivMessage.sendError(event.getPlayer(), "This beast cannot be tamed.");
                event.setCancelled(true);
                return;
            }
        }
    }
}
