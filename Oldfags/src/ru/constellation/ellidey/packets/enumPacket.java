package ru.constellation.ellidey.packets;

public enum enumPacket {

	//Клиентские пакеты
	  BOTTOM_STRING(S0PacketBottomString.class),
	  LEFT_RIGHT_MESSAGE(S0PacketRightMessage.class),
	  
	   //Серверные пакеты
	  GETBOTTOM_STRING(C0PacketBottomString.class), 
	  GETLEFT_RIGHT_MESSAGE(C0PackeRightMessage.class);

	  private Class<? extends packetAbstract> packetClass;
	  private enumPacket(Class<? extends packetAbstract> packetClass) { this.packetClass = packetClass; }
	  public Class<? extends packetAbstract> getPacketClass() { return packetClass; }
	}