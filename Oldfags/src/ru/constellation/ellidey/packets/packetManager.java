package ru.constellation.ellidey.packets;

import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.HashMap;

public class packetManager {

	//Коллекция дискриминаторов
	   private static HashMap<Class<? extends packetAbstract>, Byte> discriminators;
	   //Коллекция классов-пакетов
	   private static HashMap<Byte, Class<? extends packetAbstract>> classes;

	   public packetManager()
	   {
	       //Инициализируем нужные переменные
	       discriminators = new HashMap<Class<? extends packetAbstract>, Byte>();
	       classes = new HashMap<Byte, Class<? extends packetAbstract>>();

	       //Добавляем дискриминаторы для наших пакетов
	       initDiscriminators();
	   }

	   private static void initDiscriminators()
	   {
	       //Система схожа с форджевской, только метод addDiscriminator() придется делать самим :с
	       for (enumPacket type : enumPacket.values())
	       {
	           addDiscriminator((byte) type.ordinal(), type.getPacketClass());
	       }
	   }

	   //Метод для добавления новых дискриминаторов
	   public static void addDiscriminator(byte discriminator, Class<? extends packetAbstract> clazz)
	   {
	       //Проверяем, не зарегистрирован ли уже указанный пакет
	       if (!discriminators.containsKey(clazz) && !classes.containsKey(discriminator))
	       {
	           //Добавляем информацию о пакете и даем ему дискриминатор
	           discriminators.put(clazz, discriminator);
	           classes.put(discriminator, clazz);
	       }
	   }

	   //Метод для получения дискриминатора
	   public static byte getDiscriminator(Class<? extends packetAbstract> clazz)
	   {
	       return discriminators.get(clazz);
	   }

	   //Метод для получения класса пакета по дискриминатору
	   public static Class<? extends packetAbstract> getDiscriminatorClass(byte discriminator)
	   {
	       return classes.get(discriminator);
	   }

	   //Метод опрделеяет какой пакет был получен в виде байтов и возвращает его
	   public static packetAbstract getPacketFromBytes(byte[] bytes) throws BufferUnderflowException, InstantiationException, IllegalAccessException
	   {
	       ByteBuffer data = ByteBuffer.wrap(bytes).asReadOnlyBuffer();
	       byte discriminator = data.get();

	       Class<? extends packetAbstract> packetClass = getDiscriminatorClass(discriminator);
	       packetAbstract packet = packetClass.newInstance();

	       packet.read(data);

	       return packet;
	   }

	   //Метод записывает указанный пакет в байты для отправки
	   public static byte[] getBytesFromPacket(packetAbstract packet) throws BufferOverflowException
	   {
	       byte discriminator = getDiscriminator(packet.getClass());

	       ByteBuffer buffer = ByteBuffer.allocate(packet.getSize() + Byte.SIZE);

	       buffer.put(discriminator);
	       packet.write(buffer);

	       return buffer.array();
	   }
	}