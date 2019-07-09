package org.laxio.protocol.netty.util;

import org.laxio.exception.protocol.ProtocolEncryptionException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

public final class EncryptionUtil {

    public static byte[] decipher(Key key, byte[] out) {
        return decipher(2, key, out);
    }

    private static byte[] decipher(int opmode, Key key, byte[] out) {
        try {
            Cipher cipher = cipher(opmode, key.getAlgorithm(), key);
            if (cipher != null) {
                return cipher.doFinal(out);
            }
        } catch (IllegalBlockSizeException | BadPaddingException ex) {
            throw new ProtocolEncryptionException("Unable to decipher", ex);
        }

        return new byte[0];
    }

    private static Cipher cipher(int opmode, String algorithm, Key key) {
        try {
            Cipher cipher = Cipher.getInstance(algorithm);
            cipher.init(opmode, key);
            return cipher;
        } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException ex) {
            throw new ProtocolEncryptionException("Unable to init cipher", ex);
        }
    }

}
