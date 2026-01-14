package com.gportal.a2s.messages;

import java.net.InetSocketAddress;

import com.gportal.a2s.Message;

import io.netty.buffer.ByteBuf;

public record ChallengeReply(InetSocketAddress remoteAddress, int payload) implements Message {
	public static final byte OP = 0x41;

	public static ChallengeReply read(InetSocketAddress remoteAddress, ByteBuf buffer) {
		int payload = buffer.readIntLE();
		return new ChallengeReply(remoteAddress, payload);
	}
	public ChallengeReply write(ByteBuf buffer) {
		buffer.writeByte(OP);
		buffer.writeIntLE(payload);
		return this;
	}
}
