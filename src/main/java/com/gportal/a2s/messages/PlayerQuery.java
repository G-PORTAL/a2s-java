package com.gportal.a2s.messages;

import java.net.InetSocketAddress;

import com.gportal.a2s.Query;

import io.netty.buffer.ByteBuf;

public record PlayerQuery(InetSocketAddress remoteAddress, Integer challenge) implements Query {
	public static final byte OP = 0x55;

	public PlayerQuery withChallenge(int challenge) { return new PlayerQuery(remoteAddress(), challenge); }

	public static PlayerQuery read(InetSocketAddress remoteAddress, ByteBuf buffer) {
		Integer challenge = buffer.readableBytes()>=4?buffer.readIntLE():null;
		if(challenge!=null && challenge==0) challenge = null;
		return new PlayerQuery(remoteAddress, challenge);
	}
	public PlayerQuery write(ByteBuf buffer) {
		buffer.writeByte(OP);
		buffer.writeIntLE(challenge()!=null?challenge():-1);
		return this;
	}
}
