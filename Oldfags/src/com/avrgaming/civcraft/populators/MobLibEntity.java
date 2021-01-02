package com.avrgaming.civcraft.populators;

import net.minecraft.server.v1_8_R3.Entity;

import java.util.UUID;

public class MobLibEntity {
    
    private UUID uid;
    private Entity entity;
    
    public MobLibEntity(UUID uid, Entity entity) {
        this.uid = uid;
        this.entity = entity;
    }
    
    public Entity getEntity() {
        return entity;
    }
    
    public void setEntity(Entity entity) {
        this.entity = entity;
    }
    
    public UUID getUid() {
        return uid;
    }
    
    public void setUid(UUID uid) {
        this.uid = uid;
    }
    
}
