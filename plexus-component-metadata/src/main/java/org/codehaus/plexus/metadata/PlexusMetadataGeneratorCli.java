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
    public static final char DIRECTORY = 'd';
    public static final char OUTPUT = 'o';

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
        options.addOption( OptionBuilder.withLongOpt( "directory" ).hasArg().withDescription( "Project directory." ).create( DIRECTORY ) );
        options.addOption( OptionBuilder.withLongOpt( "output" ).hasArg().withDescription( "Output directory." ).create( OUTPUT ) );
        return options;
    }

    @Override
    public void invokePlexusComponent( final CommandLine cli, PlexusContainer plexus )
        throws Exception
    {
        MetadataGenerator mg = (MetadataGenerator) plexus.lookup( MetadataGenerator.class );
        File directory = new File( cli.getOptionValue( DIRECTORY ) );
        ExtractorConfiguration ec = new ExtractorConfiguration();
        ec.outputDirectory = new File( directory, "target/classes" );
        ec.classpath = Collections.EMPTY_LIST;
        ec.sourceDirectories = Arrays.asList( new String[]{ new File( directory, "src/main/java" ).getAbsolutePath() } );
        File output = new File( cli.getOptionValue( OUTPUT ) );
        mg.generateDescriptor( ec, output );
    }
}
