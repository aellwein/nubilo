package de.steinpfeffer.nubilo.users;

import static java.lang.String.format;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

/**
 * TODO Complete Javadoc comment
 * 
 * @param <T>
 * 
 * @author Juergen Fickel
 * @since 1.0.0
 */
@ThreadSafe
final class DefaultEntityCache<T extends Entity> implements EntityCache<T> {

    @GuardedBy("this")
    private final ConcurrentMap<String, T> cache;

    private DefaultEntityCache() {
        cache = new ConcurrentHashMap<>();
    }

    public static <T extends Entity> EntityCache<T> getInstance() {
        return new DefaultEntityCache<>();
    }

    @Override
    public void add(final T entity) {
        if (null == entity) {
            return;
        }
        synchronized (this) {
            cache.put(entity.getName(), entity);
        }
    }

    @Override
    public T get(final String name) {
        return cache.get(name);
    }

    @Override
    public Collection<T> getAll() {
        return cache.values();
    }

    @Override
    public void remove(final T entity) {
        if (null == entity) {
            return;
        }
        final String entityName = entity.getName();
        synchronized (this) {
            if (!cache.containsKey(entityName)) {
                final String msgTemplate = "An entity with the name '%s' is unknown!";
                throw new UserManagementException(format(msgTemplate, entityName));
            }
            cache.remove(entityName);
        }
    }

}
