package com.gportal.a2s.messages;

import static com.gportal.a2s.Message.readString;
import static com.gportal.a2s.Message.writeString;

import java.net.InetSocketAddress;

import com.gportal.a2s.Query;

import io.netty.buffer.ByteBuf;

public record InfoQuery(InetSocketAddress remoteAddress, String payload, Integer challenge) implements Query {
	public static final byte OP = 0x54;

	public InfoQuery withChallenge(int challenge) { return new InfoQuery(remoteAddress(), payload(), challenge); }

	public static InfoQuery read(InetSocketAddress remoteAddress, ByteBuf buffer) {
		String payload = readString(buffer);
		Integer challenge = buffer.readableBytes()>0?buffer.readIntLE():null;
		return new InfoQuery(remoteAddress, payload, challenge);
	}
	public InfoQuery write(ByteBuf buffer) {
		buffer.writeByte(OP);
		writeString(buffer, payload());
		if(challenge() != null) buffer.writeIntLE(challenge());
		return this;
	}
}
