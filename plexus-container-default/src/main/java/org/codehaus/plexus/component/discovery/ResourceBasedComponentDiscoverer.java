package org.codehaus.plexus.component.discovery;

/**
 * Component discoverer which relies on the metadata for the components residing in a resource that
 * can be found in a classloader.
 * 
 * @author jvanzyl
 */
public interface ResourceBasedComponentDiscoverer
    extends ComponentDiscoverer
{
    String getComponentDescriptorLocation();
}
