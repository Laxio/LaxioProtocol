package org.laxio.protocol.netty.packet.server.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.laxio.network.stream.LaxioInput;
import org.laxio.network.stream.LaxioOutput;
import org.laxio.packet.Packet;
import org.laxio.protocol.status.ServerListResponse;

import java.io.IOException;

public class StatusServerResponsePacket implements Packet {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private ServerListResponse response;

    public StatusServerResponsePacket() {
        // required
    }

    public StatusServerResponsePacket(ServerListResponse response) {
        this.response = response;
    }

    @Override
    public void read(LaxioInput input) throws IOException {
        String value = input.readString();
        response = MAPPER.readValue(value, ServerListResponse.class);
    }

    @Override
    public void write(LaxioOutput output) throws IOException {
        output.writeString(MAPPER.writeValueAsString(response));
    }

}
