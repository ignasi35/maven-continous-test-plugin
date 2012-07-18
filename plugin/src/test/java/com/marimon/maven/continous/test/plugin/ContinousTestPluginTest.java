package com.marimon.maven.continous.test.plugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import junit.framework.Assert;

import org.apache.maven.monitor.logging.DefaultLog;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.surefire.SurefirePlugin;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit test for {@link ContinousTestPlugin}.
 */
public class ContinousTestPluginTest {

    private ContinousTestPlugin _mojo;

    private File _tmpFolder;

    @SuppressWarnings("rawtypes")
    @Before
    public void before() {
        _mojo = new ContinousTestPlugin();
        _mojo.setPlugin(new FakeSurefirePlugin());
        _mojo.setPluginContext(new ConcurrentHashMap());

        _tmpFolder =
            new File(System.getProperty("java.io.tmpdir"),
                ContinousTestPluginTest.class.getName()
                    + Long.toString(System.currentTimeMillis()));
        _tmpFolder.mkdirs();
    }

    @After
    public void after() {
        delete(_tmpFolder);
    }

    private void delete(final File file) {
        if (file.isDirectory()) {
            File[] listFiles = file.listFiles();
            for (File f : listFiles) {
                if (f.isDirectory()) {
                    delete(f);
                }
                f.delete();
            }
        }
        file.delete();
    }

    @Test
    public void testLoggerOutputsDataRegardingThePluginWhenExecuted()
            throws Exception {

        FakeLog log = new FakeLog();
        _mojo.setLog(log);

        ExecutorService pool = Executors.newFixedThreadPool(1);
        pool.submit(new Runnable() {

            @Override
            public void run() {
                try {
                    _mojo.execute();
                } catch (Exception e) {
                }
            }
        });

        pool.awaitTermination(ContinousTestPlugin.DELAY * 12,
            TimeUnit.MILLISECONDS);

        Assert.assertEquals("Continous Test Plugin started..."
            + "1. Checking files...Starting tests...Tests completed."
            + "2. Checking files...", log.getSb().toString());
    }

    @Test
    public void testWaitsUntilAChangeIsDetectedInSourceFolders()
            throws Exception {

        FakeLog log = new FakeLog();
        _mojo.setLog(log);

        _mojo.setTestSourceDirectory(mkdir("test"));
        _mojo.setBasedir(mkdir("baseDir"));

        touch(_mojo.getTestSourceDirectory(), "SampleTest.java");
        touch(_mojo.getBasedir(), "Sample.java");

        ExecutorService pool = Executors.newFixedThreadPool(1);
        pool.submit(new Runnable() {

            @Override
            public void run() {
                try {
                    _mojo.execute();
                } catch (Exception e) {
                }
            }
        });

        Thread.sleep(ContinousTestPlugin.DELAY * 2);
        touch(_mojo.getBasedir(), "Some.java");
        Thread.sleep(ContinousTestPlugin.DELAY * 5);
        Assert.assertEquals("Continous Test Plugin started..."
            + "1. Checking files...Starting tests...Tests completed."
            + "2. Checking files...Starting tests...Tests completed."
            + "3. Checking files...", log.getSb().toString());
        pool.shutdownNow();
    }

    private void touch(final File basedir, final String name)
            throws IOException {
        File parent = new File(basedir, "src/main/java/");
        parent.mkdirs();
        File file = new File(parent, name);
        FileOutputStream out = new FileOutputStream(file);
        out.write(Long.toString(System.currentTimeMillis()).getBytes());
        out.close();
    }

    private File mkdir(final String dirname) {
        File mkdir = new File(_tmpFolder, dirname);
        mkdir.mkdirs();
        return mkdir;
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
