package org.laxio.protocol.netty.connection;

import org.laxio.network.encryption.Encryption;
import org.laxio.protocol.netty.encryption.InCipher;
import org.laxio.protocol.netty.encryption.OutCipher;
import org.laxio.protocol.netty.pipeline.PacketDecrypter;
import org.laxio.protocol.netty.pipeline.PacketEncrypter;
import org.laxio.protocol.netty.server.LaxioServerNettyClient;
import org.laxio.protocol.netty.util.EncryptionUtil;

import javax.crypto.spec.SecretKeySpec;
import java.security.KeyPair;

public class NettyEncryption implements Encryption {

    private final LaxioServerNettyClient client;

    private byte[] sharedSecret;
    private byte[] verifyToken;
    private KeyPair keyPair;

    private boolean enabled;

    public NettyEncryption(LaxioServerNettyClient client) {
        this.client = client;
        this.enabled = false;
    }

    @Override
    public void enable() {
        if (enabled) {
            return;
        }

        SecretKeySpec spec = new SecretKeySpec(EncryptionUtil.decipher(keyPair.getPrivate(), sharedSecret), "AES");

        InCipher inCipher = new InCipher(spec);
        OutCipher outCipher = new OutCipher(spec);

        client.getChannel().pipeline().addBefore("splitter", "decrypt", new PacketDecrypter(inCipher));
        client.getChannel().pipeline().addBefore("prepender", "encrypt", new PacketEncrypter(outCipher));
    }

    @Override
    public void disable() {
        if (!enabled) {
            return;
        }
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public byte[] getSharedSecret() {
        return sharedSecret;
    }

    @Override
    public void setSharedSecret(byte[] sharedSecret) {
        this.sharedSecret = sharedSecret;
    }

    @Override
    public byte[] getVerifyToken() {
        return verifyToken;
    }

    @Override
    public void setVerifyToken(byte[] verifyToken) {
        this.verifyToken = verifyToken;
    }

    @Override
    public KeyPair getKeyPair() {
        return keyPair;
    }

    @Override
    public void setKeyPair(KeyPair keyPair) {
        this.keyPair = keyPair;
    }

}
