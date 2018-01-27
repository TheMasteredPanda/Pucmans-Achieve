package io.pucman.bungee.module;

import io.pucman.bungee.PLibrary;
import org.junit.Before;
import org.junit.Test;

public class ModuleManagerTest
{
    private ModuleManager m;


    @Before
    public void load()
    {
        m = new ModuleManager(PLibrary.get());
    }

    @Test
    public void testLoadOrder()
    {
        m.getLoadOrder().keySet().forEach(m::boot);
    }

    public void testShutdownOrder()
    {

    }
}
