package org.laxio.protocol.netty.pipeline;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.laxio.protocol.netty.encryption.OutCipher;

import javax.crypto.ShortBufferException;

public class PacketEncrypter extends MessageToByteEncoder<ByteBuf> {

    private final OutCipher outCipher;

    public PacketEncrypter(OutCipher outCipher) {
        this.outCipher = outCipher;
    }

    @Override
    protected void encode(ChannelHandlerContext context, ByteBuf input, ByteBuf output) throws ShortBufferException {
        outCipher.encrypt(input, output);
    }

}
