package io.pucman.module;

import lombok.Getter;

/**
 * Holds all module metadata.
 */
public class ModuleInfo
{
    /**
     * An immutable array of developers who helped write or contributed to this module.
     */
    @Getter
    private String[] authors;

    /**
     * Module version.
     */
    private String version;

    /**
     * Name of module.
     */
    private String name;
}
