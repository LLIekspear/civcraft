package com.avrgaming.civcraft.threading.timers;



import static com.avrgaming.civcraft.main.CivGlobal.nextClearLag;
import static com.avrgaming.civcraft.main.CivGlobal.nextClearLagMobs;

import java.util.Calendar;
import java.util.LinkedList;

import org.bukkit.Bukkit;

import com.avrgaming.civcraft.main.CivLog;
import com.avrgaming.civcraft.main.CivMessage;
import com.avrgaming.civcraft.mobs.CommonCustomMob;
import com.avrgaming.civcraft.util.CivColor;

/**
 * Created by TheLegion on 27.07.2017.
 */

public class ClearLagCheck implements Runnable {
    
    @Override
    public void run() {
        //        CivLog.warning(nextClearLag + " " + nextClearLagMobs + " " + Calendar.getInstance().getTimeInMillis());
        if (nextClearLag - Calendar.getInstance().getTimeInMillis() - 1000 * 60 <= (long) 1000 * 60 &&
            nextClearLag - Calendar.getInstance().getTimeInMillis() > (long) 1) {
        	CivMessage.clearlag(CivColor.RedBold + "Внимание! " + CivColor.Rose + "Через минуту будет удалён весь мусор, лежащий на земле, а также враждебные мобы.");
        }
        
        if (nextClearLagMobs - Calendar.getInstance().getTimeInMillis() - 1000 * 60 <= (long) 1000 * 60 &&
            nextClearLagMobs - Calendar.getInstance().getTimeInMillis() > (long) 1) {
        	CivMessage.clearlag(CivColor.RedBold + "Внимание! " + CivColor.Rose + "Через минуту будут удалены все враждебные мобы.");
        }
        
        if (nextClearLag - Calendar.getInstance().getTimeInMillis() <= (long) 1) {
            removeMobsAndDrops();
            CivMessage.clearlag(CivColor.LightGreen + "Весь мусор, лежащий на земле, а также враждебные мобы были удалены.");
            nextClearLag = Calendar.getInstance().getTimeInMillis() + 1000 * 60 * 15; // Ровно 15 минут (это - сейчас) тут + 1 не нужен, ибо лагать не должно на сервере
        }
        
        if (nextClearLagMobs - Calendar.getInstance().getTimeInMillis() <= (long) 1) {
            removeCustomMob("yobo");
            removeCustomMob("angryyobo");
            removeCustomMob("ruffian");
            removeCustomMob("savage");
            removeCustomMob("behemoth");
            CivMessage.clearlag(CivColor.LightGreen + "Все враждебные мобы удалены.");
            nextClearLagMobs = Calendar.getInstance().getTimeInMillis() + 1000 * 60 * 60; // Ровно час (это - сейчас) тут + 1 не нужен, ибо лагать не должно на сервере
        }
        
        //        CivLog.warning(nextClearLag + " " + nextClearLagMobs + " " + Calendar.getInstance().getTimeInMillis());
    }
    
    public static void removeMobsAndDrops() {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "killall mobs world");
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "killall drops world");
    }
    
    public static void removeCustomMob(String type) {
        String name = type;
        
        LinkedList<CommonCustomMob> removeUs = new LinkedList<>();
        CommonCustomMob.customMobs.values().stream().filter((mob) -> (mob.getType().toString().equalsIgnoreCase(name))).forEachOrdered((mob) -> {
            removeUs.add(mob);
        });
        
        int count = 0;
        
        count = removeUs.stream().map((mob) -> {
            CommonCustomMob.customMobs.remove(mob.entity.getUniqueID());
            return mob;
        }).map((mob) -> {
            mob.entity.getBukkitEntity().remove();
            return mob;
        }).map((_item) -> 1).reduce(count, Integer::sum);
        CivLog.debug("Удалено " + count + " мобов типа " + name);
    }
}
