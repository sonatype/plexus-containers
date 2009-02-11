package org.codehaus.plexus.component.repository;

import java.util.List;

public interface ComponentDescriptorListener<T>
{
    Class<T> getType();

    List<String> getRoleHints();

    void componentDescriptorAdded(ComponentDescriptor<? extends T> componentDescriptor);

    void componentDescriptorRemoved(ComponentDescriptor<? extends T> componentDescriptor);
}
