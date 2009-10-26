package org.codehaus.plexus.component.configurator;

import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.builder.XBeanComponentBuilder;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluator;
import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.apache.xbean.recipe.ConstructionException;
import org.apache.xbean.recipe.ObjectRecipe;
import org.apache.xbean.recipe.ExecutionContext;
import org.apache.xbean.recipe.DefaultExecutionContext;

public class XBeanComponentConfiguratorTest extends AbstractComponentConfiguratorTest {
    @Override
    protected void configureComponent(Object component, ComponentDescriptor descriptor, ClassRealm realm) throws Exception {
        XBeanComponentBuilder componentBuilder = new XBeanComponentBuilder();
        ObjectRecipe recipe = componentBuilder.createObjectRecipe( component, descriptor, realm);

        // need a caller context
        ExecutionContext executionContext = new DefaultExecutionContext();
        executionContext.push(new ObjectRecipe(component.getClass()));

        // call the recipie setProperties directly, but setup the thead state first
        ClassLoader oldClassLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(realm);
        ExecutionContext.setContext(executionContext);
        try
        {
            recipe.setProperties( component );
        }
        catch ( ConstructionException e )
        {
            throw new ComponentConfigurationException( "Failed to configure component", e );
        }
        finally
        {
            ExecutionContext.setContext(null);
            Thread.currentThread().setContextClassLoader(oldClassLoader);
        }
    }

    protected void configureComponent(Object component, ComponentDescriptor descriptor, ClassRealm realm, ExpressionEvaluator expressionEvaluator) throws Exception {
        this.configureComponent(component, descriptor, realm);    
    }

    public void testComponentConfigurationWithPropertiesFieldsWithExpression() throws Exception {
        // expression evalator is not supported since it is not used by normal AutoConfigurePhase
    }

    public void testComponentConfigurationWithPropertiesFieldsWithExpressions() throws Exception {
        // expression evalator is not supported since it is not used by normal AutoConfigurePhase
    }

    public void testComponentConfigurationWithAmbiguousExpressionValue()
        throws Exception
    {
        // expression evalator is not supported since it is not used by normal AutoConfigurePhase
    }

    public void testComponentConfigurationWithPrimitiveValueConversion()
        throws Exception
    {
        // expression evalator is not supported since it is not used by normal AutoConfigurePhase
    }

    public void testComponentConfigurationWithUnresolvedExpressionContentForCompositeFieldOfNonInstantiatableType()
        throws Exception
    {
        // expression evalator is not supported since it is not used by normal AutoConfigurePhase
    }

    protected ComponentConfigurator getComponentConfigurator() throws Exception {
        // this should never be called because the configureComponent is overridden
        throw new UnsupportedOperationException();
    }
}
