package org.laxio.protocol.netty.protocol;

import org.laxio.Application;
import org.laxio.protocol.Protocol;
import org.laxio.protocol.ProtocolRegistry;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class BasicProtocolRegistry implements ProtocolRegistry {

    private final Application application;
    private final Map<Integer, Protocol> protocols;

    public BasicProtocolRegistry(Application application) {
        this.application = application;
        this.protocols = new ConcurrentHashMap<>();
    }

    @Override
    public Application getApplication() {
        return application;
    }

    @Override
    public void register(Protocol protocol) {
        protocols.put(protocol.getProtocolVersion(), protocol);
        protocol.onProtocolEnable(application);
    }

    @Override
    public Optional<Protocol> findByVersion(int version) {
        return Optional.ofNullable(protocols.get(version));
    }

}
