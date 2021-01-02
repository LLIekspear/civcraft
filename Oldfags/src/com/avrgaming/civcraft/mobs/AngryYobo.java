package com.avrgaming.civcraft.mobs;

import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTargetEvent.TargetReason;

import com.avrgaming.civcraft.config.CivSettings;
import com.avrgaming.civcraft.config.ConfigMobsDrops;
import com.avrgaming.civcraft.mobbase.ICustomMob;
import com.avrgaming.civcraft.mobbase.MobBaseZombie;
import com.avrgaming.civcraft.mobs.components.MobComponentDefense;

import net.minecraft.server.v1_8_R3.DamageSource;
import net.minecraft.server.v1_8_R3.Entity;
import net.minecraft.server.v1_8_R3.EntityCreature;
import net.minecraft.server.v1_8_R3.EntityHuman;
import net.minecraft.server.v1_8_R3.EntityInsentient;
import net.minecraft.server.v1_8_R3.PathfinderGoalFloat;
import net.minecraft.server.v1_8_R3.PathfinderGoalLookAtPlayer;
import net.minecraft.server.v1_8_R3.PathfinderGoalMeleeAttack;
import net.minecraft.server.v1_8_R3.PathfinderGoalNearestAttackableTarget;
import net.minecraft.server.v1_8_R3.PathfinderGoalSelector;

public class AngryYobo extends CommonCustomMob implements ICustomMob {
    

    static ConfigMobsDrops Lesser_yobo = CivSettings.mobsDrops.get("Lesser_angry");
    static ConfigMobsDrops Greater_yobo = CivSettings.mobsDrops.get("Greater_angry");
    static ConfigMobsDrops Brutal_yobo = CivSettings.mobsDrops.get("Brutal_angry");
    static ConfigMobsDrops Elite_yobo = CivSettings.mobsDrops.get("Elite_angry");
    @Override
    public void onCreate() {
        initLevelAndType();
        getGoalSelector().a(0, new PathfinderGoalFloat((EntityInsentient) entity));
        getGoalSelector().a(2, new PathfinderGoalMeleeAttack((EntityCreature) entity, EntityHuman.class, 1.0D, false));
        getGoalSelector().a(8, new PathfinderGoalLookAtPlayer((EntityInsentient) entity, EntityHuman.class, 8.0F));
        getTargetSelector().a(2, new PathfinderGoalNearestAttackableTarget<EntityHuman>((EntityCreature) entity, EntityHuman.class, true));
        this.setName(this.getLevel().getName() + " " + this.getType().getName());
        MobBaseZombie zombie = ((MobBaseZombie) this.entity);
        zombie.setBaby(true);
    }
    
    @Override
    public void onTick() {
        super.onTick();
    }
    
    @Override
    public String getBaseEntity() {
        return MobBaseZombie.class.getName();
    }
    
    @Override
    public void onDamage(EntityCreature e, DamageSource damagesource, PathfinderGoalSelector goalSelector, PathfinderGoalSelector targetSelector) {
        //
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
    public void onRangedAttack(Entity target) {
    }
    
    @Override
    public String getClassName() {
        return AngryYobo.class.getName();
    }
    
    @Override
    public void onTarget(EntityTargetEvent event) {
        super.onTarget(event);
        
        if (event.getReason().equals(TargetReason.FORGOT_TARGET) ||
            event.getReason().equals(TargetReason.TARGET_DIED)) {
            event.getEntity().remove();
        }
    }
}