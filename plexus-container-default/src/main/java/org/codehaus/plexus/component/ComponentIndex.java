package org.codehaus.plexus.component;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import org.codehaus.plexus.classworlds.realm.ClassRealm;
import static org.codehaus.plexus.PlexusConstants.PLEXUS_DEFAULT_HINT;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Iterator;
import java.util.Collections;

public class ComponentIndex<V>
{
    /**
     * Should values be indexed by all types or simply the supplied type?
     */
    private final boolean indexByAllTypes;

    /**
     * This is the actual index.
     * ClassLoader -> Class -> RoleHint -> Value
     */
    private final Map<ClassLoader, SortedMap<Class<?>, Multimap<String, V>>> index =
        new LinkedHashMap<ClassLoader, SortedMap<Class<?>, Multimap<String, V>>>();

    /**
     * Creates a component index that indexes by all super types and interfaces of supplied type.
     */
    public ComponentIndex()
    {
        this( true );
    }

    /**
     * Creates a component index.
     * @param indexByAllTypes if true, values are indexed by all super types and interfaces of supplied type; otherwise
     * values are only indexed by supplied type
     */
    public ComponentIndex( boolean indexByAllTypes )
    {
        this.indexByAllTypes = indexByAllTypes;
    }

    /**
     * Are values are indexed by all super types and interfaces of supplied type?
     * @return true, values are indexed by all super types and interfaces of supplied type; otherwise
     * false and values are only indexed by supplied type
     */
    public boolean isIndexByAllTypes()
    {
        return indexByAllTypes;
    }

    /**
     * Gets the value associated with the specified type and roleHint.
     *
     * Values are searched for in classloader order starting from the thread context class loader or type class loader
     * if thread context class loader is not set.
     *
     * @param type the type (or super type if enabled) associated with the value; not null
     * @param roleHint the roleHint associated with the value, or null for the default roleHint
     * @return the value associated with the type and roleHint, or null
     */
    public synchronized V get( Class<?> type, String roleHint )
    {
        return get( type, roleHint, Thread.currentThread().getContextClassLoader() );
    }

    /**
     * Gets the value associated with the specified type and roleHint.
     *
     * Values are searched for in classloader order starting from the specified class loader, or thread context class
     * loader or type class loader if specified class loader is null.
     *
     * @param type the type (or super type if enabled) associated with the value; not null
     * @param roleHint the roleHint associated with the value, or null for the default roleHint
     * @param classLoader the class loader to search from
     * @return the value associated with the type and roleHint, or null
     */
    public synchronized V get( Class<?> type, String roleHint, ClassLoader classLoader )
    {
        if ( type == null )
        {
            throw new NullPointerException( "type is null" );
        }
        if ( roleHint == null )
        {
            roleHint = PLEXUS_DEFAULT_HINT;
        }

        Collection<V> values = findAll( type, classLoader ).get( roleHint );
        if ( values.isEmpty() )
        {
            return null;
        }
        return values.iterator().next();
    }

    public synchronized Collection<V> getAll( )
    {
        ArrayList<V> values = new ArrayList<V>();

        for ( SortedMap<Class<?>, Multimap<String, V>> roleIndex : index.values() )
        {
            for ( Multimap<String, V> roleHintIndex : roleIndex.values() )
            {
                values.addAll(roleHintIndex.values());
            }
        }
        return values;
    }

    /**
     * Gets all values associated with the specified type.
     *
     * Values are searched for in classloader order starting from the thread context class loader or type class loader
     * if thread context class loader is not set.
     *
     * The values are sorted in class loader search order then by registration order.
     *
     * @param type the type (or super type if enabled) associated with the value; not null
     * @return all values associated with the type; never null
     */
    public synchronized List<V> getAll( Class<?> type )
    {
        return getAll( type, Thread.currentThread().getContextClassLoader() );
    }

