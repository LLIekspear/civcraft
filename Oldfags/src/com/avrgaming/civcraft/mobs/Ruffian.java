package com.avrgaming.civcraft.mobs;


import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.util.Vector;

import com.avrgaming.civcraft.config.CivSettings;
import com.avrgaming.civcraft.config.ConfigMobsDrops;
import com.avrgaming.civcraft.mobbase.ICustomMob;
import com.avrgaming.civcraft.mobbase.MobBaseWitch;
import com.avrgaming.civcraft.mobs.MobSpawner.CustomMobLevel;
import com.avrgaming.civcraft.mobs.MobSpawner.CustomMobType;
import com.avrgaming.civcraft.mobs.components.MobComponentDefense;
import com.avrgaming.civcraft.threading.TaskMaster;
import com.avrgaming.civcraft.threading.tasks.FireWorkTask;
import com.avrgaming.civcraft.util.ItemManager;

import net.minecraft.server.v1_8_R3.AxisAlignedBB;
import net.minecraft.server.v1_8_R3.DamageSource;
import net.minecraft.server.v1_8_R3.Entity;
import net.minecraft.server.v1_8_R3.EntityCreature;
import net.minecraft.server.v1_8_R3.EntityHuman;
import net.minecraft.server.v1_8_R3.EntityInsentient;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.IRangedEntity;
import net.minecraft.server.v1_8_R3.PathfinderGoalArrowAttack;
import net.minecraft.server.v1_8_R3.PathfinderGoalFloat;
import net.minecraft.server.v1_8_R3.PathfinderGoalHurtByTarget;
import net.minecraft.server.v1_8_R3.PathfinderGoalLookAtPlayer;
import net.minecraft.server.v1_8_R3.PathfinderGoalNearestAttackableTarget;
import net.minecraft.server.v1_8_R3.PathfinderGoalRandomLookaround;
import net.minecraft.server.v1_8_R3.PathfinderGoalRandomStroll;
import net.minecraft.server.v1_8_R3.PathfinderGoalSelector;

public class Ruffian extends CommonCustomMob implements ICustomMob {
    
    private double damage;
    int attackCount = 0;
    static ConfigMobsDrops Lesser_yobo = CivSettings.mobsDrops.get("Lesser_Ruffian");
    static ConfigMobsDrops Greater_yobo = CivSettings.mobsDrops.get("Greater_Ruffian");
    static ConfigMobsDrops Brutal_yobo = CivSettings.mobsDrops.get("Brutal_Ruffian");
    static ConfigMobsDrops Elite_yobo = CivSettings.mobsDrops.get("Elite_Ruffian");
    
    @Override
    public void onCreate() {
        initLevelAndType();
        
        getGoalSelector().a(1, new PathfinderGoalFloat((EntityInsentient) entity));
        getGoalSelector().a(2, new PathfinderGoalArrowAttack((IRangedEntity) entity, 1.0D, 60, 10.0F));
        getGoalSelector().a(2, new PathfinderGoalRandomStroll((EntityCreature) entity, 1.0D));
        getGoalSelector().a(3, new PathfinderGoalLookAtPlayer((EntityInsentient) entity, EntityHuman.class, 8.0F));
        getGoalSelector().a(3, new PathfinderGoalRandomLookaround((EntityInsentient) entity));
        getTargetSelector().a(1, new PathfinderGoalHurtByTarget((EntityCreature) entity, false));
        getTargetSelector().a(2, new PathfinderGoalNearestAttackableTarget((EntityCreature) entity, EntityHuman.class, true));
        this.setName(this.getLevel().getName() + " " + this.getType().getName());
    }
    
