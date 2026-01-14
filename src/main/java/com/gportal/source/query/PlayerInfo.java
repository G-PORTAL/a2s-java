package com.gportal.source.query;

import static com.gportal.source.query.Message.readString;
import static com.gportal.source.query.Message.writeString;

import io.netty.buffer.ByteBuf;

public record PlayerInfo(byte index, String name, int score, float duration) {
	public static PlayerInfo read(ByteBuf buffer) {
		byte index = buffer.readByte();
		String name = readString(buffer);
		int score = buffer.readIntLE();
		float duration = buffer.readFloatLE();

		return new PlayerInfo(index, name, score, duration);
	}
	public PlayerInfo write(ByteBuf buffer) {
		buffer.writeByte(index());
		writeString(buffer, name());
		buffer.writeIntLE(score());
		buffer.writeFloatLE(duration());

		return this;
	}
}
