package org.codehaus.plexus.metadata;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.tools.cli.AbstractCli;

public class PlexusMetadataGeneratorCli
    extends AbstractCli
{
    public static final char SOURCE_DIRECTORY = 's';
    public static final char CLASSES_DIRECTORY = 'c';
    public static final char OUTPUT_FILE = 'o';

    public static void main( String[] args )
        throws Exception
    {
        new PlexusMetadataGeneratorCli().execute( args );
    }

    @Override
    public String getPomPropertiesPath()
    {
        return "META-INF/maven/org.codehaus.plexus/plexus-metadata-generator/pom.properties";
    }

    @Override
    @SuppressWarnings("static-access")
    public Options buildCliOptions( Options options )
    {
        options.addOption( OptionBuilder.withLongOpt( "source" ).hasArg().withDescription( "Source directory." ).create( SOURCE_DIRECTORY ) );
        options.addOption( OptionBuilder.withLongOpt( "classes" ).hasArg().withDescription( "Classes directory." ).create( CLASSES_DIRECTORY ) );
        options.addOption( OptionBuilder.withLongOpt( "output" ).hasArg().withDescription( "Output directory." ).create( OUTPUT_FILE ) );
        return options;
    }    

    public void invokePlexusComponent( CommandLine cli, PlexusContainer plexus )
        throws Exception
    {
        MetadataGenerator mg = (MetadataGenerator) plexus.lookup( MetadataGenerator.class );
        ExtractorConfiguration extractorConfiguration = new ExtractorConfiguration();        
        extractorConfiguration.classesDirectory = new File( cli.getOptionValue( CLASSES_DIRECTORY ) );
        extractorConfiguration.classpath = Collections.EMPTY_LIST;
        extractorConfiguration.sourceDirectories = Arrays.asList( new String[]{ new File( cli.getOptionValue( SOURCE_DIRECTORY ) ).getAbsolutePath() } );
        extractorConfiguration.useContextClassLoader = true;
        mg.generateDescriptor( extractorConfiguration, new File( cli.getOptionValue( OUTPUT_FILE ) ) );
    }
}
