package org.codehaus.plexus.component.connector;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.CodeVisitor;
import org.objectweb.asm.Constants;
import org.objectweb.asm.Type;

import java.lang.reflect.Method;

/**
 * Generates component connectors.
 *
 * <ui>
 *  <li>two components connected by role</li>
 *  <li>look at the interface</li>
 *  <li>client and provider</li>
 * </ul>
 * <p>
 * Client making a use of the services provided by the
 * component C1 via the connector Con1.
 * </p><p>
 * <pre>
 *   Client                                 Provider
 *   +-------+                              +-------+
 *   |       |                              |       |
 *   |       | -------> Connector --------> |       |
 *   +-------+                              +-------+
 * </pre>
 * </p>
 * The connector will have the same interface as the Provider and
 * the client will be given a reference to the connector and not
 * the Provider directly.
 */
public class ConnectorGenerator
{
    private static String PROVIDER_FIELD_NAME = "provider";

    private static String BASE_CLASS = slashName( AbstractConnector.class );

    private static String CONNECTOR_INTERFACE = slashName( Connector.class );

    public Object generate( ClassLoader classLoader, Class providerInterface, Object component )
        throws Exception
    {
        String className = baseName( providerInterface ) + "Connector";

        String interfaceName = slashName( providerInterface );

        String internalInterfaceName = internalName( providerInterface );

        ClassWriter cw = new ClassWriter( true );

        String[] interfaces = new String[]{slashName( providerInterface ), CONNECTOR_INTERFACE};

        cw.visit( Constants.ACC_PUBLIC + Constants.ACC_SUPER, className, BASE_CLASS, interfaces, className );

        // Create the field

        cw.visitField( Constants.ACC_PRIVATE, PROVIDER_FIELD_NAME, internalInterfaceName, null );

        // Write the default constructor

        CodeVisitor cv = cw.visitMethod( Constants.ACC_PUBLIC, "<init>", "()V", null );

        cv.visitVarInsn( Constants.ALOAD, 0 );

        cv.visitMethodInsn( Constants.INVOKESPECIAL, BASE_CLASS, "<init>", "()V" );

        cv.visitInsn( Constants.RETURN );

        cv.visitMaxs( 0, 0 );

        // Add provider setter: void setProvider( Object provider );

        cv = cw.visitMethod( Constants.ACC_PUBLIC, "setProvider", "(Ljava/lang/Object;)V", null );

        cv.visitVarInsn( Constants.ALOAD, 0 );

        cv.visitVarInsn( Constants.ALOAD, 1 );

        cv.visitTypeInsn( Constants.CHECKCAST, slashName( providerInterface ) );

        cv.visitFieldInsn( Constants.PUTFIELD, className, PROVIDER_FIELD_NAME, internalInterfaceName );

        cv.visitInsn( Constants.RETURN );

        cv.visitMaxs( 0, 0 );

        // Write provider delegate methods.

        Method[] methods = providerInterface.getMethods();

        for ( int i = 0; i < methods.length; i++ )
        {
            Method method = methods[i];

            String methodName = method.getName();

            String methodSignature = Type.getMethodDescriptor( method );

            int parameters = method.getParameterTypes().length;

            cv = cw.visitMethod( Constants.ACC_PUBLIC, methodName, methodSignature, null );

            cv.visitVarInsn( Constants.ALOAD, 0 );

            cv.visitFieldInsn( Constants.GETFIELD, className, PROVIDER_FIELD_NAME, internalInterfaceName );

            for ( int j = 0; j < parameters; j++ )
            {
                cv.visitVarInsn( Constants.ALOAD, j + 1 );
            }

            cv.visitMethodInsn( Constants.INVOKEINTERFACE, interfaceName, methodName, methodSignature );

            cv.visitInsn( Constants.RETURN );

            cv.visitMaxs( 0, 0 );
        }

        cw.visitEnd();

        SyntheticClassLoader cl = new SyntheticClassLoader( classLoader, cw );

        Connector connector = (Connector) cl.findClass( className ).newInstance();

        connector.setProvider( component );

        return connector;
    }

    private static String baseName( Class clazz )
    {
        String s = clazz.getName();

        return s.substring( s.lastIndexOf( "." ) + 1 );
    }


    private static String internalName( Class clazz )
    {
        String s = clazz.getName();

        return "L" + s.replace( '.', '/' ) + ";";
    }

    private static String slashName( Class clazz )
    {
        String s = clazz.getName();

        return s.replace( '.', '/' );
    }


    public class SyntheticClassLoader
        extends ClassLoader
    {
        private ClassWriter classWriter;

        public SyntheticClassLoader( ClassLoader parent, ClassWriter classWriter )
        {
            super( parent );

            this.classWriter = classWriter;
        }

        protected Class findClass( String className )
            throws ClassNotFoundException
        {
            byte[] bytes = classWriter.toByteArray();

            return defineClass( className, bytes, 0, bytes.length );
        }
    }
}
