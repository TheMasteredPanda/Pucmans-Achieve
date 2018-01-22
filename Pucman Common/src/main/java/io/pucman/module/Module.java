package io.pucman.module;

/**
 * Base Interface Module.
 *
 * The Modules Framework would be used in plugins that supply
 * two or more features that do not have any relation to each-
 * -other. Think of a module as a plugin within a plugin. If
 * onc of this modules doesn't boot up properly the other
 * one should remain untouched.
 *
 * This design comes with several desirable development traits
 * as it will feature high cohesion with low coupling.
 */
public interface Module
{
    /**
     * Module information.
     * @return information instance.
     */
    ModuleInfo getInfo();

    /**
     * Invoked when booting up the module.
     */
    void boot();

    /**
     * Invoked when shutting down the module.
     */
    void shutdown();

    /**
     * @return true if the module is enabled, otherwise false.
     */
    boolean isEnabled();

    /**
     * @return true if the module is loaded, otherwise false.
     */
    boolean isLoaded();
}
