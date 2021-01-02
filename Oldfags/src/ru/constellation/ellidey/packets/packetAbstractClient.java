package ru.constellation.ellidey.packets;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;

public abstract class packetAbstractClient extends packetAbstract {
	@Override
	public final void write(ByteBuffer data) throws BufferUnderflowException {}

	//При отправке нам необходимо указывать размер отправляемого пакета
	@Override
	public final int getSize(){
	       return 0;
	}
}
