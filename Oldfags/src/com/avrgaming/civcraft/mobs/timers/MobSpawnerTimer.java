package com.avrgaming.civcraft.mobs.timers;

import static com.avrgaming.civcraft.main.CivCraft.civRandom;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.avrgaming.civcraft.exception.CivException;
import com.avrgaming.civcraft.main.CivData;
import com.avrgaming.civcraft.main.CivGlobal;
import com.avrgaming.civcraft.mobs.MobSpawner;
import com.avrgaming.civcraft.object.TownChunk;
import com.avrgaming.civcraft.util.ChunkCoord;
import com.avrgaming.civcraft.util.EntityProximity;
import com.avrgaming.civcraft.util.ItemManager;

import net.minecraft.server.v1_8_R3.EntityCreature;

public class MobSpawnerTimer implements Runnable {
    
    public static int UPDATE_LIMIT = 20;
    public static int MOB_AREA_LIMIT = 12;
    public static int MOB_AREA = 32;
    
    public static int MIN_SPAWN_DISTANCE = 30
    		;
    public static int MAX_SPAWN_DISTANCE = 60;
    
    public static int Y_SHIFT = 3;
    
    
    public static Queue<String> playerQueue = new LinkedList<String>();
    
    @Override
    public void run() {
        String name = null;
        
        for (int i = 0; i < UPDATE_LIMIT; i++) {
            try {
                name = playerQueue.poll();
                if (name == null) {
                    return;
                }
                
                Player player = CivGlobal.getPlayer(name);
                World world = player.getWorld();
                if (!world.getAllowMonsters()) {
                    continue;
                }
    
                for (int j = 0; j < getMinSpawnAmount(Bukkit.getOnlinePlayers().size()); j++) {
                    Random random = civRandom;
                    int x = random.nextInt(MAX_SPAWN_DISTANCE) + MIN_SPAWN_DISTANCE;
                    if (random.nextBoolean()) {
                        x *= -1;
                    }
                    
                    int z = random.nextInt(MAX_SPAWN_DISTANCE) + MIN_SPAWN_DISTANCE;
                    if (random.nextBoolean()) {
                        z *= -1;
                    }
                    
                    int y = world.getHighestBlockYAt(((Double) player.getLocation().getX()).intValue() + x, ((Double) player.getLocation().getZ()).intValue() + z);
                    Location loc = new Location(world, player.getLocation().getX() + x, y, player.getLocation().getZ() + z);
                    if (!loc.getChunk().isLoaded()) {
                        continue;
                    }
                    
                    TownChunk tc = CivGlobal.getTownChunk(new ChunkCoord(loc));
                    if (tc != null) {
                        continue;
                    }
                    
                    if ((ItemManager.getId(loc.getBlock().getRelative(BlockFace.DOWN)) == CivData.WATER) ||
                        (ItemManager.getId(loc.getBlock().getRelative(BlockFace.DOWN)) == CivData.WATER_RUNNING) ||
                        (ItemManager.getId(loc.getBlock().getRelative(BlockFace.DOWN)) == CivData.LAVA) ||
                        (ItemManager.getId(loc.getBlock().getRelative(BlockFace.DOWN)) == CivData.LAVA_RUNNING)) {
                        continue;
                    }
                    
                    loc.setY(loc.getY() + Y_SHIFT);
                    LinkedList<Entity> entities = EntityProximity.getNearbyEntities(null, loc, MOB_AREA, EntityCreature.class);
                    if (entities.size() > MOB_AREA_LIMIT) {
                        continue;
                    }
                    MobSpawner.spawnRandomCustomMob(loc);
                }
                break;
            } catch (CivException e) {
            } finally {
                if (name != null) {
                    playerQueue.add(name);
                }
            }
        }
    }
    
    public int getMinSpawnAmount(int onlinePlayers) {
        if (onlinePlayers > 75) {
            return 60;
        } else if (onlinePlayers < 75 && onlinePlayers > 50) {
            return 45;
        } else if (onlinePlayers < 50 && onlinePlayers > 25) {
            return 30;
        }
    
        return 15;
    }
}