package org.laxio.protocol.netty.packet.server.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.laxio.network.stream.LaxioInput;
import org.laxio.network.stream.LaxioOutput;
import org.laxio.packet.Packet;
import org.laxio.protocol.netty.packet.AbstractPacket;
import org.laxio.protocol.status.ServerListResponse;

import java.io.IOException;

public class StatusServerResponsePacket extends AbstractPacket {

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
        response = MAPPER.readValue(input.readString(), ServerListResponse.class);
    }

    @Override
    public void write(LaxioOutput output) throws IOException {
        output.writeString(MAPPER.writeValueAsString(response));
    }

}
