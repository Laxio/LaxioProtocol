package org.laxio.protocol.v404.packet.client.login;

import org.laxio.network.stream.LaxioInput;
import org.laxio.network.stream.LaxioOutput;
import org.laxio.protocol.netty.packet.AbstractPacket;

import java.io.IOException;

public class LoginClientEncryptionResponse extends AbstractPacket {

    private byte[] sharedSecret;
    private byte[] verifyToken;

    public LoginClientEncryptionResponse() {
        // required
    }

    public LoginClientEncryptionResponse(byte[] sharedSecret, byte[] verifyToken) {
        this.sharedSecret = sharedSecret;
        this.verifyToken = verifyToken;
    }

    public byte[] getSharedSecret() {
        return sharedSecret;
    }

    public byte[] getVerifyToken() {
        return verifyToken;
    }

    @Override
    public void read(LaxioInput input) throws IOException {
        sharedSecret = input.readBytes(input.readVarInt());
        verifyToken = input.readBytes(input.readVarInt());
    }

    @Override
    public void write(LaxioOutput output) throws IOException {
        output.writeVarInt(sharedSecret.length);
        output.writeBytes(sharedSecret);
        output.writeVarInt(verifyToken.length);
        output.writeBytes(verifyToken);
    }

}