    /**
     * Gets all values associated with the specified type.
     *
     * Values are searched for in classloader order starting from the specified class loader, or thread context class
     * loader or type class loader if specified class loader is null.
     *
     * The values are sorted in class loader search order then by registration order.
     *
     * @param type the type (or super type if enabled) associated with the value; not null
     * @param classLoader the class loader to search from
     * @return all values associated with the type; never null
     */
    public synchronized List<V> getAll( Class<?> type, ClassLoader classLoader )
    {
        if ( type == null )
        {
            throw new NullPointerException( "type is null" );
        }

        return new ArrayList<V>( findAll( type, classLoader ).values() );
    }

    /**
     * Gets a map of all values associated with the specified type indexed by roleHint.
     *
     * Values are searched for in classloader order starting from the thread context class loader or type class loader
     * if thread context class loader is not set.
     *
     * @param type the type (or super type if enabled) associated with the value; not null
     * @return all of the value associated with the type; never null
     */
    public synchronized Map<String, V> getAllAsMap( Class<?> type )
    {
        return getAllAsMap( type, Thread.currentThread().getContextClassLoader() );
    }

    /**
     * Gets a map of all values associated with the specified type indexed by roleHint.
     *
     * Values are searched for in classloader order starting from the specified class loader, or thread context class
     * loader or type class loader if specified class loader is null.
     *
     * @param type the type (or super type if enabled) associated with the value; not null
     * @param classLoader the class loader to search from
     * @return all of the value associated with the type; never null
     */
    public synchronized Map<String, V> getAllAsMap( Class<?> type, ClassLoader classLoader )
    {
        if ( type == null )
        {
            throw new NullPointerException( "type is null" );
        }

        Map<String, V> descriptors = new TreeMap<String, V>();
        for ( Entry<String, V> entry : findAll( type, classLoader ).entries() )
        {
            if ( !descriptors.containsKey( entry.getKey() ) )
            {
                descriptors.put( entry.getKey(), entry.getValue() );
            }
        }
        return descriptors;
    }

    private synchronized Multimap<String, V> findAll( Class<?> type, ClassLoader classLoader )
    {
         if ( classLoader == null )
        {
            classLoader = type.getClassLoader();
        }

        // Determine class loaders to search
        LinkedHashSet<ClassLoader> classLoaders = new LinkedHashSet<ClassLoader>();
        for ( ClassLoader cl = classLoader; cl != null; cl = cl.getParent() )
        {
            if ( cl instanceof ClassRealm )
            {
                ClassRealm realm = (ClassRealm) cl;
                while ( realm != null )
                {
                    classLoaders.add( realm );
                    realm = realm.getParentRealm();
                }
            }
            else
            {
                // todo lots of plexus code depends on a global search when there is a class loader associated with
                // the thread but the cl is not a class realm
                // classLoaders.add( cl );
            }
        }
        // todo remove this when plexus code is updated to manage thread context class loader correctly
        if ( classLoaders.isEmpty() )
        {
            classLoaders.addAll( index.keySet() );
        }

        // Get all valid component descriptors
        Multimap<String, V> roleHintIndex = Multimaps.newHashMultimap();
        for ( ClassLoader cl : classLoaders )
        {
            SortedMap<Class<?>, Multimap<String, V>> roleIndex = index.get( cl );
            if ( roleIndex != null )
            {
                Multimap<String, V> values = roleIndex.get( type );
                if ( values != null )
                {
                    roleHintIndex.putAll( values );
                }
            }
        }
        return Multimaps.unmodifiableMultimap( roleHintIndex );
    }

