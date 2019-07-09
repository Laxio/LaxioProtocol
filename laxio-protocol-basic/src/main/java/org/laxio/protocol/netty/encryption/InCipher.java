package org.laxio.protocol.netty.encryption;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import javax.crypto.ShortBufferException;
import java.security.Key;

public class InCipher extends CipherStore {

    public InCipher(Key key) {
        super(key);
    }

    @Override
    protected int getOpMode() {
        return 2;
    }

    public ByteBuf decrypt(ChannelHandlerContext context, ByteBuf input) throws ShortBufferException {
        synchronized (cipher) {
            int i = input.readableBytes();
            byte[] abyte = store(input);
            ByteBuf buf = context.alloc().heapBuffer(cipher.getOutputSize(i));

            buf.writerIndex(cipher.update(abyte, 0, i, buf.array(), buf.arrayOffset()));
            return buf;
        }
    }

}
