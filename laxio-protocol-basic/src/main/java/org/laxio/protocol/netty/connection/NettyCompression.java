package org.laxio.protocol.netty.connection;

import org.laxio.network.connection.Compression;

public class NettyCompression implements Compression {

    private int threshold = -1;

    @Override
    public boolean isEnabled() {
        return threshold != -1;
    }

    @Override
    public int getThreshold() {
        return threshold;
    }

    @Override
    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }

}
