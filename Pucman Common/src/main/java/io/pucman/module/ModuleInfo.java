package io.pucman.module;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Holds all module metadata.
 */
@Getter
@AllArgsConstructor
public class ModuleInfo
{
    /**
     * An immutable array of developers who helped write or contributed to this module.
     */
    private String[] authors;

    /**
     * Module version.
     */
    private String version;

    /**
     * Name of module.
     */
    private String name;

    /**
     * If this is true, then this module cannot be disabled non-programmatically. If it
     * is false, then the module can be disabled non-programmatically.
     */
    private boolean immutableModule;
}
