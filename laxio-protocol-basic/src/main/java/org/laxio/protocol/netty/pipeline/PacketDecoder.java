package org.laxio.protocol.netty.pipeline;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.laxio.chat.ChatColor;
import org.laxio.chat.MessageBuilder;
import org.laxio.network.connection.Connection;
import org.laxio.packet.Packet;
import org.laxio.protocol.netty.stream.LaxioByteBuf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class PacketDecoder extends ByteToMessageDecoder {

    private static final Logger LOGGER = LoggerFactory.getLogger(PacketDecoder.class);

    private final Connection connection;

    public PacketDecoder(Connection connection) {
        this.connection = connection;
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        if (byteBuf.readableBytes() > 0) {
            LaxioByteBuf buffer = new LaxioByteBuf(byteBuf);
            list.add(connection.getProtocol().readPacket(connection, buffer));
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        LOGGER.error("Unable to decode Packet", cause);
        connection.disconnect(cause);
    }

}
