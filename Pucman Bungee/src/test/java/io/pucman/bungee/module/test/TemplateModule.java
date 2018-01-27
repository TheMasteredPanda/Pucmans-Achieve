package io.pucman.bungee.module.test;

import io.pucman.module.Module;
import io.pucman.module.ModuleInfo;

import java.util.UUID;

public class TemplateModule implements Module
{
    private ModuleInfo info;
    private boolean enable = false;

    public TemplateModule(String name, String version)
    {
        info = new ModuleInfo(new String[] {UUID.randomUUID().toString()}, version, name, true);
    }

    @Override
    public ModuleInfo getInfo()
    {
        return null;
    }

    @Override
    public void boot()
    {
        System.out.println("Booted module " + info.getName() + ".");
        enable = true;
    }

    @Override
    public void shutdown()
    {
        System.out.println("Shutdown module " + info.getName() + ".");
        enable = false;
    }

    @Override
    public boolean isEnabled()
    {
        return enable;
    }
}
