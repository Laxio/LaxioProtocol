package org.laxio.protocol.netty.pipeline;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.laxio.network.connection.Connection;
import org.laxio.packet.Packet;
import org.laxio.protocol.netty.stream.LaxioByteBuf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class PacketEncoder extends MessageToByteEncoder<Packet> {

    private static final Logger LOGGER = LoggerFactory.getLogger(PacketEncoder.class);

    private final Connection connection;

    public PacketEncoder(Connection connection) {
        this.connection = connection;
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Packet packet, ByteBuf buffer) throws IOException {
        LaxioByteBuf buf = new LaxioByteBuf(buffer);

        try {
            connection.getProtocol().writePacket(connection, buf, packet);
        } catch (Exception ex) {
            LOGGER.error("Unable to write Packet", ex);
            connection.disconnect(ex);
        }
    }

}