    @Override
    public void onCreateAttributes() {
        MobComponentDefense defense;
        this.setKnockbackResistance(0.5D);
        this.setMovementSpeed(0.2);
        
        switch (this.getLevel()) {
            case LESSER:
                defense = new MobComponentDefense(3.5);
                setMaxHealth(Lesser_yobo.MaxHealth);
                modifySpeed(Lesser_yobo.modifySpeed);
                damage = Lesser_yobo.Attack;
                
                for(int i=0; i< Lesser_yobo.drop.length; i++){
                    this.addDrop(Lesser_yobo.drop[i], Lesser_yobo.dropC[i]);
    			    }
                    this.coinDrop(Lesser_yobo.CoinsMin, Lesser_yobo.CoinsMax);
                
                break;
            case GREATER:
                defense = new MobComponentDefense(10);
                setMaxHealth(Greater_yobo.MaxHealth);
                modifySpeed(Greater_yobo.modifySpeed);
                damage = Greater_yobo.Attack;
                
                for(int i=0; i< Greater_yobo.drop.length; i++){
                    this.addDrop(Greater_yobo.drop[i], Greater_yobo.dropC[i]);
    			    }
                    this.coinDrop(Greater_yobo.CoinsMin, Greater_yobo.CoinsMax);
                
                break;
            case ELITE:
                defense = new MobComponentDefense(16);
                setMaxHealth(Elite_yobo.MaxHealth);
                modifySpeed(Elite_yobo.modifySpeed);
                damage = Elite_yobo.Attack;
                
                for(int i=0; i< Elite_yobo.drop.length; i++){
                    this.addDrop(Elite_yobo.drop[i], Elite_yobo.dropC[i]);
    			    }
                    this.coinDrop(Elite_yobo.CoinsMin, Elite_yobo.CoinsMax);
                
                break;
            case BRUTAL:
                defense = new MobComponentDefense(20);
                setMaxHealth(Brutal_yobo.MaxHealth);
                modifySpeed(Brutal_yobo.modifySpeed);
                damage = Brutal_yobo.Attack;
                
                for(int i=0; i< Brutal_yobo.drop.length; i++){
                    this.addDrop(Brutal_yobo.drop[i], Brutal_yobo.dropC[i]);
    			    }
                    this.coinDrop(Brutal_yobo.CoinsMin, Brutal_yobo.CoinsMax);
                
                break;
            default:
                defense = new MobComponentDefense(2);
                break;
        }
        
        this.addComponent(defense);
    }
    
    @Override
    public void onDamage(EntityCreature e, DamageSource damagesource, PathfinderGoalSelector goalSelector, PathfinderGoalSelector targetSelector) {
    
        //        attackCount++;
        //
        //        if (attackCount % 2 == 0) {
        //            if (damagesource.getEntity().getBukkitEntity().getLocation().getY() - this.entity.getBukkitEntity().getLocation().getY() > 1.25 || this.entity.getBukkitEntity().getLocation().getY() - damagesource.getEntity().getBukkitEntity().getLocation().getY() > 1.25) {
        //                this.entity.getBukkitEntity().teleport((damagesource.getEntity().getBukkitEntity().getLocation().add(0, 0.25, 0)));
        //            }
        //        }
    }
    
    @Override
    public String getBaseEntity() {
        return MobBaseWitch.class.getName();
    }
    
