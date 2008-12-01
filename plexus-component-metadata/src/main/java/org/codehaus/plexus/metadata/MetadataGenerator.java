package org.codehaus.plexus.metadata;

public interface MetadataGenerator
{
    void generateDescriptor( MetadataGenerationRequest configuration )
        throws Exception;

}
