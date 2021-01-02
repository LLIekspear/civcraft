package ru.constellation.ellidey;

import org.bukkit.plugin.java.JavaPlugin;
import com.google.common.util.concurrent.ExecutionError;

public class log {
	private static JavaPlugin plugin;
	
	public static void init(JavaPlugin plugin){
		log.plugin = plugin;	
	}
	
	
	
	public static void heading(String title) {
		plugin.getLogger().info("========= "+title+" =========");
	}
	
	public static void info(String message) {
		plugin.getLogger().info(message);
	}
	
	public static void debug(String message) {
		plugin.getLogger().info("[DEBUG] "+message);
	}

	public static void warning(String message) {
		if (message == null) {
			try {
				throw new Exception("Null warning message!");
			} catch (Exception e){
				e.printStackTrace();
			}
		}
		
		plugin.getLogger().info("[WARNING] "+message);
		
	}

	public static void error(String message) {
		plugin.getLogger().severe(message);
	}
}
