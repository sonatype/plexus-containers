package org.codehaus.plexus.component;

import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public final class ComponentStack
{
    private static final ThreadLocal<LinkedList<ComponentStackElement>> STACK =
        new ThreadLocal<LinkedList<ComponentStackElement>>()
        {
            protected LinkedList<ComponentStackElement> initialValue()
            {
                return new LinkedList<ComponentStackElement>();
            }
        };

    public static List<ComponentStackElement> getComponentStack()
    {
        LinkedList<ComponentStackElement> stack = STACK.get();
        ArrayList<ComponentStackElement> copy = new ArrayList<ComponentStackElement>( stack.size() );
        for ( ComponentStackElement element : stack )
        {
            copy.add( new ComponentStackElement( element ) );
        }
        return Collections.unmodifiableList( copy );
    }

    public static void pushComponentStack( ComponentDescriptor<?> descriptor ) throws ComponentLookupException
    {
        if ( descriptor == null )
        {
            throw new IllegalArgumentException( "descriptor is null" );
        }

        LinkedList<ComponentStackElement> stack = STACK.get();
        for ( ComponentStackElement element : stack )
        {
            if ( descriptor.equals( element.getDescriptor() ) )
            {
                // HACK: add descriptor to stack before creating exception so it is in the stack
                stack.addFirst( new ComponentStackElement( descriptor ) );
                ComponentLookupException exception = new ComponentLookupException( "Creation circularity", descriptor );
                stack.removeFirst();

                throw exception;
            }
        }

        stack.addFirst( new ComponentStackElement( descriptor ) );
    }

    public static void popComponentStack()
    {
        STACK.get().removeFirst();
    }

    public static void setComponentStackProperty( String property )
    {
        LinkedList<ComponentStackElement> stackElementLinkedList = STACK.get();
        if (stackElementLinkedList.isEmpty()) {
            return;
        }

        ComponentStackElement element = stackElementLinkedList.getLast();
        element.setProperty( property );
    }

    private ComponentStack()
    {
    }
}
