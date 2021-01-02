package com.avrgaming.civcraft.mobs;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Scanner;

import org.bukkit.block.Biome;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTargetEvent.TargetReason;

import com.avrgaming.civcraft.config.CivSettings;
import com.avrgaming.civcraft.config.ConfigMobsDrops;
import com.avrgaming.civcraft.exception.CivException;
import com.avrgaming.civcraft.main.CivLog;
import com.avrgaming.civcraft.mobbase.ICustomMob;
import com.avrgaming.civcraft.mobbase.MobBaseZombie;
import com.avrgaming.civcraft.mobs.MobSpawner.CustomMobLevel;
import com.avrgaming.civcraft.mobs.MobSpawner.CustomMobType;
import com.avrgaming.civcraft.mobs.components.MobComponentDefense;

import net.minecraft.server.v1_8_R3.DamageSource;
import net.minecraft.server.v1_8_R3.Entity;
import net.minecraft.server.v1_8_R3.EntityCreature;
import net.minecraft.server.v1_8_R3.EntityDamageSource;
import net.minecraft.server.v1_8_R3.EntityHuman;
import net.minecraft.server.v1_8_R3.EntityInsentient;
import net.minecraft.server.v1_8_R3.PathfinderGoalHurtByTarget;
import net.minecraft.server.v1_8_R3.PathfinderGoalLookAtPlayer;
import net.minecraft.server.v1_8_R3.PathfinderGoalMeleeAttack;
import net.minecraft.server.v1_8_R3.PathfinderGoalRandomStroll;
import net.minecraft.server.v1_8_R3.PathfinderGoalSelector;

public class Yobo extends CommonCustomMob implements ICustomMob {
    
    private final String entityType = MobBaseZombie.class.getName();
    private boolean angry = false;

    static ConfigMobsDrops Lesser_yobo = CivSettings.mobsDrops.get("Lesser_yobo");
    static ConfigMobsDrops Greater_yobo = CivSettings.mobsDrops.get("Greater_yobo");
    static ConfigMobsDrops Brutal_yobo = CivSettings.mobsDrops.get("Brutal_yobo");
    static ConfigMobsDrops Elite_yobo = CivSettings.mobsDrops.get("Elite_yobo");

    
    LinkedList<Entity> minions = new LinkedList<Entity>();
    
    @Override
    public void onCreate() {
        initLevelAndType();
        getGoalSelector().a(7, new PathfinderGoalRandomStroll((EntityCreature) entity, 1.0D));
        getGoalSelector().a(8, new PathfinderGoalLookAtPlayer((EntityInsentient) entity, EntityHuman.class, 8.0F));
        getTargetSelector().a(1, new PathfinderGoalHurtByTarget((EntityCreature) entity, true));
        this.setName(this.getLevel().getName() + " " + this.getType().getName());
    }
    
