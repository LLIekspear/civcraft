package ru.constellation.ellidey.packets;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public class S0PacketRightMessage extends packetAbstractServer {

	
	  public String text[] = null;

	  public S0PacketRightMessage() {}

	
	  public S0PacketRightMessage(String[] text) {
	     this.text = text;
	  }

	
	  @Override
	  public void write(ByteBuffer data) throws BufferOverflowException {
	     writeStrings(text, data);
	  }

	  @Override
	  public int getSize() {
		 String string = "";
		 for(String s : this.text){
			 string = string + s + "<>";
		 }
		 return string.getBytes().length;
	  }
	}