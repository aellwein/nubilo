package de.steinpfeffer.nubilo.users;

import java.io.IOException;
import java.util.List;

/**
 * TODO Complete Javadoc comment
 * 
 * @param <T>
 * 
 * @author Juergen Fickel
 * @since 1.0.0
 */
interface FileAccessor<T> {

    T[] readBeansFromFile(Class<T[]> beanType) throws IOException;

    void writeBeansToFile(List<T> beans) throws IOException;

    String getDataSourceIdentification();

}