    @Override
    public void onRangedAttack(Entity target) {
        if (!(target instanceof EntityPlayer)) {
            return;
        }
        
        class RuffianProjectile {
            Location loc;
            Location target;
            org.bukkit.entity.Entity attacker;
            int speed = 1;
            double damage;
            int splash = 6;
            
            public RuffianProjectile(Location loc, Location target, org.bukkit.entity.Entity attacker, double damage) {
                this.loc = loc;
                this.target = target;
                this.attacker = attacker;
                this.damage = damage;
            }
            
            public Vector getVectorBetween(Location to, Location from) {
                Vector dir = new Vector();
                
                dir.setX(to.getX() - from.getX());
                dir.setY(to.getY() - from.getY());
                dir.setZ(to.getZ() - from.getZ());
                
                return dir;
            }
            
            public boolean advance() {
                Vector dir = getVectorBetween(target, loc).normalize();
                double distance = loc.distanceSquared(target);
                dir.multiply(speed);
                
                loc.add(dir);
                loc.getWorld().createExplosion(loc, 0.0f, false);
                distance = loc.distanceSquared(target);
                
                if (distance < speed * 1.5) {
                    loc.setX(target.getX());
                    loc.setY(target.getY());
                    loc.setZ(target.getZ());
                    this.onHit();
                    return true;
                }
                
                return false;
            }
            
            public void onHit() {
                int spread = 3;
                int[][] offset = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
                for (int i = 0; i < 4; i++) {
                    int x = offset[i][0] * spread;
                    int y = 0;
                    int z = offset[i][1] * spread;
                    
                    Location location = new Location(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ());
                    location = location.add(x, y, z);
                    
                    launchExplodeFirework(location);
                    //loc.getWorld().createExplosion(location, 1.0f, true);
                    //setFireAt(location, spread);
                }
                
                launchExplodeFirework(loc);
                //loc.getWorld().createExplosion(loc, 1.0f, true);
                damagePlayers(loc, splash);
                //setFireAt(loc, spread);		
            }
            
            @SuppressWarnings("deprecation")
            private void damagePlayers(Location loc, int radius) {
                double x = loc.getX() + 0.5;
                double y = loc.getY() + 0.5;
                double z = loc.getZ() + 0.5;
                double r = (double) radius;
                
                CraftWorld craftWorld = (CraftWorld) attacker.getWorld();
                
                AxisAlignedBB bb = AxisAlignedBB.a(x - r, y - r, z - r, x + r, y + r, z + r);
                
                @SuppressWarnings("unchecked")
                List<Entity> entities = craftWorld.getHandle().getEntities(((CraftEntity) attacker).getHandle(), bb);
                
                for (Entity e : entities) {
                    if (e instanceof EntityPlayer) {
                        EntityDamageByEntityEvent event = new EntityDamageByEntityEvent(attacker, ((EntityPlayer) e).getBukkitEntity(), DamageCause.ENTITY_ATTACK, damage);
                        Bukkit.getServer().getPluginManager().callEvent(event);
                        e.damageEntity(DamageSource.GENERIC, (float) event.getDamage());
                    }
                }
                
            }
            
            
            //			private void setFireAt(Location loc, int radius) {
            //				//Set the entire area on fire.
            //				for (int x = -radius; x < radius; x++) {
            //					for (int y = -3; y < 3; y++) {
            //						for (int z = -radius; z < radius; z++) {
            //							Block block = loc.getWorld().getBlockAt(loc.getBlockX()+x, loc.getBlockY()+y, loc.getBlockZ()+z);
            //							if (ItemManager.getId(block) == CivData.AIR) {
            //								ItemManager.setTypeId(block, CivData.FIRE);
            //								ItemManager.setData(block, 0, true);
            //							}
            //						}
            //					}
            //				}
            //			}
            
            private void launchExplodeFirework(Location loc) {
                FireworkEffect fe = FireworkEffect.builder().withColor(Color.ORANGE).withColor(Color.YELLOW).flicker(true).with(Type.BURST).build();
                TaskMaster.syncTask(new FireWorkTask(fe, loc.getWorld(), loc, 3), 0);
            }
        }
        
        
        class SyncFollow implements Runnable {
            public RuffianProjectile proj;
            
            @Override
            public void run() {
                
                if (proj.advance()) {
                    proj = null;
                    return;
                }
                TaskMaster.syncTask(this, 1);
            }
        }
        
        SyncFollow follow = new SyncFollow();
        follow.proj = new RuffianProjectile(getLocation(entity), getLocation((EntityPlayer) target), this.entity.getBukkitEntity(), damage);
        TaskMaster.syncTask(follow);
    }
    
    public Location getLocation(EntityPlayer p) {
        World world = Bukkit.getWorld(p.world.getWorld().getName());
        Location loc = new Location(world, p.locX, p.locY, p.locZ);
        return loc;
    }
    
    @Override
    public String getClassName() {
        return Ruffian.class.getName();
    }
    
    public static void register() {
    	 setValidBiome(CustomMobType.RUFFIAN, CustomMobLevel.LESSER, Biome.JUNGLE);
 	    setValidBiome(CustomMobType.RUFFIAN, CustomMobLevel.LESSER, Biome.MEGA_TAIGA);
 	    setValidBiome(CustomMobType.RUFFIAN, CustomMobLevel.LESSER, Biome.JUNGLE_EDGE);
 	    setValidBiome(CustomMobType.RUFFIAN, CustomMobLevel.LESSER, Biome.JUNGLE_EDGE_MOUNTAINS);
 	    setValidBiome(CustomMobType.RUFFIAN, CustomMobLevel.LESSER, Biome.SWAMPLAND);


 	    setValidBiome(CustomMobType.RUFFIAN, CustomMobLevel.GREATER, Biome.MEGA_SPRUCE_TAIGA_HILLS);
 	    setValidBiome(CustomMobType.RUFFIAN, CustomMobLevel.GREATER, Biome.MEGA_SPRUCE_TAIGA_HILLS);
 	    setValidBiome(CustomMobType.RUFFIAN, CustomMobLevel.GREATER, Biome.JUNGLE_HILLS);


 	    setValidBiome(CustomMobType.RUFFIAN, CustomMobLevel.ELITE, Biome.BIRCH_FOREST_HILLS_MOUNTAINS);
 	    setValidBiome(CustomMobType.RUFFIAN, CustomMobLevel.ELITE, Biome.ROOFED_FOREST_MOUNTAINS);
 	    setValidBiome(CustomMobType.RUFFIAN, CustomMobLevel.BRUTAL, Biome.JUNGLE_MOUNTAINS);
 	    setValidBiome(CustomMobType.RUFFIAN, CustomMobLevel.BRUTAL, Biome.SWAMPLAND_MOUNTAINS);
    }
}