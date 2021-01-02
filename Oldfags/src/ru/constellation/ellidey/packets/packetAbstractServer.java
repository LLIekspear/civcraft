package ru.constellation.ellidey.packets;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;

public abstract class packetAbstractServer extends packetAbstract {

	@Override
	public void read(ByteBuffer data) throws BufferUnderflowException {}

}
