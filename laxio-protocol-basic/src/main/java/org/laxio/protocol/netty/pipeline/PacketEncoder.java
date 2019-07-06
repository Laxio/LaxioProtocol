package org.laxio.protocol.netty.pipeline;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.laxio.network.connection.Connection;
import org.laxio.packet.Packet;
import org.laxio.protocol.netty.stream.LaxioByteBuf;

import java.io.IOException;

public class PacketEncoder extends MessageToByteEncoder<Packet> {

    private final Connection connection;

    public PacketEncoder(Connection connection) {
        this.connection = connection;
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Packet packet, ByteBuf buffer) throws IOException {
        LaxioByteBuf buf = new LaxioByteBuf(buffer);
        connection.getProtocol().writePacket(connection, buf, packet);
    }

}
