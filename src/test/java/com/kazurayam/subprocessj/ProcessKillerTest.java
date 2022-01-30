package com.kazurayam.subprocessj;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class ProcessKillerTest {

    HiThereServer server = null;

    @BeforeEach
    public void setup() throws IOException {
        try {
            server = new HiThereServer();
        } catch (java.net.BindException e) {
            e.printStackTrace();
        }
    }

    @AfterEach
    public void tearDown() {
        if (server != null) {
            server.shutdown();
            server = null;
        }
    }

    @Test
    public void test_findIdOfProcessOnIpPort_Mac()
            throws IOException, InterruptedException
    {
        Long processId = ProcessKiller.findProcessIdOnPort(8500);
        assert processId > 0;
    }

    @Disabled
    @Test
    public void test_killProcessOnPort()
            throws IOException, InterruptedException
    {
        Long processId = ProcessKiller.killProcessOnPort(8500);
        assert processId > 0;
    }
}
