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

import javax.annotation.concurrent.Immutable;

import de.steinpfeffer.utilities.hashcode.BaseHashCodeBuilder;
import de.steinpfefffer.utilities.string.DefaultToStringBuilder;

/**
 * Default implementation of {@link Group}.
 * 
 * @author Juergen Fickel
 * @since 1.0.0
 */
@Immutable
public final class DefaultGroup implements Group {

    private final String name;
    private final String displayName;

    private DefaultGroup(final String theName, final String theDisplayName) {
        validateName(theName);
        name = theName;
        if (isGivenDisplayNameSuitable(theDisplayName)) {
            displayName = theDisplayName;
        } else {
            displayName = name;
        }
    }

    private static void validateName(final String name) {
        final String msgTemplate = "The name must not be %s!";
        argumentNotNull(name, msgTemplate, "null");
        argumentNotEmpty(name, msgTemplate, "empty");
    }

    private static boolean isGivenDisplayNameSuitable(final String givenDisplayName) {
        return null != givenDisplayName && !givenDisplayName.isEmpty();
    }

    /**
     * Delivers an instance of {@link Group} based on the provided
     * name. The display name of the returned group is equal to the
     * provided name.
     * 
     * @param name
     *            the technical name of the group, must neither be
     *            {@code null} nor empty!
     * @return a {@link Group} object.
     */
    public static Group getInstance(final String name) {
        return getInstance(name, name);
    }

    /**
     * Delivers an instance of {@link Group} based on the provided
     * arguments.
     * 
     * @param name
     *            the technical name of the group, must neither be
     *            {@code null} nor empty!
     * @param displayName
     *            the name which is displayed on UIs. May be
     *            {@code null} or empty!; in both cases it will be
     *            replaced by {@code name}.
     * @return a {@link Group} object.
     */
    public static Group getInstance(final String name, final String displayName) {
        return new DefaultGroup(name, displayName);
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
    public int compareTo(final Group o) {
        return displayName.compareTo(o.getDisplayName());
    }

    @Override
    public int hashCode() {
        return BaseHashCodeBuilder.newInstance().hash(name).hash(displayName).getHashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (null == obj) {
            return false;
        }
        if (!(obj instanceof DefaultGroup)) {
            return false;
        }
        final DefaultGroup other = (DefaultGroup) obj;
        if (!name.equals(other.name)) {
            return false;
        }
        if (!displayName.equals(other.displayName)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return DefaultToStringBuilder.newInstance(getClass()).add("name", name).add("displayName", displayName)
                .toString();
    }

}
