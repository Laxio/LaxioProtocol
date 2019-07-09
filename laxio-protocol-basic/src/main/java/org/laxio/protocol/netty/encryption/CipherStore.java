package org.laxio.protocol.netty.encryption;

import io.netty.buffer.ByteBuf;
import org.laxio.exception.protocol.ProtocolEncryptionException;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import java.security.GeneralSecurityException;
import java.security.Key;

public abstract class CipherStore {

    protected final Cipher cipher;

    protected byte[] before = new byte[0];
    protected byte[] after = new byte[0];

    CipherStore(Key key) {
        this.cipher = generate(getOpMode(), key);
    }

    protected abstract int getOpMode();

    private Cipher generate(int opmode, Key key) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CFB8/NoPadding");
            cipher.init(opmode, key, new IvParameterSpec(key.getEncoded()));
            return cipher;
        } catch (GeneralSecurityException generalsecurityexception) {
            throw new ProtocolEncryptionException("Unable to generate cipher", generalsecurityexception);
        }
    }

    protected byte[] store(ByteBuf bytebuf) {
        int i = bytebuf.readableBytes();

        if (before.length < i) {
            before = new byte[i];
        }

        bytebuf.readBytes(before, 0, i);
        return before;
    }

}
