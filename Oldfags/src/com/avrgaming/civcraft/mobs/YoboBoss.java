package com.avrgaming.civcraft.mobs;

import java.util.LinkedList;

import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTargetEvent.TargetReason;

import com.avrgaming.civcraft.config.CivSettings;
import com.avrgaming.civcraft.exception.CivException;
import com.avrgaming.civcraft.main.CivLog;
import com.avrgaming.civcraft.main.CivMessage;
import com.avrgaming.civcraft.mobbase.ICustomMob;
import com.avrgaming.civcraft.mobbase.MobBaseZombie;
import com.avrgaming.civcraft.mobs.components.MobComponentDefense;
import com.avrgaming.civcraft.util.CivColor;

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

public class YoboBoss extends CommonCustomMob implements ICustomMob {
    
    private String entityType = MobBaseZombie.class.getName();
    private boolean angry = false;
    
    LinkedList<Entity> minions = new LinkedList<Entity>();
    
    @Override
    public void onCreate() {
        initLevelAndType();
        getGoalSelector().a(7, new PathfinderGoalRandomStroll((EntityCreature) entity, 1.0D));
        getGoalSelector().a(8, new PathfinderGoalLookAtPlayer((EntityInsentient) entity, EntityHuman.class, 8.0F));
        getTargetSelector().a(1, new PathfinderGoalHurtByTarget((EntityCreature) entity, true));
        this.setName(this.getLevel().getName() + " " + this.getType().getName());
        
        CivMessage.global(CivColor.Blue + CivSettings.localize.localizedString("event_handler_boss_spawned"));
    }
    
    @Override
    public void onCreateAttributes() {
        MobComponentDefense defense;
        this.setKnockbackResistance(0.99);
        switch (this.getLevel()) {
            case BRUTAL:
                defense = new MobComponentDefense(20);
                setMaxHealth(1250.0);
                this.setAttack(40.0);
                
                this.addDrop("mat_mercury", 0.05);
                this.addDrop("mat_mercury_bath", 0.01);
                
                this.addDrop("mat_iron_helmet", 0.005);
                this.addDrop("mat_iron_chestplate", 0.005);
                this.addDrop("mat_iron_leggings", 0.005);
                this.addDrop("mat_iron_boots", 0.005);
                this.addDrop("mat_leather_boots", 0.005);
                this.addDrop("mat_leather_leggings", 0.005);
                this.addDrop("mat_leather_chestplate", 0.005);
                this.addDrop("mat_leather_helmet", 0.005);
                this.addDrop("mat_iron_sword", 0.005);
                this.addDrop("mat_hunting_bow", 0.005);
                
                this.addDrop("mat_refined_leather_helmet", 0.001);
                this.addDrop("mat_refined_leather_chestplate", 0.001);
                this.addDrop("mat_refined_leather_leggings", 0.001);
                this.addDrop("mat_refined_leather_boots", 0.001);
                this.addDrop("mat_steel_helmet", 0.001);
                this.addDrop("mat_steel_boots", 0.001);
                this.addDrop("mat_steel_chestplate", 0.001);
                this.addDrop("mat_steel_leggings", 0.001);
                this.addDrop("mat_steel_sword", 0.001);
                this.addDrop("mat_recurve_bow", 0.001);
                
                this.addDrop("mat_carbide_steel_helmet", 0.0005);
                this.addDrop("mat_carbide_steel_chestplate", 0.0005);
                this.addDrop("mat_carbide_steel_leggings", 0.0005);
                this.addDrop("mat_carbide_steel_boots", 0.0005);
                this.addDrop("mat_hardened_leather_boots", 0.0005);
                this.addDrop("mat_hardened_leather_leggings", 0.0005);
                this.addDrop("mat_hardened_leather_chestplate", 0.0005);
                this.addDrop("mat_hardened_leather_helmet", 0.0005);
                this.addDrop("mat_carbide_steel_sword", 0.0005);
                this.addDrop("mat_longbow", 0.0005);
                
                this.addDrop("mat_tungsten_helmet", 0.0001);
                this.addDrop("mat_tungsten_leggings", 0.0001);
                this.addDrop("mat_tungsten_chestplate", 0.0001);
                this.addDrop("mat_tungsten_boots", 0.0001);
                this.addDrop("mat_composite_leather_boots", 0.0001);
                this.addDrop("mat_composite_leather_leggings", 0.0001);
                this.addDrop("mat_composite_leather_chestplate", 0.0001);
                this.addDrop("mat_composite_leather_helmet", 0.0001);
                this.addDrop("mat_tungsten_sword", 0.0001);
                this.addDrop("mat_marksmen_bow", 0.0001);
                
                this.coinDrop(15000, 30000);
                
                break;
            default:
                defense = new MobComponentDefense(20);
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
            goalSelector.a(2, new PathfinderGoalMeleeAttack(e, EntityHuman.class, 1.0D, false));
            for (int i = 0; i < 1; i++) {
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
        return YoboBoss.class.getName();
    }
    
    public static void register() {
        //		    setValidBiome(CustomMobType.YOBOBOSS, CustomMobLevel.BRUTAL, Biome.HELL);
        //		    setValidBiome(CustomMobType.YOBOBOSS, CustomMobLevel.BRUTAL, Biome.SKY);
    }
    
    @Override
    public void onTarget(EntityTargetEvent event) {
        super.onTarget(event);
        
        if (event.getReason().equals(TargetReason.FORGOT_TARGET) ||
            event.getReason().equals(TargetReason.TARGET_DIED)) {
            this.angry = false;
            for (Entity e : minions) {
                e.getBukkitEntity().remove();
            }
        }
    }
    
    
    public void onDeath(EntityCreature arg0, EntityCreature e, DamageSource damagesource, PathfinderGoalSelector goalSelector, PathfinderGoalSelector targetSelector, EntityDamageByEntityEvent event) {
        CivMessage.global(CivColor.Blue + CivSettings.localize.localizedString("event_handler_boss_killed"));
        
        this.setLevel(MobSpawner.CustomMobLevel.EXTRA);
        
        goalSelector.a(2, new PathfinderGoalMeleeAttack(e, EntityHuman.class, 1.0D, false));
        for (int i = 0; i < 1; i++) {
            try {
                this.minions.add(MobSpawner.spawnCustomMob(MobSpawner.CustomMobType.ANGRYYOBO, this.getLevel(), getLocation(e)).entity);
            } catch (CivException e1) {
                e1.printStackTrace();
            }
        }
    }
}          