    /**
     * Associate a value with the specified class loader, type and roleHint.  The value is also associated with all
     * superclasses and interfaces of the specified type unless index by all types is disabled.
     */
    public synchronized void add( ClassLoader classLoader, Class<?> type, String roleHint, V value )
    {
        if ( classLoader == null )
        {
            throw new NullPointerException( "classLoader is null" );
        }
        if ( type == null )
        {
            throw new NullPointerException( "type is null" );
        }
        if ( roleHint == null )
        {
            roleHint = PLEXUS_DEFAULT_HINT;
        }
        if ( value == null )
        {
            throw new NullPointerException( "value is null" );
        }

        SortedMap<Class<?>, Multimap<String, V>> roleIndex = index.get( classLoader );
        if ( roleIndex == null )
        {
            roleIndex = new TreeMap<Class<?>, Multimap<String, V>>( ClassComparator.INSTANCE );
            index.put( classLoader, roleIndex );
        }

        for ( Class<?> clazz : getAllTypes( type ) )
        {
            Multimap<String, V> roleHintIndex = roleIndex.get( clazz );
            if ( roleHintIndex == null )
            {
                roleHintIndex = new ArrayListMultimap<String, V>();
                roleIndex.put( clazz, roleHintIndex );
            }
            roleHintIndex.put( roleHint, value );
        }
    }

    /**
     * Removes the specified value from the index.  This is operation requires a linear search of the whole index, and
     * is therefor very expensive.
     * @param value the value to remove
     */
    public synchronized void remove( V value )
    {
        if ( value == null )
        {
            throw new NullPointerException( "value is null" );
        }

        for ( SortedMap<Class<?>, Multimap<String, V>> roleIndex : index.values() )
        {
            for ( Multimap<String, V> roleHintIndex : roleIndex.values() )
            {
                for ( Iterator<V> iterator = roleHintIndex.values().iterator(); iterator.hasNext(); )
                {
                    V v = iterator.next();
                    if ( value.equals( v ) )
                    {
                        iterator.remove();
                    }
                }
            }
        }
    }

    /**
     * Removes all values associated with the specified class loader.  This operation is very fast.
     */
    public synchronized List<V> removeAll( ClassLoader classLoader )
    {
        if ( classLoader == null )
        {
            throw new NullPointerException( "classLoader is null" );
        }

        ArrayList<V> values = new ArrayList<V>();

        SortedMap<Class<?>, Multimap<String, V>> roleIndex = index.remove( classLoader );
        for ( Multimap<String, V> roleHintIndex : roleIndex.values() )
        {
            values.addAll(roleHintIndex.values());
        }
        return values;
    }

    /**
     * Removes all values from this index.
     */
    public synchronized void clear()
    {
        index.clear();
    }

    private Set<Class<?>> getAllTypes( Class<?> type )
    {
        if ( type.isArray() )
        {
            throw new IllegalArgumentException( "type is an array: type=" + type );
        }

        // if we are not indexing by all types, simply return a set containing the source type
        if ( !indexByAllTypes )
        {
            return Collections.<Class<?>>singleton( type );
        }

        // All found types
        Set<Class<?>> allTypes = new LinkedHashSet<Class<?>>();

        // Types that must still be processed... may contain entries that
        // have already been added to allTypes, so check all types before
        // actuall processing to avoid infinite loops
        LinkedList<Class<?>> typesToProcess = new LinkedList<Class<?>>();
        typesToProcess.add( type );

        while ( !typesToProcess.isEmpty() )
        {
            Class<?> clazz = typesToProcess.removeFirst();

            // have we already processed this type
            if ( !allTypes.contains( clazz ) )
            {
                allTypes.add( clazz );

                // schedule superclass for processing
                Class<?> superclass = clazz.getSuperclass();
                if ( superclass != null )
                {
                    typesToProcess.addFirst( superclass );
                }

                // schedule all interfaces for processing
                typesToProcess.addAll( 0, Arrays.<Class<?>>asList( clazz.getInterfaces() ) );
            }
        }
        return allTypes;
    }

    private static final class ClassComparator implements Comparator<Class<?>>, Serializable
    {
        private static final ClassComparator INSTANCE = new ClassComparator();

        public int compare( Class<?> class1, Class<?> class2 )
        {
            return class1.getName().compareTo( class2.getName() );
        }
    }
}
