package de.steinpfeffer.nubilo.users;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mutabilitydetector.unittesting.MutabilityAssert.assertImmutable;

import org.junit.Ignore;
import org.junit.Test;

/**
 * Component test for {@link DefaultGroup}.
 * 
 * @author Juergen Fickel
 * @since 1.0.0
 */
// CHECKSTYLE:OFF
public final class DefaultGroupTest {

    @Ignore("Test is irrelevant for functionality.")
    @Test
    public void testToString() {
        final Group group = DefaultGroup.getInstance("foobar", "Foo Bar");
        System.out.println(group);
    }

    @Test(expected = IllegalArgumentException.class)
    public void tryToCreateInstanceWithNullName() {
        DefaultGroup.getInstance(null);
    }

    @Test
    public void displayNameIsNameIfNotProvided() {
        final String name = "Foo";
        final DefaultGroup group = DefaultGroup.getInstance(name);
        assertThat(group.getDisplayName()).isEqualTo(name);
    }

    @Test
    public void displayNameIsNameIfNullProvided() {
        final String name = "Foo";
        final DefaultGroup group = DefaultGroup.getInstance(name, null);
        assertThat(group.getDisplayName()).isEqualTo(name);
    }

    @Test
    public void displayNameIsNameIfEmptyStrngProvided() {
        final String name = "Foo";
        final DefaultGroup group = DefaultGroup.getInstance(name, "");
        assertThat(group.getDisplayName()).isEqualTo(name);
    }

    @Test
    public void groupIsImmutable() {
        assertImmutable(DefaultGroup.class);
    }

}
// CHECKSTYLE:ON
