package org.codehaus.plexus.metadata;

import java.io.File;

public interface MetadataGenerator
{
    void generateDescriptor( ExtractorConfiguration configuration, File outputFile )
        throws Exception;

}
