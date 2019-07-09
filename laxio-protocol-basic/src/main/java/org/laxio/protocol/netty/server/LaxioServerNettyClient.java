package org.laxio.protocol.netty.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import org.laxio.Application;
import org.laxio.authentication.ConnectionProfile;
import org.laxio.chat.ChatColor;
import org.laxio.chat.MessageBuilder;
import org.laxio.chat.MessageComponent;
import org.laxio.event.packet.PacketInboundEvent;
import org.laxio.event.packet.PacketOutboundEvent;
import org.laxio.event.status.ServerListPingEvent;
import org.laxio.network.connection.Compression;
import org.laxio.network.connection.Connection;
import org.laxio.network.encryption.Encryption;
import org.laxio.packet.Packet;
import org.laxio.protocol.Protocol;
import org.laxio.protocol.ProtocolState;
import org.laxio.protocol.netty.HandshakingProtocol;
import org.laxio.protocol.netty.connection.NettyCompression;
import org.laxio.protocol.netty.connection.NettyEncryption;
import org.laxio.protocol.netty.packet.client.handshake.HandshakeClientHandshakePacket;
import org.laxio.protocol.netty.packet.client.status.StatusClientPingPacket;
import org.laxio.protocol.netty.packet.client.status.StatusClientRequestPacket;
import org.laxio.protocol.netty.packet.server.login.LoginServerDisconnectPacket;
import org.laxio.protocol.netty.packet.server.status.StatusServerPongPacket;
import org.laxio.protocol.netty.packet.server.status.StatusServerResponsePacket;
import org.laxio.protocol.netty.pipeline.ChannelInboundMessageAdapter;
import org.laxio.protocol.status.ServerListResponse;
import org.laxio.protocol.status.ServerListResponseDescription;
import org.laxio.protocol.status.ServerListResponsePlayers;
import org.laxio.protocol.status.ServerListResponsePlayersRecord;
import org.laxio.protocol.status.ServerListResponseVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LaxioServerNettyClient extends ChannelInboundMessageAdapter<Packet> implements Connection {

    private static final Logger LOGGER = LoggerFactory.getLogger(LaxioServerNettyClient.class);

    private final Application application;
    private final Compression compression;
    private final Encryption encryption;

    private InetSocketAddress address;
    private ChannelHandlerContext context;
    private Channel channel;

    private ConnectionProfile profile;
    private ProtocolState protocolState;
    private int protocolVersion;
    private String providedHost;
    private int providedPort;
    private Protocol protocol;

    public LaxioServerNettyClient(Application application) {
        this.application = application;
        this.compression = new NettyCompression();
        this.encryption = new NettyEncryption(this);
        this.protocolState = ProtocolState.HANDSHAKE;
        this.protocolVersion = -1;
        this.protocol = HandshakingProtocol.INSTANCE;
    }

    @Override
    public Application getApplication() {
        return application;
    }

    @Override
    public InetSocketAddress getAddress() {
        return address;
    }

    public ChannelHandlerContext getContext() {
        return context;
    }

    public Channel getChannel() {
        return channel;
    }

    @Override
    public Compression getCompression() {
        return compression;
    }

    @Override
    public Encryption getEncryption() {
        return encryption;
    }

    @Override
    public ConnectionProfile getProfile() {
        return profile;
    }

    @Override
    public void setProfile(ConnectionProfile profile) {
        this.profile = profile;
    }

    @Override
    public ProtocolState getProtocolState() {
        return protocolState;
    }

    @Override
    public void setProtocolState(ProtocolState protocolState) {
        this.protocolState = protocolState;
    }

    @Override
    public int getProtocolVersion() {
        return protocolVersion;
    }

    public void setProtocolVersion(int protocolVersion) {
        this.protocolVersion = protocolVersion;
    }

    @Override
    public Protocol getProtocol() {
        return protocol;
    }

    @Override
    public void setProtocol(Protocol protocol) {
        this.protocol = protocol;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);

        this.channel = ctx.channel();
        this.context = ctx;
        this.address = (InetSocketAddress) this.channel.remoteAddress();

        LOGGER.info("Connected: {}", this.address);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);

        LOGGER.info("Disconnected: {}", this.address);
    }

    @Override
    public void onMessage(ChannelHandlerContext ctx, Packet packet) throws Exception {
        packet.setConnection(this);
        PacketInboundEvent inboundEvent = new PacketInboundEvent(application, packet, this);
        application.getListenerManager().call(inboundEvent);
        if (inboundEvent.isDropped()) {
            LOGGER.info("Dropping Inbound Packet: {}", packet);
            return;
        }

        application.getListenerManager().call(packet);

        // TODO: change this completely
        if (packet instanceof HandshakeClientHandshakePacket) {
            HandshakeClientHandshakePacket handshake = (HandshakeClientHandshakePacket) packet;
            setProtocolState(handshake.getNextState());
            setProtocolVersion(handshake.getProtocolVersion());

            providedHost = handshake.getServerAddress();
            providedPort = handshake.getServerPort();

            if (protocolState != ProtocolState.STATUS) {
                Optional<Protocol> optProtocol = application.getProtocolRegistry().findByVersion(protocolVersion);
                if (optProtocol.isPresent()) {
                    setProtocol(optProtocol.get());
                } else {
                    disconnect(MessageBuilder.builder().message(ChatColor.RED + "Unsupported Minecraft version").build());
                }
            }
        } else if (packet instanceof StatusClientRequestPacket) {
            ServerListPingEvent event = new ServerListPingEvent(application, getAddress(), getProtocolVersion(), providedHost, providedPort);
            application.getListenerManager().call(event);

            List<ServerListResponsePlayersRecord> playersRecordList = new ArrayList<>();
            sendPacket(new StatusServerResponsePacket(
                new ServerListResponse(
                    new ServerListResponseVersion(event.getProtocolVersionName(), event.getProtocolVersion()),
                    new ServerListResponsePlayers(
                            event.getMaxPlayers(),
                            event.getOnlinePlayers(),
                            playersRecordList
                    ),
                    new ServerListResponseDescription(event.getDescription()),
                    null
                )
            ));
        } else if (packet instanceof StatusClientPingPacket) {
            sendPacket(new StatusServerPongPacket(((StatusClientPingPacket) packet).getPayload()));
        }
    }

    @Override
    public void sendPacket(Packet packet) {
        PacketOutboundEvent outboundEvent = new PacketOutboundEvent(application, packet, this);
        application.getListenerManager().call(outboundEvent);
        if (outboundEvent.isDropped()) {
            LOGGER.info("Dropping Outbound Packet: {}", packet);
            return;
        }

        if (channel.eventLoop().inEventLoop()) {
            channel.writeAndFlush(packet);
        } else {
            channel.eventLoop().execute(() -> channel.writeAndFlush(packet));
        }
    }

    @Override
    public void disconnect(MessageComponent reason) {
        if (protocolState == ProtocolState.LOGIN) {
            sendPacket(new LoginServerDisconnectPacket(reason));
        } else if (protocolState == ProtocolState.PLAY) {
            // some other disconnect
        }
    }

    @Override
    public void disconnect(Throwable throwable) {
        StringBuilder builder = new StringBuilder();
        builder.append(ChatColor.AQUA).append("Error in communication\n\n");
        builder.append(ChatColor.RED).append(throwable.getClass().getName());
        if (throwable.getMessage() != null) {
            builder.append(ChatColor.WHITE).append("\n").append(throwable.getMessage());
        }

        disconnect(MessageBuilder.builder().message(builder.toString()).build());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        LOGGER.error("Issue with Connection", cause);
        disconnect(cause);
    }

}
