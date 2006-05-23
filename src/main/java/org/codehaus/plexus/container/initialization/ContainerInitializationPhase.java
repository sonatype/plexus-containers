package org.codehaus.plexus.container.initialization;

/**
 * @author Jason van Zyl
 */
public interface ContainerInitializationPhase
{
    String ROLE = ContainerInitializationPhase.class.getName();

    void execute( ContainerInitializationContext context )
        throws ContainerInitializationException;
}
