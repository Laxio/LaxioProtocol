package org.laxio.protocol.netty.encryption;

import io.netty.buffer.ByteBuf;

import javax.crypto.ShortBufferException;
import java.security.Key;

public class OutCipher extends CipherStore {

    public OutCipher(Key key) {
        super(key);
    }

    @Override
    protected int getOpMode() {
        return 1;
    }

    public void encrypt(ByteBuf input, ByteBuf output) throws ShortBufferException {
        synchronized (cipher) {
            int i = input.readableBytes();
            byte[] abyte = store(input);
            int j = cipher.getOutputSize(i);

            if (after.length < j) {
                after = new byte[j];
            }

            output.writeBytes(after, 0, cipher.update(abyte, 0, i, after));
        }
    }

}
