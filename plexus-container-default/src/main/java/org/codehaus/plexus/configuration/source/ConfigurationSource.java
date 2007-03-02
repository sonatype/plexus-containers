package org.codehaus.plexus.configuration.source;

/**
 * A source for component configurations which may reside outside the
 * configuration within a component descriptor. 
 *
 * @author Jason van Zyl
 */
public interface ConfigurationSource
{
    // validate a DOM
    // pick it apart into the right component descriptor configuration
    // let the normal configuration process occur
    // need phase to set a configuration source be applied
}
