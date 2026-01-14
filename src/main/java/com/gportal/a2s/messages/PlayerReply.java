package com.gportal.a2s.messages;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

import com.gportal.a2s.PlayerInfo;
import com.gportal.a2s.Reply;

import io.netty.buffer.ByteBuf;

public record PlayerReply(InetSocketAddress remoteAddress, List<PlayerInfo> payload) implements Reply {
	public static final byte OP = 0x44;

	public static PlayerReply read(InetSocketAddress remoteAddress, ByteBuf buffer) {
		byte count = buffer.readByte();
		List<PlayerInfo> payload = new ArrayList<PlayerInfo>();
		for(int i = 0; i < count; i++) {
			payload.add(PlayerInfo.read(buffer));
		}
		return new PlayerReply(remoteAddress, payload);
	}
	public PlayerReply write(ByteBuf buffer) {
		buffer.writeByte(OP);
		buffer.writeByte(payload().size());
		payload().forEach(player -> {
			player.write(buffer);
		});
		return this;
	}
}
