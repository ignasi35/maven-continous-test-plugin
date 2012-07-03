package com.marimon.maven.dummy;

import org.junit.Assert;
import org.junit.Test;

public class SampleTest {

    @Test
    public void test() {
        int result = new Sample().run();
        Assert.assertEquals(1, result);
    }
}
