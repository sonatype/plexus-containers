package org.codehaus.plexus.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


/**
 * Concurrent reads, but synchronized writes. Wraps a <code>HashMap</code>
 *
 * <p>Writes will block new reads, and current reads will block new writes<p>
 *
 * <p>Due to the extra overhead of locks, this map should only be used if there are
 * far more reads then writes. If the ratio is about equal then just creating a
 * synchronized Map would be faster</p>
 *
 * <p>The iterators returned by all of this class's "collection view methods" are
 * fail-fast: if the map is structurally modified at any time after the iterator is
 * created, in any way except through the iterator's own remove or add methods,
 * the iterator will throw a ConcurrentModificationException.</p>
 *
 * <p>Created on 17/06/2003</p>
 *
 *
 * @author <a href="mailto:bert@tuaworks.co.nz">Bert van Brakel</a>
 * @version $Revision$
 */
public class ThreadSafeMap implements Map
{
    /**
     * The Map holding the entries.
     */
    private Map objects;

    /**
     * How many readers are currently reading the Map
     */
    private long readers = 0;

    /**
     * Whether a write lock, or write request is active
     */
    private boolean writeLock = false;

    /**
     * The object synchronized against. Seperate object used and not this
     * Map manager because we don't know if external code is going to
     * synchronize on this Map and stuff things up.
     */
    private Object lock = new Object();
    private ThreadSafeSet keySet = null;
    private ThreadSafeSet entrySet = null;
    private ThreadSafeCollection values = null;

    /**
     * Constructor
     *
     *
     */
    public ThreadSafeMap()
    {
        super();
        objects = new HashMap();
    }

    public Set entrySet()
    {
        if ( entrySet == null )
        {
            entrySet = new ThreadSafeSet( this, objects.entrySet() );
        }

        return entrySet;
    }

    public Set keySet()
    {
        if ( keySet == null )
        {
            keySet = new ThreadSafeSet( this, objects.keySet() );
        }

        return keySet;
    }

    /**
     * Constructor
     *
     *
     */
    public ThreadSafeMap( int initializeSize )
    {
        super();
        objects = new HashMap( initializeSize );
    }

    /**
     * Return the object which we lock against. Useful
     * if we want to call multiple methods on this
     * object and we want to have exclusive access during
     * that time.
     *
     * <p>NOTE: not yet implementetd!!</p>
     *
     * @return
     */
    public Object getWriteLock()
    {
        return lock;
    }

    /**
     * If a write is currently underway, or is waiting for readers to finish,
     * this method will block until the write has completed.
     *
     */
    private void enteringRead()
    {
        if ( writeLock )
        {
            synchronized ( lock )
            {
                while ( writeLock )
                {
                    try
                    {
                        lock.wait();
                    }
                    catch ( InterruptedException e )
                    {
                    }
                }
            }
        }

        readers++;
    }

    /**
     * Called when a read has finished. If it is the last reader, notify the
     * waiting writer.
     *
     */
    private void exitingRead()
    {
        readers--;

        if ( readers == 0 )
        {
            //notify waiting write lock...
            synchronized ( lock )
            {
                lock.notifyAll();
            }
        }
    }

    public Object[] getValues()
    {
        enteringRead();

        Object[] values = objects.values().toArray();
        exitingRead();

        return values;
    }

    public Object get( Object key )
    {
        if ( key == null )
        {
            return null;
        }

        enteringRead();

        Object obj = objects.get( key );
        exitingRead();

        return obj;
    }

    public Object put( Object key, Object obj )
    {
        if ( key == null )
        {
            return null;
        }

        obtainWriteLock();

        Object prev = objects.put( key, obj );
        releaseWriteLock();

        return prev;
    }

    public Object remove( Object key )
    {
        if ( key == null )
        {
            return null;
        }

        obtainWriteLock();

        Object obj = objects.remove( key );
        releaseWriteLock();

        return obj;
    }

    public void removeAll( Object[] keys )
    {
        obtainWriteLock();

        for ( int i = 0; i < keys.length; i++ )
        {
            objects.remove( keys[i] );
        }

        releaseWriteLock();
    }

    /**
     * @see java.util.Map#containsKey(java.lang.Object)
     */
    public boolean containsKey( Object key )
    {
        enteringRead();

        boolean ret = objects.containsKey( key );
        exitingRead();

        return ret;
    }

    /**
     * @see java.util.Map#containsValue(java.lang.Object)
     */
    public boolean containsValue( Object value )
    {
        enteringRead();

        boolean ret = objects.containsValue( value );
        exitingRead();

        return ret;
    }

