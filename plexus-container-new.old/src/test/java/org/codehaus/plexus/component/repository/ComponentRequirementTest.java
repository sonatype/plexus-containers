package org.codehaus.plexus.component.repository;

import junit.framework.TestCase;

/**
 * @author <a href="michal.maczka@dimatics.com">Michal Maczka</a> 
 * @version $Id$ 
 */
public class ComponentRequirementTest extends TestCase
{
    public void testComponentRequirement()
    {
        ComponentRequirement requirement = new ComponentRequirement();
        requirement.setFieldName( "field" );
        requirement.setRole( "role" );
        
        assertEquals(  "field", requirement.getFieldName() );
        assertEquals( "role", requirement.getRole() );
                
    }
    
}
