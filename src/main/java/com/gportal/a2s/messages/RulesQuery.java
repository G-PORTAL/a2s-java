package com.gportal.a2s.messages;

import java.net.InetSocketAddress;

import com.gportal.a2s.Query;

import io.netty.buffer.ByteBuf;

public record RulesQuery(InetSocketAddress remoteAddress, Integer challenge) implements Query {
	public static final byte OP = 0x56;

	public RulesQuery withChallenge(int challenge) { return new RulesQuery(remoteAddress(), challenge); }

	public static RulesQuery read(InetSocketAddress remoteAddress, ByteBuf buffer) {
		Integer challenge = buffer.readIntLE();
		if(challenge==0) challenge = null;
		return new RulesQuery(remoteAddress, challenge);
	}
	public RulesQuery write(ByteBuf buffer) {
		buffer.writeByte(OP);
		buffer.writeIntLE(challenge()!=null?challenge():-1);
		return this;
	}
}
