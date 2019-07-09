package org.laxio.protocol.netty.packet.client.status;

import org.laxio.network.stream.LaxioInput;
import org.laxio.network.stream.LaxioOutput;
import org.laxio.packet.Packet;
import org.laxio.protocol.netty.packet.AbstractPacket;

import java.io.IOException;

public class StatusClientRequestPacket extends AbstractPacket {

    @Override
    public void read(LaxioInput input) throws IOException {
    }

    @Override
    public void write(LaxioOutput output) throws IOException {
    }

}
