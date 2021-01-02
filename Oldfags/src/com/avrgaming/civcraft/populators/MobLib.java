package com.avrgaming.civcraft.populators;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftLivingEntity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

import com.avrgaming.civcraft.mobbase.ICustomMob;
import com.avrgaming.civcraft.mobbase.ISpawnable;
import com.avrgaming.civcraft.mobbase.MobBaseIronGolem;
import com.avrgaming.civcraft.mobbase.MobBasePigZombie;
import com.avrgaming.civcraft.mobbase.MobBaseWitch;
import com.avrgaming.civcraft.mobbase.MobBaseWither;
import com.avrgaming.civcraft.mobbase.MobBaseZombie;
import com.avrgaming.civcraft.mobbase.MobBaseZombieGiant;

import net.minecraft.server.v1_8_R3.Entity;
import net.minecraft.server.v1_8_R3.EntityGiantZombie;
import net.minecraft.server.v1_8_R3.EntityInsentient;
import net.minecraft.server.v1_8_R3.EntityIronGolem;
import net.minecraft.server.v1_8_R3.EntityLiving;
import net.minecraft.server.v1_8_R3.EntityPigZombie;
import net.minecraft.server.v1_8_R3.EntityTypes;
import net.minecraft.server.v1_8_R3.EntityWitch;
import net.minecraft.server.v1_8_R3.EntityWither;
import net.minecraft.server.v1_8_R3.EntityZombie;

public class MobLib {
    
    private static final HashMap<UUID, MobLibEntity> entities = new HashMap<UUID, MobLibEntity>();
    
    public static boolean isMobLibEntity(LivingEntity e) {
        
        EntityLiving handle = ((CraftLivingEntity) e).getHandle();
        return handle instanceof ISpawnable;
    
    }
    
    public static void registerEntity(CustomEntityType type) {
        CustomEntityType.registerEntity(type);
    }
    
    public static void registerAllEntities() {
        CustomEntityType.registerEntities();
    }
    
    public static ICustomMob spawnCustom(String customMob, Location loc) {
        try {
            Class<?> customClass = Class.forName(customMob);
            ICustomMob iCustom = (ICustomMob) customClass.newInstance();
            
            String base = iCustom.getBaseEntity();
            if (base == null) {
                System.out.println("ERROR: no base entity set up for " + customMob);
                return null;
            }
            
            Class<?> baseClass = Class.forName(iCustom.getBaseEntity());
            Method spawnMethod = baseClass.getMethod("spawnCustom", Location.class, ICustomMob.class);
            spawnMethod.invoke(null, loc, iCustom);
            
            return iCustom;
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
            NoSuchMethodException | SecurityException | IllegalArgumentException |
            InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
        
    }
    
    public static MobLibEntity createNamedEntity(String customMob, Location loc, String name) {
        Method spawnMethod;
        try {
            Class<?> customClass = Class.forName(customMob);
            ICustomMob iCustom = (ICustomMob) customClass.newInstance();
            
            String base = iCustom.getBaseEntity();
            if (base == null) {
                System.out.println("ERROR: no base entity set up for " + customMob);
                return null;
            }
            
            Class<?> baseClass = Class.forName(iCustom.getBaseEntity());
            spawnMethod = baseClass.getMethod("spawn", Location.class, ICustomMob.class, String.class);
            Entity entity = (Entity) spawnMethod.invoke(null, loc, iCustom, name);
            MobLibEntity mobEntity = new MobLibEntity(entity.getUniqueID(), entity);
            entities.put(mobEntity.getUid(), mobEntity);
            return mobEntity;
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | ClassNotFoundException | InstantiationException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    
    public enum CustomEntityType {
        PIG_ZOMBIE("PigZombie", 57, EntityType.PIG_ZOMBIE, EntityPigZombie.class, MobBasePigZombie.class),
        ZOMBIE("Zombie", 54, EntityType.ZOMBIE, EntityZombie.class, MobBaseZombie.class),
        ZOMBIE_GIANT("Giant", 53, EntityType.GIANT, EntityGiantZombie.class, MobBaseZombieGiant.class),
        WITCH("Witch", 66, EntityType.WITCH, EntityWitch.class, MobBaseWitch.class),
        IRON_GOLEM("VillagerGolem", 99, EntityType.IRON_GOLEM, EntityIronGolem.class, MobBaseIronGolem.class),
        WITHER("WitherBoss", 64, EntityType.WITHER, EntityWither.class, MobBaseWither.class),
        CAVE_SPIDER("CaveSpider", 99, EntityType.CAVE_SPIDER, EntityIronGolem.class, MobBaseIronGolem.class);
        
        private final String name;
        private final int id;
        private final Class<? extends EntityInsentient> customClass;
        
        CustomEntityType(String name, int id, EntityType entityType, Class<? extends EntityInsentient> nmsClass, Class<? extends EntityInsentient> customClass) {
            this.name = name;
            this.id = id;
            this.customClass = customClass;
        }
        
        public String getName() {
            return this.name;
        }
        
        public int getID() {
            return this.id;
        }
        
        public Class<? extends EntityInsentient> getCustomClass() {
            return this.customClass;
        }
        
        public static void registerEntities() {
            for (CustomEntityType entity : values()) {
                a(entity.getCustomClass(), entity.getName(), entity.getID());
            }
        }
        
        public static void registerEntity(CustomEntityType type) {
            a(type.getCustomClass(), type.getName(), type.getID());
        }
        
        @SuppressWarnings("rawtypes")
        public static Object getPrivateStatic(Class clazz, String f) throws Exception {
            Field field = clazz.getDeclaredField(f);
            field.setAccessible(true);
            return field.get(null);
        }
        
        @SuppressWarnings({"unchecked", "rawtypes"})
        private static void a(Class paramClass, String paramString, int paramInt) {
            try {
                ((Map) getPrivateStatic(EntityTypes.class, "c")).put(paramString, paramClass);
                ((Map) getPrivateStatic(EntityTypes.class, "d")).put(paramClass, paramString);
                
                ((Map) getPrivateStatic(EntityTypes.class, "e")).put(paramInt, paramClass);
                ((Map) getPrivateStatic(EntityTypes.class, "f")).put(paramClass, paramInt);
                ((Map) getPrivateStatic(EntityTypes.class, "g")).put(paramString, paramInt);
            } catch (Exception exc) {
            }
        }
        
    }
    
    
    public static void removeEntity(UUID id) {
        MobLibEntity mobEntity = entities.get(id);
        if (mobEntity != null) {
            mobEntity.getEntity().world.removeEntity(mobEntity.getEntity());
        }
    }
}
