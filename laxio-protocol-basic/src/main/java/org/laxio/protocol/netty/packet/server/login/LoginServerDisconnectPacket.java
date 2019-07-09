package org.laxio.protocol.netty.packet.server.login;

import org.json.JSONObject;
import org.laxio.chat.MessageBuilder;
import org.laxio.chat.MessageComponent;
import org.laxio.network.stream.LaxioInput;
import org.laxio.network.stream.LaxioOutput;
import org.laxio.packet.Packet;
import org.laxio.protocol.netty.packet.AbstractPacket;

import java.io.IOException;

public class LoginServerDisconnectPacket extends AbstractPacket {

    private MessageComponent reason;

    public LoginServerDisconnectPacket() {
        // required
    }

    public LoginServerDisconnectPacket(MessageComponent reason) {
        this.reason = reason;
    }

    @Override
    public void read(LaxioInput input) throws IOException {
        String chatString = input.readString();
        JSONObject json = new JSONObject(chatString);
        reason = MessageBuilder.builder().json(json).build();
    }

    @Override
    public void write(LaxioOutput output) throws IOException {
        String chatString = reason.toJSON().toString();
        output.writeString(chatString);
    }

}
