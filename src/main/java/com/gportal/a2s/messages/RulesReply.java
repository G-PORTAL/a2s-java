package com.gportal.a2s.messages;

import static com.gportal.a2s.Message.readString;
import static com.gportal.a2s.Message.writeString;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import com.gportal.a2s.Reply;

import io.netty.buffer.ByteBuf;

public record RulesReply(InetSocketAddress remoteAddress, Map<String, String> payload) implements Reply {
	public static final byte OP = 0x45;

	public static RulesReply read(InetSocketAddress remoteAddress, ByteBuf buffer) {
		int count = buffer.readUnsignedShortLE();
		Map<String, String> payload = new HashMap<String, String>();
		for(int i = 0; i < count; i++) {
			payload.put(readString(buffer), readString(buffer));
		}
		return new RulesReply(remoteAddress, payload);
	}
	public RulesReply write(ByteBuf buffer) {
		buffer.writeByte(OP);
		buffer.writeShortLE(payload().entrySet().size());
		payload().entrySet().forEach(entry -> {
			writeString(buffer, entry.getKey());
			writeString(buffer, entry.getValue());
		});
		return this;
	}
}
