package com.avrgaming.civcraft.mobbase;

import net.minecraft.server.v1_8_R3.EntityLiving;
import net.minecraft.server.v1_8_R3.EntityWitherSkull;
import net.minecraft.server.v1_8_R3.MovingObjectPosition;
import net.minecraft.server.v1_8_R3.World;

public class MobBaseWitherSkull extends EntityWitherSkull {
    
    public MobBaseWitherSkull(World world) {
        super(world);
    }
    
    public MobBaseWitherSkull(World world, EntityLiving entityliving, double d0, double d1, double d2) {
        super(world, entityliving, d0, d1, d2);
    }
    
    @Override
    protected void a(MovingObjectPosition movingobjectposition) {
        
    }
    
}
