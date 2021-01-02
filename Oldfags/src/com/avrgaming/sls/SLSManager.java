package com.avrgaming.sls;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.UUID;

import org.bukkit.Bukkit;

import com.avrgaming.civcraft.config.CivSettings;
import com.avrgaming.civcraft.exception.CivException;
import com.avrgaming.civcraft.exception.InvalidConfiguration;
import com.avrgaming.civcraft.main.CivLog;
import com.avrgaming.civcraft.threading.TaskMaster;
import com.avrgaming.civcraft.util.TimeTools;

public class SLSManager implements Runnable {

	public static String serverName;
	public static String serverDescription;
	public static String serverAddress;
	public static String serverURL;
	public static String serverURLDescription;
	public static String serverTimezone;
	public static String gen_id;
	
	public static void init() throws CivException, InvalidConfiguration {
		String useListing = CivSettings.getStringBase("use_server_listing_service");
		
		if (useListing == null) {
			return;
		}
		
		if (!useListing.equalsIgnoreCase("true")) {
			return;
		}
		
		serverName = CivSettings.getStringBase("server_name");
		if (serverName.contains(";")) {
			throw new CivException("Cannot have a server name with a ';' in it.");
		}
		if (serverName.contains("<") && serverName.contains(">")) {
			serverName = serverName.replaceAll("[<>]", "");
		}
		
		serverDescription = CivSettings.getStringBase("server_description");
		if (serverDescription.contains(";")) {
			throw new CivException("Cannot have a server description with a ';' in it.");
		}
		if (serverDescription.contains("<") && serverDescription.contains(">")) {
			serverDescription = serverDescription.replaceAll("[<>]", "");
		}
		
		serverAddress = CivSettings.getStringBase("server_address");
		if (serverAddress.contains(";")) {
			throw new CivException("Cannot have a server address with a ';' in it.");
		}
		if (serverAddress.contains("<") && serverAddress.contains(">")) {
			serverAddress = serverAddress.replaceAll("[<>]", "");
		}
		
		serverURL = CivSettings.getStringBase("server_url");
		if (serverURL.contains(";")) {
			throw new CivException("Cannot have a server url with a ';' in it.");
		}
		
		serverURLDescription = CivSettings.getStringBase("server_url_description");
		if (serverURLDescription.contains(";")) {
			throw new CivException("Cannot have a server url description with a ';' in it.");
		}
		
		serverTimezone = CivSettings.getStringBase("server_timezone");
		if (serverTimezone.contains(";")) {
			throw new CivException("Cannot have a server timezone with a ';' in it.");
		}
		
		
		gen_id = CivSettings.getGenID();
		if (gen_id == null) {
			UUID uid = UUID.randomUUID();
			gen_id = uid.toString();
			CivSettings.saveGenID(gen_id);
		}
		
	
		TaskMaster.asyncTimer("SLS", new SLSManager(), TimeTools.toTicks(60));
	}
	
	public static String getParsedVersion() {
		String version = Bukkit.getVersion();
		//version = version.split("MC: ")[1].split("\\)")[0];
		return version;
	}
	
	public static void sendHeartbeat() {
		try {
			InetAddress address = InetAddress.getByName("atlas.civcraft.net");
//			String message = gen_id+";<a href=\""+serverURL+"\" title=\""+serverURLDescription+"\" target=\"_blank\" style=\"color: #0012FF\"><strong>"+serverName+"</strong></a>;<strong>"+serverDescription+"</strong>;<strong>"+serverTimezone+"</strong>;<a href=\""+serverURL+"\" title=\""+serverURLDescription+"\" target=\"_blank\" style=\"color: #0012FF\"><strong>"+serverAddress+"</strong></a>;"+

			String message = gen_id+";"+serverName+";"+serverDescription+";"+serverTimezone+";"+serverAddress+";"+
					Bukkit.getOnlinePlayers().size()+";"+Bukkit.getMaxPlayers()+";"+getParsedVersion();
			
			try {
				if (CivSettings.getStringBase("debug_heartbeat").equalsIgnoreCase("true")) {
					CivLog.info("SLS HEARTBEAT:"+message);
				}
			} catch (InvalidConfiguration e1) {
			}
			
			DatagramPacket packet = new DatagramPacket(message.getBytes(), message.toCharArray().length, address, 25580);
			DatagramSocket socket;
			try {
				socket = new DatagramSocket();
				socket.send(packet);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		} catch (UnknownHostException e) {
			CivLog.error("Couldn't IP address to SLS service. If you're on a LAN with no internet access, disable SLS in the CivCraft config.");
			//e.printStackTrace();
		}
	}


	@Override
	public void run() {
		SLSManager.sendHeartbeat();
	}
	
}
