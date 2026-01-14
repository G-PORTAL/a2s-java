package com.gportal.source.query;

import java.net.InetSocketAddress;

import io.netty.buffer.ByteBuf;

public interface Message {
	public InetSocketAddress remoteAddress();
	public Message write(ByteBuf buffer);

	public static String readString(ByteBuf buffer) {
		String val = "";
		byte in;
		while((in = buffer.readByte()) != 0) val += (char) in;
		return val;
	}
	public static void writeString(ByteBuf buffer, String val) {
		buffer.writeBytes(val.getBytes());
		buffer.writeByte(0);
	}
}
