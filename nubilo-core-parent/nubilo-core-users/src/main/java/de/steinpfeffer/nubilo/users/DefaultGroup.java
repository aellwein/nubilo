package de.steinpfeffer.nubilo.users;

import static de.steinpfeffer.utilities.validation.Validator.argumentNotEmpty;
import static de.steinpfeffer.utilities.validation.Validator.argumentNotNull;
import static de.steinpfeffer.utilities.validation.Validator.isNotNull;
import static de.steinpfeffer.utilities.validation.Validator.isNull;

import javax.annotation.concurrent.Immutable;

import de.steinpfeffer.utilities.hashcode.BaseHashCodeBuilder;
import de.steinpfeffer.utilities.hashcode.HashCodeBuilder;
import de.steinpfeffer.utilities.string.ToStringBuilder;
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

    private static boolean isGivenDisplayNameSuitable(final String providedDisplayName) {
        return null != providedDisplayName && !providedDisplayName.isEmpty();
    }

    /**
     * Delivers an instance of {@link DefaultGroup} based on the
     * provided name. The display name of the returned group is equal
     * to the provided name.
     * 
     * @param name
     *            the technical name of the group, must neither be
     *            {@code null} nor empty!
     * @return a {@link DefaultGroup} object.
     */
    public static DefaultGroup getInstance(final String name) {
        return getInstance(name, name);
    }

    /**
     * Delivers an instance of {@link DefaultGroup} based on the
     * provided parameters.
     * 
     * @param name
     *            the technical name of the group, must neither be
     *            {@code null} nor empty!
     * @param displayName
     *            the name which is displayed on UIs. May be
     *            {@code null} or empty!; in both cases it will be
     *            replaced by {@code name}.
     * @return a {@link DefaultGroup} object.
     */
    public static DefaultGroup getInstance(final String name, final String displayName) {
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
    public int hashCode() {
        final HashCodeBuilder builder = BaseHashCodeBuilder.newInstance();
        builder.hash(name).hash(displayName);
        return builder.getHashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (isNull(obj)) {
            return false;
        }
        if (!(obj instanceof DefaultGroup)) {
            return false;
        }
        final DefaultGroup other = (DefaultGroup) obj;
        if (!name.equals(other.name)) {
            return false;
        }
        if (isNull(displayName)) {
            if (isNotNull(other.displayName)) {
                return false;
            }
        } else if (!displayName.equals(other.displayName)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        final ToStringBuilder builder = DefaultToStringBuilder.newInstance(getClass());
        builder.add("name", name).add("displayName", displayName);
        return builder.toString();
    }

}
