package com.avrgaming.civcraft.mobs;

import org.bukkit.block.Biome;

import com.avrgaming.civcraft.config.CivSettings;
import com.avrgaming.civcraft.config.ConfigMobsDrops;
import com.avrgaming.civcraft.mobbase.ICustomMob;
import com.avrgaming.civcraft.mobbase.MobBasePigZombie;
import com.avrgaming.civcraft.mobs.MobSpawner.CustomMobLevel;
import com.avrgaming.civcraft.mobs.MobSpawner.CustomMobType;
import com.avrgaming.civcraft.mobs.components.MobComponentDefense;

import net.minecraft.server.v1_8_R3.DamageSource;
import net.minecraft.server.v1_8_R3.EntityCreature;
import net.minecraft.server.v1_8_R3.EntityHuman;
import net.minecraft.server.v1_8_R3.EntityInsentient;
import net.minecraft.server.v1_8_R3.PathfinderGoalFloat;
import net.minecraft.server.v1_8_R3.PathfinderGoalLookAtPlayer;
import net.minecraft.server.v1_8_R3.PathfinderGoalMeleeAttack;
import net.minecraft.server.v1_8_R3.PathfinderGoalNearestAttackableTarget;
import net.minecraft.server.v1_8_R3.PathfinderGoalSelector;

public class Savage extends CommonCustomMob implements ICustomMob {
    
    public static int TARGET_DISTANCE = 64;
    int attackCount = 0;
    static ConfigMobsDrops Lesser_yobo = CivSettings.mobsDrops.get("Lesser_Savage");
    static ConfigMobsDrops Greater_yobo = CivSettings.mobsDrops.get("Greater_Savage");
    static ConfigMobsDrops Brutal_yobo = CivSettings.mobsDrops.get("Brutal_Savage");
    static ConfigMobsDrops Elite_yobo = CivSettings.mobsDrops.get("Elite_Savage");
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
    public void onCreate() {
        initLevelAndType();
        
        getGoalSelector().a(0, new PathfinderGoalFloat((EntityInsentient) entity));
        getGoalSelector().a(2, new PathfinderGoalMeleeAttack((EntityCreature) entity, EntityHuman.class, 1.0D, false));
        getGoalSelector().a(8, new PathfinderGoalLookAtPlayer((EntityInsentient) entity, EntityHuman.class, 8.0F));
        getTargetSelector().a(2, new PathfinderGoalNearestAttackableTarget<>((EntityCreature) entity, EntityHuman.class, true));
        
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
        return MobBasePigZombie.class.getName();
    }
    
    @Override
    public String getClassName() {
        return Savage.class.getName();
    }
    
    public static void register() {
    	  setValidBiome(CustomMobType.SAVAGE, CustomMobLevel.LESSER, Biome.DESERT);
  	    setValidBiome(CustomMobType.SAVAGE, CustomMobLevel.LESSER, Biome.DESERT_HILLS);
  	    setValidBiome(CustomMobType.SAVAGE, CustomMobLevel.LESSER, Biome.DESERT_MOUNTAINS);
  	
  	    setValidBiome(CustomMobType.SAVAGE, CustomMobLevel.GREATER, Biome.SAVANNA);
  	    setValidBiome(CustomMobType.SAVAGE, CustomMobLevel.GREATER, Biome.SAVANNA_MOUNTAINS);
  	    setValidBiome(CustomMobType.SAVAGE, CustomMobLevel.GREATER, Biome.SAVANNA_PLATEAU);
  	    setValidBiome(CustomMobType.SAVAGE, CustomMobLevel.GREATER, Biome.SAVANNA_PLATEAU_MOUNTAINS);

  	    setValidBiome(CustomMobType.SAVAGE, CustomMobLevel.ELITE, Biome.MESA);
  	    setValidBiome(CustomMobType.SAVAGE, CustomMobLevel.ELITE, Biome.MESA_PLATEAU);
  	    setValidBiome(CustomMobType.SAVAGE, CustomMobLevel.ELITE, Biome.MEGA_TAIGA);
  	    setValidBiome(CustomMobType.SAVAGE, CustomMobLevel.ELITE, Biome.MEGA_SPRUCE_TAIGA);
  		

  	    setValidBiome(CustomMobType.SAVAGE, CustomMobLevel.BRUTAL, Biome.MESA_BRYCE);
  	    setValidBiome(CustomMobType.SAVAGE, CustomMobLevel.ELITE, Biome.MESA_PLATEAU_FOREST);
  	    setValidBiome(CustomMobType.SAVAGE, CustomMobLevel.BRUTAL, Biome.MESA_PLATEAU_MOUNTAINS);
  	    setValidBiome(CustomMobType.SAVAGE, CustomMobLevel.BRUTAL, Biome.MESA_PLATEAU_FOREST_MOUNTAINS);
  	    setValidBiome(CustomMobType.SAVAGE, CustomMobLevel.ELITE, Biome.MEGA_SPRUCE_TAIGA_HILLS);
  	    setValidBiome(CustomMobType.SAVAGE, CustomMobLevel.ELITE, Biome.MEGA_TAIGA_HILLS);
    }
    
        /* Экстра метод =( аналог этого метода уже есть в MobBasePigZombie
        @Override
	public void onTarget(EntityTargetEvent event) {
		Location current = getLocation((EntityCreature) entity);
		
		if (event.getTarget() != null && current.distance(event.getTarget().getLocation()) > TARGET_DISTANCE) {
			event.setCancelled(true);
		}
		
		super.onTarget(event);
	} */
}
