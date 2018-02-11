package io.pucman.bungee.dependency;

import io.pucman.bungee.PLibrary;
import io.pucman.bungee.manager.Manager;

public class DependencyManager extends Manager<PLibrary>
{
    public DependencyManager()
    {
        super(PLibrary.get(), Priority.HIGH);
    }


}
