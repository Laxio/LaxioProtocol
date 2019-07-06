package org.laxio.protocol.netty.pipeline;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.laxio.network.connection.Connection;
import org.laxio.packet.Packet;
import org.laxio.protocol.netty.stream.LaxioByteBuf;

import java.util.List;

public class PacketDecoder extends ByteToMessageDecoder {

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
        cause.printStackTrace();
        /*DecoderExceptionMessage message = new DecoderExceptionMessage(client, client.getServer(), cause);
        client.getServer().getManager().call(message);

        //client.sendPacket(new PlayDisconnectPacket(message.getDisconnectReason()));
        //ctx.close();*/
    }

}
