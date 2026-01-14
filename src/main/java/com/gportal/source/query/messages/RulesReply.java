package com.gportal.source.query.messages;

import static com.gportal.source.query.Message.readString;
import static com.gportal.source.query.Message.writeString;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import com.gportal.source.query.Reply;

import io.netty.buffer.ByteBuf;

public record RulesReply(InetSocketAddress remoteAddress, Map<String, String> payload) implements Reply {
	public static final byte OP = 0x45;

	public static RulesReply read(InetSocketAddress remoteAddress, ByteBuf buffer) {
		short count = buffer.readShortLE();
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
