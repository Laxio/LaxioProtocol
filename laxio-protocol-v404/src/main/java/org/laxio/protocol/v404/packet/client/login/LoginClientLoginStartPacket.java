package org.laxio.protocol.v404.packet.client.login;

import org.laxio.network.stream.LaxioInput;
import org.laxio.network.stream.LaxioOutput;
import org.laxio.packet.Packet;
import org.laxio.protocol.netty.packet.AbstractPacket;
import org.laxio.util.Conditions;

import java.io.IOException;

public class LoginClientLoginStartPacket extends AbstractPacket {

    private String username;

    public LoginClientLoginStartPacket() {
        // required
    }

    public LoginClientLoginStartPacket(String username) {
        Conditions.notNull(username, "username");
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public void read(LaxioInput input) throws IOException {
        username = input.readString();
    }

    @Override
    public void write(LaxioOutput output) throws IOException {
        output.writeString(username);
    }

}
