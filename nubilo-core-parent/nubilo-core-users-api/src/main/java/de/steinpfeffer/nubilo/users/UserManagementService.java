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
 * Service for user management like creating, retrieving or deleting
 * user entities.
 * 
 * @author Juergen Fickel
 * @since 1.0.0
 */
public interface UserManagementService {

    /**
     * Gets a {@link List} of all {@link User}s which are managed by
     * Nubilo.
     * 
     * @return all Nubilo users or an empty {@link List} but never
     *         {@code null}.
     */
    List<User> getAllUsers();

    /**
     * Gets the {@link User} with the given {@code name}.
     * 
     * @param name
     *            the <em>unique</em> name of the user to get.
     * @return the {@link User} with the given {@code name}. This
     *         result is never {@code null}.
     */
    User getUser(String name);

    /**
     * Makes Nubilo aware of the given {@link User}.
     * 
     * @param user
     *            the {@link User} to manage by Nubilo.
     * @throws UserManagementException
     *             if the Nubilo core system fails to manage the given
     *             user. One reason is that a user with the same name
     *             already is managed by Nubilo.
     */
    void manageUser(User user) throws UserManagementException;

    /**
     * Updates the given {@link User}. If the name of the user should
     * be changed this is the wrong method. Instead the old user has
     * to be deleted and a new one inserted.
     * 
     * @param user
     *            the user to update.
     * @throws UserManagementException
     *             if {@code User} is not yet managed by Nubilo.
     * @see #deleteUser(User)
     * @see #manageUser(User)
     */
    void updateUser(User user) throws UserManagementException;

    /**
     * Deletes the given {@link User} from Nubilo.
     * 
     * @param user
     *            the {@link User} to delete from Nubilo.
     * @throws UserManagementException
     *             if the Nubilo core system fails to delete the given
     *             user.
     */
    void deleteUser(User user) throws UserManagementException;

}
