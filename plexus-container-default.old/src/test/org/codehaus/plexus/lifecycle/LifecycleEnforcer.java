package org.codehaus.plexus.lifecycle;

/**
 * @author kaz
 */
public interface LifecycleEnforcer
{
    public static final String ROLE = LifecycleEnforcer.class.getName();
    
    public boolean isLogged();
    public boolean isContextualized();
    public boolean isServiced();
    public boolean isConfigured();
    //public boolean isParameterized();
    public boolean isInitialized();
    public boolean isStarted();
    public boolean isStopped();
    public boolean isDisposed();    
}
