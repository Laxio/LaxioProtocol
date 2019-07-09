package org.laxio.protocol.v404.util;

import org.laxio.exception.authentication.SessionAuthenticationException;

import javax.crypto.SecretKey;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.PublicKey;

public final class MojangDigestUtil {

    public static String hash(String serverName, PublicKey pub, SecretKey secret) {
        try {
            byte[] ascii = serverName.getBytes("ISO_8859_1");
            byte[] digest = digest("SHA-1", ascii, secret.getEncoded(), pub.getEncoded());
            return new BigInteger(digest).toString(16);
        } catch (Exception ex) {
            throw new SessionAuthenticationException("Unable to digest", ex);
        }
    }

    private static byte[] digest(String algorithm, byte[] serverName, byte[] secretKey, byte[] publicKey) {
        try {
            MessageDigest digest = MessageDigest.getInstance(algorithm);
            digest.update(serverName);
            digest.update(secretKey);
            digest.update(publicKey);

            return digest.digest();
        } catch (Exception ex) {
            throw new SessionAuthenticationException("Unable to digest", ex);
        }
    }

}
