package de.steinpfeffer.nubilo.users;

import static de.steinpfeffer.utilities.validation.Validator.argumentNotEmpty;
import static de.steinpfeffer.utilities.validation.Validator.argumentNotNull;

import javax.annotation.concurrent.Immutable;

import de.steinpfeffer.utilities.hashcode.BaseHashCodeBuilder;
import de.steinpfeffer.utilities.hashcode.HashCodeBuilder;
import de.steinpfefffer.utilities.string.DefaultToStringBuilder;

/**
 * Default implementation of {@link User}.
 * 
 * @author Juergen Fickel
 * @since 1.0.0
 */
@Immutable
public final class DefaultUser implements User {

    private final String name;
    private final String displayName;
    private final Group group;
    private final Password password;

    private DefaultUser(final String theName,
            final String theDisplayName,
            final Group theGroup,
            final Password thePassword) {
        assertThatArgumentIsNotNull(theName, "name");
        argumentNotEmpty(theName, "The name must not be empty!");
        assertThatArgumentIsNotNull(theGroup, "group");
        assertThatArgumentIsNotNull(thePassword, "password");
        name = theName;
        if (isGivenDisplayNameSuitable(theDisplayName)) {
            displayName = theDisplayName;
        } else {
            displayName = name;
        }
        group = theGroup;
        password = thePassword;
    }

    private static void assertThatArgumentIsNotNull(final Object argument, final String argumentName) {
        final String msgTemplate = "The %s must not be null!";
        argumentNotNull(argument, msgTemplate, argumentName);
    }

    private static boolean isGivenDisplayNameSuitable(final String givenDisplayName) {
        return null != givenDisplayName && !givenDisplayName.isEmpty();
    }

    /**
     * Delivers an instance of {@link DefaultUser} based on the
     * provided arguments. The display name of the returned group is
     * equal to the provided name.
     * 
     * @param name
     *            the technical name of the user, must neither be
     *            {@code null} nor empty!
     * @param group
     *            the {@link Group} of the user, must not be
     *            {@code null}.
     * @param password
     *            the {@link Password} of the user, must not be
     *            {@code null}.
     * @return a {@link DefaultUser} object.
     */
    public static DefaultUser getInstance(final String name, final Group group, final Password password) {
        return new DefaultUser(name, name, group, password);
    }

    /**
     * Delivers an instance of {@link DefaultUser} based on the
     * provided arguments.
     * 
     * @param name
     *            the technical name of the user, must neither be
     *            {@code null} nor empty!
     * @param displayName
     *            the name which is displayed on UIs. May be
     *            {@code null} or empty!; in both cases it will be
     *            replaced by {@code name}.
     * @param group
     *            the {@link Group} of the user, must not be
     *            {@code null}.
     * @param password
     *            the {@link Password} of the user, must not be
     *            {@code null}.
     * @return a {@link DefaultUser} object.
     */
    public static DefaultUser getInstance(final String name,
            final String displayName,
            final Group group,
            final Password password) {
        return new DefaultUser(name, displayName, group, password);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public Group getGroup() {
        return group;
    }

    @Override
    public Password getPassword() {
        return password;
    }

    @Override
    public int hashCode() {
        final HashCodeBuilder builder = BaseHashCodeBuilder.newInstance();
        builder.hash(name).hash(displayName).hash(group).hash(password);
        return builder.getHashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof DefaultUser)) {
            return false;
        }
        DefaultUser other = (DefaultUser) obj;
        if (!name.equals(other.name)) {
            return false;
        }
        if (!displayName.equals(other.displayName)) {
            return false;
        }
        if (!group.equals(other.group)) {
            return false;
        }
        if (!password.equals(other.password)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        final DefaultToStringBuilder builder = DefaultToStringBuilder.newInstance(getClass());
        builder.add("name", name).add("displayName", displayName).add("group", group).add("password", "*****");
        return builder.toString();
    }

}
