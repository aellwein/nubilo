package de.steinpfeffer.nubilo.users;

import java.util.List;

/**
 * Service for user management like creating, retrieving or deleting
 * user entities. User management implicitly includes group
 * management; so if all users of a group are deleted the group will
 * be gone, too.
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
     * Gets a {@link List} of all {@link Group}s which are managed by
     * Nubilo.
     * 
     * @return all Nubilo groups or an empty {@link List} but never
     *         {@code null}.
     */
    List<Group> getAllGroups();

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
     *             user.
     */
    void manageUser(User user) throws UserManagementException;

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
