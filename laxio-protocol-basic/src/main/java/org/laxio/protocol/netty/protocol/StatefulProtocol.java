package org.laxio.protocol.netty.protocol;

import org.laxio.exception.protocol.InternalProtocolException;
import org.laxio.network.connection.Connection;
import org.laxio.network.stream.LaxioInput;
import org.laxio.network.stream.LaxioOutput;
import org.laxio.packet.Packet;
import org.laxio.protocol.PacketOrigin;
import org.laxio.protocol.Protocol;
import org.laxio.protocol.ProtocolState;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public class StatefulProtocol implements Protocol {

    private final int version;

    private final Map<PacketOrigin, Map<ProtocolState, Map<Integer, Class<? extends Packet>>>> packets;
    private final Map<Class<? extends Packet>, Integer> reversePackets;

    public StatefulProtocol(int version) {
        this.version = version;
        this.packets = new EnumMap<>(PacketOrigin.class);
        for (PacketOrigin origin : PacketOrigin.values()) {
            Map<ProtocolState, Map<Integer, Class<? extends Packet>>> protocolStatePackets = new EnumMap<>(ProtocolState.class);
            for (ProtocolState state : ProtocolState.values()) {
                protocolStatePackets.put(state, new HashMap<>());
            }

            this.packets.put(origin, protocolStatePackets);
        }

        this.reversePackets = new HashMap<>();
        register();
    }

    protected void register() {
        // subclasses
    }

    protected void register(PacketOrigin origin, ProtocolState state, int packetId, Class<? extends Packet> packetType) {
        // TODO: check for overlaps
        packets.get(origin).get(state).put(packetId, packetType);
        reversePackets.put(packetType, packetId);
    }

    @Override
    public int getVersion() {
        return version;
    }

    @Override
    public Packet readPacket(Connection connection, LaxioInput input) throws IOException {
        int packetId = input.readVarInt();
        Packet packet = build(connection.getProtocolState(), packetId);
        packet.read(input);
        return packet;
    }

    @Override
    public void writePacket(Connection connection, LaxioOutput output, Packet packet) throws IOException {
        int packetId = reversePackets.get(packet.getClass());
        output.writeVarInt(packetId);
        packet.write(output);
    }

    private Packet build(ProtocolState state, int packetId) {
        Class<? extends Packet> packetType = packets.get(PacketOrigin.CLIENT).get(state).get(packetId);

        try {
            return packetType.getConstructor().newInstance();
        } catch (InstantiationException | InvocationTargetException | NoSuchMethodException | IllegalAccessException ex) {
            throw new InternalProtocolException("Unable to construct Packet", ex);
        }
    }

}
