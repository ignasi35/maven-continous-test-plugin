package com.marimon.maven.continous.test.plugin;

import junit.framework.Assert;

import org.apache.maven.monitor.logging.DefaultLog;
import org.apache.maven.plugin.Mojo;
import org.junit.Test;

/**
 * Unit test for {@link ContinousTestPlugin}.
 */
public class ContinousTestPluginTest {

    @Test
    public void testContinousTestPluginImplementsMojoInterface()
            throws Exception {
        try {
            Mojo mojo = new ContinousTestPlugin();
            mojo.getLog();
        } catch (ClassCastException e) {
            Assert.fail("The cast should be valid.");
        }
    }

    @Test
    public void testLoggerOutputsDataRegardingThePluginWhenExecuted()
            throws Exception {
        Mojo mojo = new ContinousTestPlugin();
        FakeLog log = new FakeLog();
        mojo.setLog(log);
        mojo.execute();
        Assert.assertEquals("ContinousTestPlugin started.", log.getSb()
            .toString());
    }

    private class FakeLog extends DefaultLog {

        private final StringBuilder _sb = new StringBuilder();

        public StringBuilder getSb() {
            return _sb;
        }

        public FakeLog() {
            super(null);
        }

        @Override
        public void info(final CharSequence content) {
            _sb.append(content);
        }

        @Override
        public boolean isInfoEnabled() {
            return true;
        }
    }

}
