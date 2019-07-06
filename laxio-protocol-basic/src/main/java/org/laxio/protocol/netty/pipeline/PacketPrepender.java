package org.laxio.protocol.netty.pipeline;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.laxio.protocol.netty.stream.LaxioByteBuf;

public class PacketPrepender extends MessageToByteEncoder<ByteBuf> {

    /**
     * This method checks if the length of the packet can be stored into a VarInt (3 bytes)
     *
     * @param channelHandlerContext The channel to send the packet to
     * @param input                 The input buffer
     * @param output                The output buffer
     *
     * @throws Exception When the length of the data is not a valid var int
     */
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, ByteBuf input, ByteBuf output) throws Exception {
        int bytes = input.readableBytes();
        int length = getVarIntLength(bytes);

        if (length > 3) {
            throw new UnsupportedOperationException("Unable to fit " + bytes + " into 3 bytes");
        } else {
            LaxioByteBuf buf = new LaxioByteBuf(output);
            output.ensureWritable(length + bytes);
            buf.writeVarInt(bytes);
            output.writeBytes(input, input.readerIndex(), bytes);
        }
    }

    /**
     * Gets the length of the supplied int as a VarInt
     *
     * @param var The int to convert and check
     *
     * @return A byte length between 1 and 5
     */
    private static int getVarIntLength(int var) {
        for (int j = 1; j < 5; ++j) {
            if ((var & -1 << j * 7) == 0) {
                return j;
            }
        }

        return 5;
    }

}
