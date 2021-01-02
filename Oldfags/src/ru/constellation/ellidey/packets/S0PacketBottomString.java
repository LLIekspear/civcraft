package ru.constellation.ellidey.packets;

import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;

public class S0PacketBottomString extends packetAbstractServer {

	
	  public String text = "";

	  public S0PacketBottomString() {}

	
	  public S0PacketBottomString(String text) {
	     this.text = text;
	  }

	
	  @Override
	  public void write(ByteBuffer data) throws BufferOverflowException {
	     writeString(text, data);
	  }

	  @Override
	  public int getSize() {
	     return this.text.getBytes().length;
	  }
	}