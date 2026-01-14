package com.gportal.source.query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.gportal.source.query.messages.InfoQuery;
import com.gportal.source.query.messages.InfoReply;
import com.gportal.source.query.messages.PlayerQuery;
import com.gportal.source.query.messages.PlayerReply;
import com.gportal.source.query.messages.RulesQuery;
import com.gportal.source.query.messages.RulesReply;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.nio.NioDatagramChannel;

public class QueryServer {
	private DatagramChannel channel;
	private EventLoopGroup worker;
	public final ServerInfo info;
	public final Map<String, String> rules = new HashMap<String, String>();
	public final List<PlayerInfo> players = new ArrayList<PlayerInfo>();

	public QueryServer(int port, ServerInfo info) {
		this.info = info;
		worker = new NioEventLoopGroup();
		Bootstrap bootstrap = new Bootstrap()
			.group(worker)
			.channel(NioDatagramChannel.class)
			.handler(new ChannelInitializer<NioDatagramChannel>() {
				protected void initChannel(NioDatagramChannel ch) throws Exception {
					ch.pipeline().addLast(
						new MessageCodec(),
						new SimpleChannelInboundHandler<InfoQuery>() {
							protected void channelRead0(ChannelHandlerContext ctx, InfoQuery msg) throws Exception {
								ctx.writeAndFlush(new InfoReply(msg.remoteAddress(), QueryServer.this.info));
							}
						},
						new SimpleChannelInboundHandler<PlayerQuery>() {
							protected void channelRead0(ChannelHandlerContext ctx, PlayerQuery msg) throws Exception {
								ctx.writeAndFlush(new PlayerReply(msg.remoteAddress(), players));
							}
						},
						new SimpleChannelInboundHandler<RulesQuery>() {
							protected void channelRead0(ChannelHandlerContext ctx, RulesQuery msg) throws Exception {
								ctx.writeAndFlush(new RulesReply(msg.remoteAddress(), rules));
							}
						}
					);
				}
			});
		channel = (DatagramChannel) bootstrap.bind(port).syncUninterruptibly().channel();
	}

	public void shutdown() {
		channel.close();
		worker.shutdownGracefully();
	}
}
