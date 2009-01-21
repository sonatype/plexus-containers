package org.codehaus.plexus.metadata;

import java.io.File;
import java.util.List;

public class MetadataGenerationRequest
{
    /** Source to examine for Javadoc annotions which are used to generate component metadata. */
    public List<String> sourceDirectories; //todo: these should be files

    /** The character encoding of the source files, may be {@code null} or empty to use platform's default encoding. */
    public String sourceEncoding;

    /** Classes to examine for annotations which are used to generate component metadata. */
    public File classesDirectory;  

    /** Supporting classpath required by class-based annotation processing. */
    public List<String> classpath; //todo: these should be files

    /** Flag to indicate using the context classloader for the supporting classpath required by annotation-based processing. */
    public boolean useContextClassLoader;
    
    /** Directory where existing component descriptors live. */
    public File componentDescriptorDirectory;
    
    /** Existing component descriptors that need to be merged. */
    public List<File> componentDescriptors;
    
    /** Where existing component descriptors are merged. */
    public File intermediaryFile;
    
    /** Output file for the final component descriptor. */
    public File outputFile;
}
