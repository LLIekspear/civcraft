package com.avrgaming.civcraft.mobs;

import org.bukkit.block.Biome;

import com.avrgaming.civcraft.config.CivSettings;
import com.avrgaming.civcraft.config.ConfigMobsDrops;
import com.avrgaming.civcraft.mobbase.ICustomMob;
import com.avrgaming.civcraft.mobbase.MobBaseIronGolem;
import com.avrgaming.civcraft.mobs.MobSpawner.CustomMobLevel;
import com.avrgaming.civcraft.mobs.MobSpawner.CustomMobType;
import com.avrgaming.civcraft.mobs.components.MobComponentDefense;

import net.minecraft.server.v1_8_R3.DamageSource;
import net.minecraft.server.v1_8_R3.EntityCreature;
import net.minecraft.server.v1_8_R3.EntityHuman;
import net.minecraft.server.v1_8_R3.EntityInsentient;
import net.minecraft.server.v1_8_R3.PathfinderGoalLookAtPlayer;
import net.minecraft.server.v1_8_R3.PathfinderGoalMeleeAttack;
import net.minecraft.server.v1_8_R3.PathfinderGoalNearestAttackableTarget;
import net.minecraft.server.v1_8_R3.PathfinderGoalRandomStroll;
import net.minecraft.server.v1_8_R3.PathfinderGoalSelector;

public class Behemoth extends CommonCustomMob implements ICustomMob {
    
    int attackCount = 0;
    static ConfigMobsDrops Lesser_yobo = CivSettings.mobsDrops.get("Lesser_Behemoth");
    static ConfigMobsDrops Greater_yobo = CivSettings.mobsDrops.get("Greater_Behemoth");
    static ConfigMobsDrops Brutal_yobo = CivSettings.mobsDrops.get("Brutal_Behemoth");
    static ConfigMobsDrops Elite_yobo = CivSettings.mobsDrops.get("Elite_Behemoth");

    @Override
    public void onCreate() {
        initLevelAndType();
        getGoalSelector().a(7, new PathfinderGoalRandomStroll((EntityCreature) entity, 1.0D));
        getGoalSelector().a(8, new PathfinderGoalLookAtPlayer((EntityInsentient) entity, EntityHuman.class, 8.0F));
        getGoalSelector().a(2, new PathfinderGoalMeleeAttack((EntityCreature) entity, EntityHuman.class, 1.0D, false));
        getTargetSelector().a(2, new PathfinderGoalNearestAttackableTarget((EntityCreature) entity, EntityHuman.class, true));
        this.setName(this.getLevel().getName() + " " + this.getType().getName());
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
                defense = new MobComponentDefense(20);
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
        return MobBaseIronGolem.class.getName();
    }
    
    @Override
    public String getClassName() {
        return Behemoth.class.getName();
    }
    
    public static void register() {
        setValidBiome(CustomMobType.BEHEMOTH, CustomMobLevel.LESSER, Biome.FROZEN_RIVER);
        setValidBiome(CustomMobType.BEHEMOTH, CustomMobLevel.LESSER, Biome.FROZEN_OCEAN);
        setValidBiome(CustomMobType.BEHEMOTH, CustomMobLevel.LESSER, Biome.COLD_BEACH);
        setValidBiome(CustomMobType.BEHEMOTH, CustomMobLevel.LESSER, Biome.COLD_TAIGA);
        setValidBiome(CustomMobType.BEHEMOTH, CustomMobLevel.LESSER, Biome.MEGA_TAIGA);
        
        setValidBiome(CustomMobType.BEHEMOTH, CustomMobLevel.GREATER, Biome.COLD_TAIGA_HILLS);
        setValidBiome(CustomMobType.BEHEMOTH, CustomMobLevel.GREATER, Biome.COLD_TAIGA_MOUNTAINS);
        setValidBiome(CustomMobType.BEHEMOTH, CustomMobLevel.GREATER, Biome.ICE_MOUNTAINS);
        setValidBiome(CustomMobType.BEHEMOTH, CustomMobLevel.GREATER, Biome.SWAMPLAND);
        
        setValidBiome(CustomMobType.BEHEMOTH, CustomMobLevel.ELITE, Biome.ICE_PLAINS);
        setValidBiome(CustomMobType.BEHEMOTH, CustomMobLevel.ELITE, Biome.MEGA_SPRUCE_TAIGA_HILLS);
        
        setValidBiome(CustomMobType.BEHEMOTH, CustomMobLevel.BRUTAL, Biome.ICE_PLAINS_SPIKES);
        setValidBiome(CustomMobType.BEHEMOTH, CustomMobLevel.BRUTAL, Biome.JUNGLE_MOUNTAINS);
    }
}
