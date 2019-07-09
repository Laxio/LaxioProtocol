package org.laxio.protocol.netty.pipeline;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.laxio.protocol.netty.encryption.InCipher;

import javax.crypto.ShortBufferException;
import java.util.List;

public class PacketDecrypter extends ByteToMessageDecoder {

    private final InCipher inCipher;

    public PacketDecrypter(InCipher inCipher) {
        this.inCipher = inCipher;
    }

    @Override
    protected void decode(ChannelHandlerContext context, ByteBuf input, List<Object> list) throws ShortBufferException {
        list.add(inCipher.decrypt(context, input));
    }

}
