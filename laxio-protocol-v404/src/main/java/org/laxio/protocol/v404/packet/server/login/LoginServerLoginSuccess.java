package org.laxio.protocol.v404.packet.server.login;

import org.laxio.network.stream.LaxioInput;
import org.laxio.network.stream.LaxioOutput;
import org.laxio.protocol.netty.packet.AbstractPacket;

import java.io.IOException;
import java.util.UUID;

public class LoginServerLoginSuccess extends AbstractPacket {

    private UUID uuid;
    private String username;

    public LoginServerLoginSuccess() {
        // required
    }

    public LoginServerLoginSuccess(UUID uuid, String username) {
        this.uuid = uuid;
        this.username = username;
    }

    @Override
    public void read(LaxioInput input) throws IOException {
        uuid = input.readDashedUUID();
        username = input.readString();
    }

    @Override
    public void write(LaxioOutput output) throws IOException {
        output.writeDashedUUID(uuid);
        output.writeString(username);
    }

}
