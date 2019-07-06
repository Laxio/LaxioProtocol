package org.laxio.protocol.netty.packet.client.handshake;

import org.laxio.network.stream.LaxioInput;
import org.laxio.network.stream.LaxioOutput;
import org.laxio.packet.Packet;

import java.io.IOException;

public class HandshakeClientHandshakePacket implements Packet {

    private int protocolVersion;
    private String serverAddress;
    private int serverPort;
    private int nextState;

    public HandshakeClientHandshakePacket() {
        // nope
    }

    public HandshakeClientHandshakePacket(int protocolVersion, String serverAddress, int serverPort, int nextState) {
        this.protocolVersion = protocolVersion;
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
        this.nextState = nextState;
    }

    public int getProtocolVersion() {
        return protocolVersion;
    }

    public String getServerAddress() {
        return serverAddress;
    }

    public int getServerPort() {
        return serverPort;
    }

    public int getNextState() {
        return nextState;
    }

    @Override
    public void read(LaxioInput input) throws IOException {
        protocolVersion = input.readVarInt();
        serverAddress = input.readString();
        serverPort = input.readUnsignedShort();
        nextState = input.readVarInt();
    }

    @Override
    public void write(LaxioOutput output) throws IOException {
        output.writeVarInt(protocolVersion);
        output.writeString(serverAddress);
        output.writeUnsignedShort(serverPort);
        output.writeVarInt(nextState);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(getClass().getSimpleName());
        sb.append('{');
        sb.append("protocolVersion=").append(protocolVersion);
        sb.append(", serverAddress='").append(serverAddress).append('\'');
        sb.append(", serverPort=").append(serverPort);
        sb.append(", nextState=").append(nextState);
        sb.append('}');
        return sb.toString();
    }

}
