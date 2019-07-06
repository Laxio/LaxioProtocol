package org.laxio.protocol.netty;

import org.laxio.LaxioApplication;
import org.laxio.thread.LaxioThreadGroup;

public class TestLaxioApplication implements LaxioApplication {

    private final String name;
    private final LaxioThreadGroup threadGroup;

    public TestLaxioApplication(String name) {
        this.name = name;
        this.threadGroup = new LaxioThreadGroup(this);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ThreadGroup getThreadGroup() {
        return threadGroup;
    }

}
