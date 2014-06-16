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
 * TODO Complete Javadoc comment
 * 
 * @author Juergen Fickel
 * @since 1.0.0
 */
public interface GroupManagementService {

    /**
     * Gets a {@link List} of all {@link Group}s which are managed by
     * Nubilo.
     * 
     * @return all Nubilo groups or an empty {@link List} but never
     *         {@code null}.
     */
    List<Group> getAllGroups();

    /**
     * Gets the {@link Group} with the given {@code name}.
     * 
     * @param name
     *            the <em>unique</em> name of the group to get.
     * @return the {@link Group} with the given {@code name}. This
     *         result is never {@code null}.
     */
    Group getGroup(String name);

    /**
     * Makes Nubilo aware of the given {@link Group}.
     * 
     * @param group
     *            the {@link Group} to manage by Nubilo.
     * @throws UserManagementException
     *             if the Nubilo core system fails to manage the given
     *             user. One reason is that a group with the same name
     *             already is managed by Nubilo.
     */
    void manageGroup(Group group) throws UserManagementException;

    /**
     * Updates the given {@link Group}. If the name of the group
     * should be changed this is the wrong method. Instead the old
     * group has to be deleted and a new one inserted.
     * 
     * @param group
     *            the group to update.
     * @throws UserManagementException
     *             if {@code Group} is not yet managed by Nubilo.
     * @see #deleteUser(Group)
     * @see #manageUser(Group)
     */
    void updateGroup(Group group) throws UserManagementException;

    /**
     * Deletes the given {@link Group} from Nubilo.
     * 
     * @param group
     *            the {@link Group} to delete from Nubilo.
     * @throws UserManagementException
     *             if the Nubilo core system fails to delete the given
     *             group.
     */
    void deleteGroup(Group group) throws UserManagementException;

}
