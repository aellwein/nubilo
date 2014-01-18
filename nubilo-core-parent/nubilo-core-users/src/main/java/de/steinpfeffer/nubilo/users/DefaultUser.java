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

import static de.steinpfeffer.utilities.validation.Validator.argumentNotEmpty;
import static de.steinpfeffer.utilities.validation.Validator.argumentNotNull;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.concurrent.Immutable;

import de.steinpfeffer.utilities.hashcode.BaseHashCodeBuilder;
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
    private final Set<Group> groups;
    private final Password password;

    private DefaultUser(final String theName,
            final String theDisplayName,
            final Set<Group> theGroups,
            final Password thePassword) {
        assertThatArgumentIsNotNull(theName, "name");
        argumentNotEmpty(theName, "The name must not be empty!");
        assertThatArgumentIsNotNull(theGroups, "groups");
        argumentNotEmpty(theGroups, "The groups must not be empty!");
        assertThatArgumentIsNotNull(thePassword, "password");
        name = theName;
        if (isGivenDisplayNameSuitable(theDisplayName)) {
            displayName = theDisplayName;
        } else {
            displayName = name;
        }
        groups = new HashSet<>(theGroups);
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
     * Delivers an instance of {@link User} based on the provided
     * arguments. The display name of the returned group is equal to
     * the provided name.
     * 
     * @param name
     *            the technical name of the user, must neither be
     *            {@code null} nor empty!
     * @param groups
     *            a {@link Set} of {@link Group}s of the user, must
     *            neither be {@code null} nor empty.
     * @param password
     *            the {@link Password} of the user, must not be
     *            {@code null}.
     * @return a {@link User} instance.
     */
    public static User getInstance(final String name, final Set<Group> groups, final Password password) {
        return new DefaultUser(name, name, groups, password);
    }

    /**
     * Delivers an instance of {@link User} based on the provided
     * arguments.
     * 
     * @param name
     *            the technical name of the user, must neither be
     *            {@code null} nor empty!
     * @param displayName
     *            the name which is displayed on UIs. May be
     *            {@code null} or empty!; in both cases it will be
     *            replaced by {@code name}.
     * @param groups
     *            a {@link Set} of {@link Group}s of the user, must
     *            neither be {@code null} nor empty.
     * @param password
     *            the {@link Password} of the user, must not be
     *            {@code null}.
     * @return a {@link User} instance.
     */
    public static User getInstance(final String name,
            final String displayName,
            final Set<Group> groups,
            final Password password) {
        return new DefaultUser(name, displayName, groups, password);
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
    public Set<Group> getGroups() {
        return new HashSet<>(groups);
    }

    @Override
    public Password getPassword() {
        return password;
    }

    @Override
    public int compareTo(final User o) {
        return displayName.compareTo(o.getDisplayName());
    }

    @Override
    public int hashCode() {
        return BaseHashCodeBuilder.newInstance().hash(name).hash(displayName).hash(groups).hash(password).getHashCode();
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
        if (!groups.equals(other.groups)) {
            return false;
        }
        if (!password.equals(other.password)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return DefaultToStringBuilder.newInstance(getClass()).add("name", name).add("displayName", displayName)
                .add("groups", groups).add("password", "*****").toString();
    }

}