    /**
     * @see java.util.Map#isEmpty()
     */
    public boolean isEmpty()
    {
        if ( objects.size() == 0 )
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * @see java.util.Map#putAll(java.util.Map)
     */
    public void putAll( Map map )
    {
        if ( map == null )
        {
            return;
        }

        obtainWriteLock();

        //put this in a try block as the underlying map may
        //throw an exception (for example if the given Map is modified,
        //contains erroneuous values), and we want to ensure the
        //writelock is released
        try
        {
            objects.putAll( map );
        }
        finally
        {
            releaseWriteLock();
        }
    }

    /**
     * @see java.util.Map#size()
     */
    public int size()
    {
        return objects.size();
    }

    /**
     * @todo : return a non modifiable or locking Collection??
     * @see java.util.Map#values()
     */
    public Collection values()
    {
        if ( values == null )
        {
            values = new ThreadSafeCollection( this, objects.values() );
        }

        return values;
    }

    public void clear()
    {
        obtainWriteLock();
        objects.clear();
        releaseWriteLock();
    }

    /**
     * Obtain a writelock. This method will block until all current readers
     * have finished. Any attempted reads once this lock is obtained will
     * also block until this write has completed.
     *
     */
    private void obtainWriteLock()
    {
        synchronized ( lock )
        {
            while ( writeLock )
            {
                //wait till the current writer has finished
                try
                {
                    lock.wait();
                }
                catch ( InterruptedException e )
                {
                }
            }

            //only thread which got this far,
            //notify intention to all to do
            //a write
            writeLock = true;

            //wait till all the readers have finished.
            //Need to do this as some reads may take a long
            //time if the Map is large.
            while ( readers > 0 )
            {
                try
                {
                    lock.wait();
                }
                catch ( InterruptedException e )
                {
                }
            }
        }
    }

    /**
     * Release the write lock and notify and waiting readers to continue.
     *
     */
    private synchronized void releaseWriteLock()
    {
        //only the current thread which holds
        //the lock can do this
        synchronized ( lock )
        {
            writeLock = false;

            //and notify waiting readers and
            //writers.
            lock.notifyAll();
        }
    }

    /**
     * Takes part in the read/write locking of the ThreadedMap
     *
     * <p>Created on 17/06/2003</p>
     *
     * @author <a href="mailto:bert@tuaworks.co.nz">Bert van Brakel</a>
     *
     * @version $Revision$
     */
    class ThreadSafeSet implements Set
    {
        private final Set set;
        private final ThreadSafeMap map;

        ThreadSafeSet( ThreadSafeMap parent, Set set )
        {
            this.set = set;
            this.map = parent;
        }

        /**
         * @param o
         * @return
         */
        public boolean add( Object o )
        {
            map.obtainWriteLock();

            boolean ret = set.add( o );
            map.releaseWriteLock();

            return ret;
        }

        /**
         * @param c
         * @return
         */
        public boolean addAll( Collection c )
        {
            map.obtainWriteLock();

            boolean ret = set.addAll( c );
            map.releaseWriteLock();

            return ret;
        }

        /**
         *
         */
        public void clear()
        {
            map.obtainWriteLock();
            set.clear();
            map.releaseWriteLock();
        }

        /**
         * @param o
         * @return
         */
        public boolean contains( Object o )
        {
            map.enteringRead();

            boolean result = set.contains( o );
            map.exitingRead();

            return result;
        }

        /**
         * @param c
         * @return
         */
        public boolean containsAll( Collection c )
        {
            map.enteringRead();

            boolean result = set.containsAll( c );
            map.exitingRead();

            return result;
        }

        /**
         * @see java.lang.Object#equals(java.lang.Object)
         */
        public boolean equals( Object obj )
        {
            map.enteringRead();

            boolean result = set.equals( obj );
            map.exitingRead();

            return result;
        }

        /**
         * @see java.lang.Object#hashCode()
         */
        public int hashCode()
        {
            map.enteringRead();

            int ret = set.hashCode();
            map.exitingRead();

            return ret;
        }

        /**
         * @return
         */
        public boolean isEmpty()
        {
            return set.isEmpty();
        }

        /**
         * @return
         */
        public Iterator iterator()
        {
            return new ThreadSafeIterator( map, set.iterator() );
        }

        /**
         * @param o
         * @return
         */
        public boolean remove( Object o )
        {
            map.obtainWriteLock();

            boolean ret = set.remove( o );
            map.releaseWriteLock();

            return ret;
        }

        /**
         * @param c
         * @return
         */
        public boolean removeAll( Collection c )
        {
            map.obtainWriteLock();

            boolean ret = set.removeAll( c );
            map.releaseWriteLock();

            return ret;
        }

        /**
         * @param c
         * @return
         */
        public boolean retainAll( Collection c )
        {
            map.obtainWriteLock();

            boolean ret = set.retainAll( c );
            map.releaseWriteLock();

            return ret;
        }

        /**
         * @return
         */
        public int size()
        {
            return set.size();
        }

        /**
         * @return
         */
        public Object[] toArray()
        {
            return set.toArray();
        }

        /**
         * @param a
         * @return
         */
        public Object[] toArray( Object[] a )
        {
            return set.toArray( a );
        }

        /**
         * @see java.lang.Object#toString()
         */
        public String toString()
        {
            return set.toString();
        }
    }

    class ThreadSafeIterator implements Iterator
    {
        private final Iterator iter;
        private final ThreadSafeMap map;

        /**
         * Constructor
         *
         *
         */
        public ThreadSafeIterator( ThreadSafeMap lockMap, Iterator delegate )
        {
            super();
            this.iter = delegate;
            this.map = lockMap;
        }

        /**
         * @see java.lang.Object#equals(java.lang.Object)
         */
        public boolean equals( Object obj )
        {
            return iter.equals( obj );
        }

        /**
         * @see java.lang.Object#hashCode()
         */
        public int hashCode()
        {
            return iter.hashCode();
        }

        /**
         * @return
         */
        public boolean hasNext()
        {
            return iter.hasNext();
        }

        /**
         * @return
         */
        public Object next()
        {
            map.enteringRead();

            Object ret = null;

            try
            {
                //within try incase something gets modified and it
                //throws a wobbly, though shouldn't happen with
                //the locks
                ret = iter.next();
            }
            finally
            {
                map.exitingRead();
            }

            return ret;
        }

        /**
         *
         */
        public void remove()
        {
            try
            {
                map.obtainWriteLock();
                iter.remove();
            }
            finally
            {
                map.releaseWriteLock();
            }
        }

        /**
         * @see java.lang.Object#toString()
         */
        public String toString()
        {
            return iter.toString();
        }
    }

    class ThreadSafeCollection implements Collection
    {
        private final ThreadSafeMap map;
        private Collection col;

        ThreadSafeCollection( ThreadSafeMap map, Collection col )
        {
            this.col = col;
            this.map = map;
        }

        /**
         * @see java.util.Collection#add(java.lang.Object)
         */
        public boolean add( Object o )
        {
            // not supported
            return false;
        }

        /**
         * @see java.util.Collection#addAll(java.util.Collection)
         */
        public boolean addAll( Collection c )
        {
            // not supported
            return false;
        }

        /**
         * @see java.util.Collection#clear()
         */
        public void clear()
        {
            map.clear();
        }

        /**
         * @see java.util.Collection#contains(java.lang.Object)
         */
        public boolean contains( Object o )
        {
            return col.contains( o );
        }

        /**
         * @see java.util.Collection#containsAll(java.util.Collection)
         */
        public boolean containsAll( Collection c )
        {
            map.enteringRead();

            boolean ret = col.containsAll( c );
            map.exitingRead();

            return ret;
        }

        /**
         * @see java.util.Collection#isEmpty()
         */
        public boolean isEmpty()
        {
            if ( map.size() == 0 )
            {
                return true;
            }
            else
            {
                return false;
            }
        }

        /**
         * @see java.util.Collection#iterator()
         */
        public Iterator iterator()
        {
            map.enteringRead();

            Iterator iter = new ThreadSafeIterator( map, col.iterator() );
            map.exitingRead();

            return iter;
        }

        /**
         * @see java.util.Collection#remove(java.lang.Object)
         */
        public boolean remove( Object o )
        {
            map.obtainWriteLock();

            boolean ret = col.remove( o );
            map.releaseWriteLock();

            return ret;
        }

        /**
         * @see java.util.Collection#removeAll(java.util.Collection)
         */
        public boolean removeAll( Collection c )
        {
            map.obtainWriteLock();

            boolean ret = col.removeAll( c );
            map.releaseWriteLock();

            return ret;
        }

        /**
         * @see java.util.Collection#retainAll(java.util.Collection)
         */
        public boolean retainAll( Collection c )
        {
            map.obtainWriteLock();

            boolean ret = col.retainAll( c );
            map.releaseWriteLock();

            return ret;
        }

        /**
         * @see java.util.Collection#size()
         */
        public int size()
        {
            return map.size();
        }

        /**
         * @see java.util.Collection#toArray()
         */
        public Object[] toArray()
        {
            map.enteringRead();

            Object[] ret = col.toArray();
            map.exitingRead();

            return ret;
        }

        /**
         * @see java.util.Collection#toArray(java.lang.Object[])
         */
        public Object[] toArray( Object[] a )
        {
            map.enteringRead();

            Object[] ret = col.toArray( a );
            map.exitingRead();

            return ret;
        }
    }
}
