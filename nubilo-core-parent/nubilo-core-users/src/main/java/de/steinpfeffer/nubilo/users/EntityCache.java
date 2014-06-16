package de.steinpfeffer.nubilo.users;

import java.util.Collection;

/**
 * TODO Complete Javadoc comment
 * 
 * @param <T>
 * 
 * @author Juergen Fickel
 * @since 1.0.0
 */
interface EntityCache<T extends Entity> {

    public void add(T entity);

    public T get(String name);

    public Collection<T> getAll();

    public void remove(T entity);

}
