package org.laxio.protocol.v404.packet.server.login;

import org.laxio.network.stream.LaxioInput;
import org.laxio.network.stream.LaxioOutput;
import org.laxio.protocol.netty.packet.AbstractPacket;

import java.io.IOException;

public class LoginServerEncryptionRequest extends AbstractPacket {

    private String serverId;
    private byte[] publicKey;
    private byte[] verifyToken;

    public LoginServerEncryptionRequest() {
        // required
    }

    public LoginServerEncryptionRequest(String serverId, byte[] publicKey, byte[] verifyToken) {
        this.serverId = serverId;
        this.publicKey = publicKey;
        this.verifyToken = verifyToken;
    }

    public String getServerId() {
        return serverId;
    }

    public byte[] getPublicKey() {
        return publicKey;
    }

    public byte[] getVerifyToken() {
        return verifyToken;
    }

    @Override
    public void read(LaxioInput input) throws IOException {
        serverId = input.readString();
        publicKey = input.readBytes(input.readVarInt());
        verifyToken = input.readBytes(input.readVarInt());
    }

    @Override
    public void write(LaxioOutput output) throws IOException {
        output.writeString(serverId);
        output.writeVarInt(publicKey.length);
        output.writeBytes(publicKey);
        output.writeVarInt(verifyToken.length);
        output.writeBytes(verifyToken);
    }

}
