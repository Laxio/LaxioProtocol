package org.laxio.protocol.v404.listener;

import org.laxio.Application;
import org.laxio.chat.ChatColor;
import org.laxio.chat.MessageBuilder;
import org.laxio.event.Priority;
import org.laxio.exception.protocol.ProtocolEncryptionException;
import org.laxio.listener.Listener;
import org.laxio.listener.PacketHandler;
import org.laxio.network.connection.Connection;
import org.laxio.network.encryption.Encryption;
import org.laxio.protocol.netty.packet.server.login.LoginServerDisconnectPacket;
import org.laxio.protocol.v404.packet.client.login.LoginClientEncryptionResponse;
import org.laxio.protocol.v404.packet.client.login.LoginClientLoginStartPacket;
import org.laxio.protocol.v404.packet.server.login.LoginServerEncryptionRequest;
import org.laxio.protocol.v404.packet.server.login.LoginServerLoginSuccess;
import org.laxio.server.ServerApplication;
import org.laxio.server.ServerConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.util.Arrays;
import java.util.Random;
import java.util.UUID;

import static org.laxio.protocol.netty.util.EncryptionUtil.decipher;

public class LoginListener implements Listener {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginListener.class);

    private static final int VERIFY_TOKEN_LENGTH = 4;

    private final Application application;

    public LoginListener(Application application) {
        this.application = application;
    }

    @PacketHandler(priority = Priority.APPLICATION)
    public void onLoginStart(LoginClientLoginStartPacket packet) {
        LOGGER.info("Login start for {}", packet.getUsername());

        if (application instanceof ServerApplication) {
            ServerApplication server = (ServerApplication) application;
            ServerConfiguration config = server.getServerConfiguration();
            if (config.isEncrypted()) {
                generateEncryption(packet.getConnection());
            } else {
                // TODO: login success
            }
        } else {
            packet.getConnection().disconnect(new IllegalStateException("Not a server"));
        }
    }

    private void generateEncryption(Connection connection) {
        byte[] verifyToken = new byte[VERIFY_TOKEN_LENGTH];
        new Random().nextBytes(verifyToken);

        Encryption encryption = connection.getEncryption();
        encryption.setVerifyToken(verifyToken);

        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(2048);
            KeyPair kp = generator.generateKeyPair();
            encryption.setKeyPair(kp);
            connection.sendPacket(new LoginServerEncryptionRequest("Laxio", kp.getPublic().getEncoded(), encryption.getVerifyToken()));
        } catch (NoSuchAlgorithmException ex) {
            LOGGER.error("Unable to get generator", ex);
            connection.disconnect(ex);
        } catch (ProtocolEncryptionException ex) {
            LOGGER.error("Unable to encrypt", ex);
            connection.disconnect(ex);
        }
    }

    @PacketHandler(priority = Priority.APPLICATION)
    public void onEncryptionResponse(LoginClientEncryptionResponse packet) {
        Connection connection = packet.getConnection();
        Encryption encryption = connection.getEncryption();
        encryption.setSharedSecret(packet.getSharedSecret());

        KeyPair keyPair = encryption.getKeyPair();
        PrivateKey priv = keyPair.getPrivate();

        encryption.enable();

        byte[] token = decipher(priv, packet.getVerifyToken());

        if (!Arrays.equals(token, encryption.getVerifyToken())) {
            StringBuilder builder = new StringBuilder();
            builder.append(ChatColor.AQUA).append("Encryption error\n\n");
            builder.append(ChatColor.RED).append("Nonce does not match");

            connection.sendPacket(new LoginServerDisconnectPacket(MessageBuilder.builder().message(builder.toString()).build()));
            return;
        }

        connection.sendPacket(new LoginServerLoginSuccess(UUID.randomUUID(), "Sir_Haribo"));

        /*String hash = BrokenHash.hash("", key, secret);
        client.getProfile().authenticate(hash);

        login(packet, client.getProfile());*/
    }

}
