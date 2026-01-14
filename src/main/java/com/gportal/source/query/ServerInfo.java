package com.gportal.source.query;

import static com.gportal.source.query.Message.readString;
import static com.gportal.source.query.Message.writeString;

import java.net.InetSocketAddress;

import io.netty.buffer.ByteBuf;

public record ServerInfo(InetSocketAddress queryAddress, byte protocol, String name, String map, String folder, String game, short appId, byte players, byte maxPlayers, byte bots, char serverType, char environment, boolean password, boolean vac, String version, Short port, Long steamId, Short tvPort, String tvName, String config, Long gameId) {
	public InetSocketAddress gameAddress() { return new InetSocketAddress(queryAddress().getAddress(), port()); }
	public static ServerInfo read(InetSocketAddress queryAddress, ByteBuf buffer) {
		byte protocol = buffer.readByte();
		String name = readString(buffer);
		String map = readString(buffer);
		String folder = readString(buffer);
		String game = readString(buffer);
		short appId = buffer.readShortLE();
		byte players = buffer.readByte();
		byte maxPlayers = buffer.readByte();
		byte bots = buffer.readByte();
		char serverType = (char) buffer.readByte();
		char environment = (char) buffer.readByte();
		boolean password = buffer.readBoolean();
		boolean vac = buffer.readBoolean();
		String version = readString(buffer);

		byte extra = buffer.readableBytes()>0?buffer.readByte():0;
		Short port = (extra&0x80)!=0?buffer.readShortLE():null;
		Long steamId = (extra&0x10)!=0?buffer.readLongLE():null;
		Short tvPort = (extra&0x40)!=0?buffer.readShortLE():null;
		String tvName = (extra&0x40)!=0?readString(buffer):null;
		String config = (extra&0x20)!=0?readString(buffer):null;
		Long gameId = (extra&0x01)!=0?buffer.readLongLE():null;

		return new ServerInfo(queryAddress, protocol, name, map, folder, game, appId, players, maxPlayers, bots, serverType, environment, password, vac, version, port, steamId, tvPort, tvName, config, gameId);
	}
	public ServerInfo write(ByteBuf buffer) {
		buffer.writeByte(protocol());
		writeString(buffer, name());
		writeString(buffer, map());
		writeString(buffer, folder());
		writeString(buffer, game());
		buffer.writeShortLE(appId());
		buffer.writeByte(players());
		buffer.writeByte(maxPlayers());
		buffer.writeByte(bots());
		buffer.writeByte(serverType());
		buffer.writeByte(environment());
		buffer.writeBoolean(password());
		buffer.writeBoolean(vac());
		writeString(buffer, version());
		
		byte extra = 0;
		if(port()!=null) extra|=0x80;
		if(steamId()!=null) extra|=0x10;
		if(tvPort()!=null&&tvName()!=null) extra|=0x40;
		if(config()!=null) extra|=0x20;
		if(gameId()!=null) extra|=0x01;
		if(extra!=0) buffer.writeByte(extra);
		if((extra&0x80)!=0) buffer.writeShortLE(port());
		if((extra&0x10)!=0) buffer.writeLongLE(steamId());
		if((extra&0x40)!=0) buffer.writeShortLE(tvPort());
		if((extra&0x40)!=0) writeString(buffer, tvName());
		if((extra&0x20)!=0) writeString(buffer, config());
		if((extra&0x01)!=0) buffer.writeLongLE(gameId());

		return this;
	}
}
