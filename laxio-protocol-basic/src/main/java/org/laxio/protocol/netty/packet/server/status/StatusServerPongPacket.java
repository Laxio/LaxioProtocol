package org.laxio.protocol.netty.packet.server.status;

import org.laxio.network.stream.LaxioInput;
import org.laxio.network.stream.LaxioOutput;
import org.laxio.packet.Packet;
import org.laxio.protocol.netty.packet.AbstractPacket;

import java.io.IOException;

public class StatusServerPongPacket extends AbstractPacket {

    private long payload;

    public StatusServerPongPacket() {
        // required
    }

    public StatusServerPongPacket(long payload) {
        this.payload = payload;
    }

    public long getPayload() {
        return payload;
    }

    @Override
    public void read(LaxioInput input) throws IOException {
        payload = input.readLong();
    }

    @Override
    public void write(LaxioOutput output) throws IOException {
        output.writeLong(payload);
    }

}
