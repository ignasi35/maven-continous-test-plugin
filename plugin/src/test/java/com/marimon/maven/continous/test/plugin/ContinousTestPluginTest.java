package com.marimon.maven.continous.test.plugin;

import java.util.concurrent.ConcurrentHashMap;

import junit.framework.Assert;

import org.apache.maven.monitor.logging.DefaultLog;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.Mojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.surefire.SurefirePlugin;
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

    @SuppressWarnings("rawtypes")
    @Test
    public void testLoggerOutputsDataRegardingThePluginWhenExecuted()
            throws Exception {
        AbstractMojo mojo =
            new ContinousTestPlugin(new FakeSurefirePlugin());
        mojo.setPluginContext(new ConcurrentHashMap());
        FakeLog log = new FakeLog();
        mojo.setLog(log);
        mojo.execute();
        Assert.assertEquals(
            "Continous Test Plugin started...Checking files..."
                + "Files checked. 0Checking files...Files checked. 1"
                + "Checking files...Files checked. 2"
                + "Checking files...Files checked. 3"
                + "Checking files...Files checked. 4"
                + "Checking files...Files checked. 5"
                + "Checking files...Files checked. 6"
                + "Checking files...Files checked. 7"
                + "Checking files...Files checked. 8"
                + "Checking files...Files checked. 9"
                + "Continous Test Plugin completed.", log.getSb()
                .toString());
    }

    private class FakeSurefirePlugin extends SurefirePlugin {
        @Override
        public void execute()
                throws MojoExecutionException, MojoFailureException {
        }
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
