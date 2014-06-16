/**
 * Copyright 2013-2014 Juergen Fickel <steinpfeffer@gmx.de>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.steinpfeffer.nubilo.users;

import java.util.List;

/**
 * Service for entity management like creating, retrieving or deleting
 * entities.
 * 
 * @param <E>
 *            the type of the managed {@link Entity}.
 * 
 * @author Juergen Fickel
 * @since 1.0.0
 */
public interface EntityManagementService<E extends Entity> {

    /**
     * Gets a {@link List} of all {@link Entity}s which are managed by
     * Nubilo.
     * 
     * @return all Nubilo entities or an empty {@link List} but never
     *         {@code null}.
     */
    List<E> getAllEntities();

    /**
     * Gets the {@link Entity} with the given {@code name}.
     * 
     * @param name
     *            the <em>unique</em> name of the entity to get.
     * @return the {@link Entity} with the given {@code name}. This
     *         result is never {@code null}.
     */
    E getEntity(String name);

    /**
     * Makes Nubilo aware of the given {@link Entity}.
     * 
     * @param entity
     *            the {@link Entity} to manage by Nubilo.
     * @throws UserManagementException
     *             if the Nubilo core system fails to manage the given
     *             entity. One reason is that a entity with the same
     *             name already is managed by Nubilo.
     */
    void manageEntity(E entity) throws UserManagementException;

    /**
     * Updates the given {@link Entity}. If the name of the entity
     * should be changed this is the wrong method. Instead the old
     * entity has to be deleted and a new one inserted.
     * 
     * @param entity
     *            the entity to update.
     * @throws UserManagementException
     *             if {@code Entity} is not yet managed by Nubilo.
     * @see #deleteEntity(E)
     * @see #manageEntity(E)
     */
    void updateEntity(E entity) throws UserManagementException;

    /**
     * Deletes the given {@link Entity} from Nubilo.
     * 
     * @param entity
     *            the {@link Entity} to delete from Nubilo.
     * @throws UserManagementException
     *             if the Nubilo core system fails to delete the given
     *             entity.
     */
    void deleteEntity(E entity) throws UserManagementException;

}
