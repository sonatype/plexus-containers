package org.codehaus.plexus;

import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;

import org.codehaus.plexus.service.repository.ComponentRepository;

/**
 * Aspect to identify "component leaks".  A component leak occurs when
 * a component is looked up, but never released.
 * 
 * @author <a href="pete-apache-dev@kazmier.com">Pete Kazmier</a>
 * @version $Id$
 */
aspect ComponentLeakAspect
{
    private List components = new LinkedList();

    /**
     * This pointcut identifies the moment in time when we should
     * actually print out the results we've been tracking in this
     * aspect.
     */
    pointcut repositoryDisposal():
        call(void ComponentRepository.dispose());

    /**
     * The lookup pointcut has one subtly, we need to be carefull to
     * remember that lookup(role, id), simply calls lookup(role).  As
     * a result, we need to make sure we exclude any calls to lookup()
     * made from other calls of lookup.
     */
    pointcut lookup():
        call(Object ComponentRepository.lookup(..)) &&
        !cflow(execution(Object ComponentRepository.lookup(..)));

    /**
     * The release pointcut is straightforward.  We just need to keep
     * track of the argument so we can remove it from our list.
     */
    pointcut release(Object o):
        call(void ComponentRepository.release(Object)) && args(o);

    /**
     * The around advice is used to get a reference to the returned
     * object so we can add this to our list of components.
     */
    Object around(): lookup()
    {
        Object result = proceed();
        components.add(result);
        return result;
    }

    /**
     * The after advice here simply removes the component from our
     * list. 
     */
    after(Object o): release(o)
    {
        components.remove(o);
    }

    /**
     * We should print the results _before_ the component repository
     * disposes() because part of the component repository's disposal
     * includes the disposal of all components (and we can't be sure
     * that it doesn't call release() which would affect our count,
     * although after looking at the code, it really doesn't so it
     * wouldn't matter, but that is an implementation detail that we
     * should not know about.  The other alternative would be to
     * exclude the control flow of ComponentRepository.dispose() from
     * our release pointcut).
     */
    before(): repositoryDisposal()
    {
        System.err.println(">> Non-Released Component Count: " + components.size());
        for (Iterator i = components.iterator(); i.hasNext(); )
        {
            System.err.println(">>>> " + i.next());
        }
    }
}
