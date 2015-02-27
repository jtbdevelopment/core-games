package com.jtbdevelopment.games.datagrid

import groovy.transform.CompileStatic
import org.springframework.cache.Cache
import org.springframework.cache.support.SimpleValueWrapper

/**
 * Date: 2/26/15
 * Time: 6:42 PM
 *
 * Wraps another cache to handle puts or gets that are on lists/sets/arrays
 * The list/set/array is converted into individual elements
 *
 * For gets, if any item is null, the entire list is null
 *
 * Does not promise atomicity across a list for put/get
 *
 */
@CompileStatic
class ListHandlingCache implements Cache {
    private final Cache wrapped

    public ListHandlingCache(final Cache wrapped) {
        this.wrapped = wrapped
    }

    @Override
    String getName() {
        return wrapped.name
    }

    @Override
    Object getNativeCache() {
        return wrapped.nativeCache
    }

    private List<Object> getKeys(final Iterable keys, final Class classOptional = null) {
        List<Object> result =
                classOptional ?
                        keys.collect {
                            wrapped.get(it, classOptional)
                        } :
                        keys.collect {
                            wrapped.get(it)?.get()
                        }
        if (result.contains(null)) {
            return null
        }
        return result
    }

    @Override
    Cache.ValueWrapper get(final Object key) {
        if (key instanceof Iterable) {
            List<Object> result = getKeys(key)
            return result ? new SimpleValueWrapper(result) : null
        }
        if (key instanceof Object[]) {
            List<Object> result = getKeys(Arrays.asList((Object[]) key))
            return result ? new SimpleValueWrapper(result.toArray()) : null
        }
        return wrapped.get(key)
    }

    @Override
    def <T> T get(final Object key, final Class<T> type) {
        if (key instanceof Iterable) {
            List<Object> result = getKeys(key, type)
            return (T) (result ? result : null)
        }
        if (key instanceof Object[]) {
            List<Object> result = getKeys(Arrays.asList((Object[]) key), type)
            return (T) (result ? result.toArray() : null)
        }
        return wrapped.get(key, type)
    }

    @Override
    void put(final Object key, final Object value) {
        if (key instanceof Collection && value instanceof Collection) {
            if (key.size() != value.size()) {
                throw new IllegalArgumentException('Key size of ' + key.size() + ' != to value size ' + value.size())
            }
            putElements(key, value)
        } else if (key instanceof Collection && !(value instanceof Collection)) {
            throw new IllegalArgumentException('keys are collection but not values')
        } else if (key instanceof Object[] && value instanceof Object[]) {
            List keys = Arrays.asList((Object[]) key)
            List values = Arrays.asList((Object[]) value)
            if (keys.size() != values.size()) {
                throw new IllegalArgumentException('Key size of ' + keys.size() + ' != to value size ' + values.size())
            }
            putElements(keys, values)
        } else if (key instanceof Object[] && !(value instanceof Object[])) {
            throw new IllegalArgumentException('keys are array but not values')
        } else {
            wrapped.put(key, value)
        }
    }


    @Override
    Cache.ValueWrapper putIfAbsent(final Object key, final Object value) {
        if (key instanceof Collection && value instanceof Collection) {
            if (key.size() != value.size()) {
                throw new IllegalArgumentException('Key size of ' + key.size() + ' != to value size ' + value.size())
            }
            return putElementsIfAbsent(key, value)
        } else if (key instanceof Collection && !(value instanceof Collection)) {
            throw new IllegalArgumentException('keys are collection but not values')
        } else if (key instanceof Object[] && value instanceof Object[]) {
            List keys = Arrays.asList((Object[]) key)
            List values = Arrays.asList((Object[]) value)
            if (keys.size() != values.size()) {
                throw new IllegalArgumentException('Key size of ' + keys.size() + ' != to value size ' + values.size())
            }
            return putElementsIfAbsent(keys, values)
        } else if (key instanceof Object[] && !(value instanceof Object[])) {
            throw new IllegalArgumentException('keys are array but not values')
        } else {
            return wrapped.putIfAbsent(key, value)
        }
    }

    @Override
    void evict(final Object key) {
        if (key instanceof Iterable) {
            key.each {
                wrapped.evict(it)
            }
        } else if (key instanceof Object[]) {
            key.each {
                wrapped.evict(it)
            }
        } else {
            wrapped.evict(key)
        }
    }

    @Override
    void clear() {
        wrapped.clear()
    }

    private void putElements(final Collection key, final Collection value) {
        Iterator keyIterator = key.iterator()
        Iterator valueIterator = value.iterator()
        while (keyIterator.hasNext()) {
            wrapped.put(keyIterator.next(), valueIterator.next())
        }
    }

    private Cache.ValueWrapper putElementsIfAbsent(final Collection key, final Collection value) {
        Iterator keyIterator = key.iterator()
        Iterator valueIterator = value.iterator()
        List<Object> result = new LinkedList<>()
        while (keyIterator.hasNext()) {
            result.add(wrapped.putIfAbsent(keyIterator.next(), valueIterator.next())?.get())
        }
        return new SimpleValueWrapper(result)
    }
}
