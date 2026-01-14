package com.gportal.a2s;

import static com.gportal.a2s.Message.readString;
import static com.gportal.a2s.Message.writeString;

import java.net.InetSocketAddress;

import io.netty.buffer.ByteBuf;

public class ServerInfo {
	private InetSocketAddress queryAddress;
	private byte protocol;
	private String name;
	private String map;
	private String folder;
	private String game;
	private short appId;
	private byte players;
	private byte maxPlayers;
	private byte bots;
	private char serverType;
	private char environment;
	private boolean password;
	private boolean vac;
	private String version;
	private Short port;
	private Long steamId;
	private Short tvPort;
	private String tvName;
	private String config;
	private Long gameId;

	public ServerInfo(InetSocketAddress queryAddress, byte protocol, String name, String map, String folder, String game, short appId, byte players, byte maxPlayers, byte bots, char serverType, char environment, boolean password, boolean vac, String version, Short port, Long steamId, Short tvPort, String tvName, String config, Long gameId) {
		this.queryAddress = queryAddress;
		this.protocol = protocol;
		this.name = name;
		this.map = map;
		this.folder = folder;
		this.game = game;
		this.appId = appId;
		this.players = players;
		this.maxPlayers = maxPlayers;
		this.bots = bots;
		this.serverType = serverType;
		this.environment = environment;
		this.password = password;
		this.vac = vac;
		this.version = version;
		this.port = port;
		this.steamId = steamId;
		this.tvPort = tvPort;
		this.tvName = tvName;
		this.config = config;
		this.gameId = gameId;
	}

	public InetSocketAddress queryAddress() { return queryAddress; }
	public void setQueryAddress(InetSocketAddress queryAddress) { this.queryAddress = queryAddress; }

	public byte protocol() { return protocol; }
	public void setProtocol(byte protocol) { this.protocol = protocol; }

	public String name() { return name; }
	public void setName(String name) { this.name = name; }

	public String map() { return map; }
	public void setMap(String map) { this.map = map; }

	public String folder() { return folder; }
	public void setFolder(String folder) { this.folder = folder; }

	public String game() { return game; }
	public void setGame(String game) { this.game = game; }

	public short appId() { return appId; }
	public void setAppId(short appId) { this.appId = appId; }

	public byte players() { return players; }
	public void setPlayers(byte players) { this.players = players; }

	public byte maxPlayers() { return maxPlayers; }
	public void setMaxPlayers(byte maxPlayers) { this.maxPlayers = maxPlayers; }

	public byte bots() { return bots; }
	public void setBots(byte bots) { this.bots = bots; }

	public char serverType() { return serverType; }
	public void setServerType(char serverType) { this.serverType = serverType; }

	public char environment() { return environment; }
	public void setEnvironment(char environment) { this.environment = environment; }

	public boolean password() { return password; }
	public void setPassword(boolean password) { this.password = password; }

	public boolean vac() { return vac; }
	public void setVac(boolean vac) { this.vac = vac; }

	public String version() { return version; }
	public void setVersion(String version) { this.version = version; }

	public Short port() { return port; }
	public void setPort(Short port) { this.port = port; }

	public Long steamId() { return steamId; }
	public void setSteamId(Long steamId) { this.steamId = steamId; }

	public Short tvPort() { return tvPort; }
	public void setTvPort(Short tvPort) { this.tvPort = tvPort; }

	public String tvName() { return tvName; }
	public void setTvName(String tvName) { this.tvName = tvName; }

	public String config() { return config; }
	public void setConfig(String config) { this.config = config; }

	public Long gameId() { return gameId; }
	public void setGameId(Long gameId) { this.gameId = gameId; }

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
