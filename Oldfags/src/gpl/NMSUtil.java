package gpl;

import net.minecraft.server.v1_8_R3.PathfinderGoalSelector;

import org.bukkit.craftbukkit.v1_8_R3.util.UnsafeList;

import java.lang.reflect.Field;

public class NMSUtil {
    
    @SuppressWarnings("rawtypes")
    public static void clearPathfinderGoals(PathfinderGoalSelector selector) {
        
        Field gsa;
        try {
            gsa = net.minecraft.server.v1_8_R3.PathfinderGoalSelector.class.getDeclaredField("b");
            gsa.setAccessible(true);
            gsa.set(selector, new UnsafeList());
            gsa = net.minecraft.server.v1_8_R3.PathfinderGoalSelector.class.getDeclaredField("c");
            gsa.setAccessible(true);
            gsa.set(selector, new UnsafeList());
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
