package org.laxio.protocol.netty.pipeline;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.CorruptedFrameException;
import org.laxio.protocol.netty.stream.LaxioByteBuf;

import java.util.ArrayList;
import java.util.List;

public class PacketSplitter extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf buffer, List<Object> list) throws Exception {
        buffer.markReaderIndex();
        byte[] length = new byte[3];

        for (int i = 0; i < length.length; ++i) {
            if (!buffer.isReadable()) {
                buffer.resetReaderIndex();
                return;
            }

            length[i] = buffer.readByte();

            List<String> strings = new ArrayList<>();
            for (int j = 0; j < length.length; j++) {
                strings.add(Integer.toBinaryString(length[j]));
            }

            if (length[i] > 0) {
                LaxioByteBuf lbf = new LaxioByteBuf(buffer);
                buffer.resetReaderIndex();
                int size = lbf.readVarInt();

                if (buffer.readableBytes() >= size) {
                    list.add(buffer.readBytes(size));
                    return;
                }

                buffer.resetReaderIndex();
                return;
            }
        }

        throw new CorruptedFrameException("length wider than 21-bit");

    }

}
