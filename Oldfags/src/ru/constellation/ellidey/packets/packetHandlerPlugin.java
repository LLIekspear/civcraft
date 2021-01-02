package ru.constellation.ellidey.packets;

import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import com.avrgaming.civcraft.main.CivCraft;


public class packetHandlerPlugin implements PluginMessageListener
{
	   //Нужный нам, экземпляр главного класса
	   private static CivCraft plugin;
	   
	   public packetHandlerPlugin(CivCraft plugin) {
	       this.plugin = plugin;
	   }

	   @Override
	   public void onPluginMessageReceived(String channel, Player player, byte[] bytes)
	   {
//	      if(channel.equals("gui")) {
//
//	          packetAbstract packet; 
//
//	          try{
//	            
//	              packet = packetManager.getPacketFromBytes(bytes);
//
//	            
//	              if (packet instanceof C0PacketGetName)
//	              {
              
//	                 sendPacketToPlayer(player, new S0PacketName(player.getDisplayName()));
//	              }
//	          } catch (Exception e){
//	              e.printStackTrace();
//	          }
//	      }
	   }

	   //Метод, отправляеющий пакет игроку
	  public void sendPacketToPlayer(Player player, packetAbstract packet)
	   {
	       try {
	           //Переводим указанный пакет в байты
	           packetManager packetManager = new packetManager();
	           byte[] bytes = packetManager.getBytesFromPacket(packet);

	           //Отправляем
	           player.sendPluginMessage(plugin, "gui", bytes);
	       } catch (Exception e) {
	           e.printStackTrace();
	       }
	   }
	}