    @Override
    public void onCreateAttributes() {
        MobComponentDefense defense;
        this.setKnockbackResistance(0.99);
        switch (this.getLevel()) {
            case LESSER:
                defense = new MobComponentDefense(3.5);
                setMaxHealth(Lesser_yobo.MaxHealth);
                this.setAttack(Lesser_yobo.Attack);
                modifySpeed(Lesser_yobo.modifySpeed);
                
                for(int i=0; i< Lesser_yobo.drop.length; i++){
                this.addDrop(Lesser_yobo.drop[i], Lesser_yobo.dropC[i]);
			    }
                this.coinDrop(Lesser_yobo.CoinsMin, Lesser_yobo.CoinsMax);
                
                break;
            
            case GREATER:
                defense = new MobComponentDefense(10);
                setMaxHealth(Greater_yobo.MaxHealth);
                this.setAttack(Greater_yobo.Attack);
                modifySpeed(Greater_yobo.modifySpeed);
                
                for(int i=0; i< Greater_yobo.drop.length; i++){
                    this.addDrop(Greater_yobo.drop[i], Greater_yobo.dropC[i]);
    		        }
                
                
                this.coinDrop(Greater_yobo.CoinsMin, Greater_yobo.CoinsMax);
                
                break;
            
            case ELITE:
                defense = new MobComponentDefense(16);
                setMaxHealth(Elite_yobo.MaxHealth);
                this.setAttack(Elite_yobo.Attack);
                modifySpeed(Elite_yobo.modifySpeed);
                
                for(int i=0; i< Elite_yobo.drop.length; i++){
                    this.addDrop(Elite_yobo.drop[i], Elite_yobo.dropC[i]);
    		        }
                
                
                this.coinDrop(Elite_yobo.CoinsMin, Elite_yobo.CoinsMax);
                
                break;
            
            case BRUTAL:
                defense = new MobComponentDefense(16);
                setMaxHealth(Brutal_yobo.MaxHealth);
                this.setAttack(Brutal_yobo.Attack);
                modifySpeed(Brutal_yobo.modifySpeed);
                
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
    public String getBaseEntity() {
        return entityType;
    }
    
    @Override
    public void onDamage(EntityCreature e, DamageSource damagesource, PathfinderGoalSelector goalSelector, PathfinderGoalSelector targetSelector) {
        if (!(damagesource instanceof EntityDamageSource)) {
            return;
        }
    
        //        attackCount++;
        //
        //        if (attackCount % 2 == 0) {
        //            if (damagesource.getEntity().getBukkitEntity().getLocation().getY() - this.entity.getBukkitEntity().getLocation().getY() > 1.25 || this.entity.getBukkitEntity().getLocation().getY() - damagesource.getEntity().getBukkitEntity().getLocation().getY() > 1.25) {
        //                this.entity.getBukkitEntity().teleport((damagesource.getEntity().getBukkitEntity().getLocation().add(0, 0.25, 0)));
        //            }
        //        }
        //
        if (this.getLevel() == null) {
            this.setLevel(MobSpawner.CustomMobLevel.valueOf(getData("level")));
            if (this.getLevel() == null) {
                try {
                    throw new CivException("Level was null after retry.");
                } catch (CivException e2) {
                    CivLog.error("getData(level):" + getData("level"));
                    e2.printStackTrace();
                }
            }
        }
        
        if (!angry) {
            angry = true;
            goalSelector.a(2, new PathfinderGoalMeleeAttack(e, EntityHuman.class, 1.0D, false));
            for (int i = 0; i < 4; i++) {
                try {
                    this.minions.add(MobSpawner.spawnCustomMob(MobSpawner.CustomMobType.ANGRYYOBO, this.getLevel(), getLocation(e)).entity);
                } catch (CivException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }
    
    @Override
    public String getClassName() {
        return Yobo.class.getName();
    }
    
    public static void register() {
	    setValidBiome(CustomMobType.YOBO, CustomMobLevel.LESSER, Biome.PLAINS);
	    setValidBiome(CustomMobType.YOBO, CustomMobLevel.LESSER, Biome.FOREST);
	    setValidBiome(CustomMobType.YOBO, CustomMobLevel.LESSER, Biome.BIRCH_FOREST);
	    setValidBiome(CustomMobType.YOBO, CustomMobLevel.LESSER, Biome.BIRCH_FOREST_HILLS);
	
	    setValidBiome(CustomMobType.YOBO, CustomMobLevel.GREATER, Biome.SUNFLOWER_PLAINS);
	    setValidBiome(CustomMobType.YOBO, CustomMobLevel.GREATER, Biome.FLOWER_FOREST);
	    setValidBiome(CustomMobType.YOBO, CustomMobLevel.GREATER, Biome.BIRCH_FOREST_HILLS_MOUNTAINS);
	    setValidBiome(CustomMobType.YOBO, CustomMobLevel.GREATER, Biome.BIRCH_FOREST_MOUNTAINS);
	    setValidBiome(CustomMobType.YOBO, CustomMobLevel.GREATER, Biome.FOREST_HILLS);

	
	    setValidBiome(CustomMobType.YOBO, CustomMobLevel.ELITE, Biome.EXTREME_HILLS);
	    setValidBiome(CustomMobType.YOBO, CustomMobLevel.ELITE, Biome.EXTREME_HILLS_PLUS);
	    setValidBiome(CustomMobType.YOBO, CustomMobLevel.ELITE, Biome.ROOFED_FOREST);
	    setValidBiome(CustomMobType.YOBO, CustomMobLevel.ELITE, Biome.ROOFED_FOREST_MOUNTAINS);


	    setValidBiome(CustomMobType.YOBO, CustomMobLevel.BRUTAL, Biome.MEGA_SPRUCE_TAIGA_HILLS);
	    setValidBiome(CustomMobType.YOBO, CustomMobLevel.BRUTAL, Biome.EXTREME_HILLS_MOUNTAINS);
	    setValidBiome(CustomMobType.YOBO, CustomMobLevel.BRUTAL, Biome.EXTREME_HILLS_PLUS_MOUNTAINS);
    }
    
    @Override
    public void onTarget(EntityTargetEvent event) {
        super.onTarget(event);
        
        if (event.getReason().equals(TargetReason.FORGOT_TARGET) ||
            event.getReason().equals(TargetReason.TARGET_DIED)) {
            for (Entity e : minions) {
                e.getBukkitEntity().remove();
            }
        }
    }
}