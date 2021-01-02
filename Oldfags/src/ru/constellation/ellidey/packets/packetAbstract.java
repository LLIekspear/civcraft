package ru.constellation.ellidey.packets;


import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.ArrayList;


public abstract class packetAbstract {
	   
	
	   public abstract void write(ByteBuffer data) throws BufferOverflowException;

	   public abstract void read(ByteBuffer data) throws BufferUnderflowException;

	   //Отвечает за настройку размера пакета
	   public abstract int getSize();

	   public static void writeString(String string, ByteBuffer data) throws BufferOverflowException{
	       byte[] stringBytes = string.getBytes();
	       data.putInt(stringBytes.length);
	       data.put(stringBytes);
	   }
	   
	   public static void writeStrings(String[] strings, ByteBuffer data) throws BufferOverflowException{
		   String string = "";
		   
		   for(String s : strings){
		   string = string + s + "<>";
		   }
		   	
		    byte[] stringBytes = string.getBytes();
			data.putInt(stringBytes.length);
			data.put(stringBytes);
	   }

	   public static String readString(ByteBuffer data) throws BufferUnderflowException{
	       int length = data.getInt();
	       byte[] stringBytes = new byte[length];
	       data.get(stringBytes);

	       return new String(stringBytes);
	   }
	   
	   public static String[] readStrings(ByteBuffer data) throws BufferUnderflowException{
		   int length = data.getInt();
		   byte[] stringBytes = new byte[length];
		   data.get(stringBytes);
		   String string = new String(stringBytes);
		   String[] strings = string.split("<>");
		   return strings;
	   }
}