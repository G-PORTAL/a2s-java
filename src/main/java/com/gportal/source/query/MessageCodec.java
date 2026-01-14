package com.gportal.source.query;

import java.util.List;

import com.gportal.source.query.messages.ChallengeReply;
import com.gportal.source.query.messages.InfoQuery;
import com.gportal.source.query.messages.InfoReply;
import com.gportal.source.query.messages.PlayerQuery;
import com.gportal.source.query.messages.PlayerReply;
import com.gportal.source.query.messages.RulesQuery;
import com.gportal.source.query.messages.RulesReply;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageCodec;

public class MessageCodec extends MessageToMessageCodec<DatagramPacket, Message> {
	protected void decode(ChannelHandlerContext ctx, DatagramPacket msg, List<Object> out) throws Exception {
		int header = msg.content().readIntLE();
		if(header != -1) throw new UnsupportedOperationException("we dont support split packets yet");
		byte op = msg.content().readByte();
		switch(op) {
			case ChallengeReply.OP: out.add(ChallengeReply.read(msg.sender(), msg.content())); break;
			case InfoQuery.OP: out.add(InfoQuery.read(msg.sender(), msg.content())); break;
			case InfoReply.OP: out.add(InfoReply.read(msg.sender(), msg.content())); break;
			case PlayerQuery.OP: out.add(PlayerQuery.read(msg.sender(), msg.content())); break;
			case PlayerReply.OP: out.add(PlayerReply.read(msg.sender(), msg.content())); break;
			case RulesQuery.OP: out.add(RulesQuery.read(msg.sender(), msg.content())); break;
			case RulesReply.OP: out.add(RulesReply.read(msg.sender(), msg.content())); break;
			default: throw new UnsupportedOperationException("Unknown OP 0x" + String.format("%2x", op).replaceAll(" ", "0"));
		}
	}
	protected void encode(ChannelHandlerContext ctx, Message msg, List<Object> out) throws Exception {
		ByteBuf buffer = ctx.alloc().buffer();
		buffer.writeIntLE(-1);
		msg.write(buffer);
		out.add(new DatagramPacket(buffer, msg.remoteAddress()));
	}
}
