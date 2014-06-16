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

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mutabilitydetector.unittesting.AllowedReason.provided;
import static org.mutabilitydetector.unittesting.MutabilityAssert.assertInstancesOf;
import static org.mutabilitydetector.unittesting.MutabilityMatchers.areImmutable;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.steinpfeffer.nubilo.users.beans.GroupBean;
import de.steinpfeffer.nubilo.users.beans.PasswordBean;
import de.steinpfeffer.nubilo.users.beans.UserBean;
import de.steinpfeffer.utilities.testing.Order;
import de.steinpfeffer.utilities.testing.junit.JUnitOrderedRunner;

/**
 * Unit test for {@link JsonFileAccessor}.
 * 
 * @author Juergen Fickel
 * @since 1.0.0
 */
// CHECKSTYLE:OFF
@RunWith(JUnitOrderedRunner.class)
public final class JsonFileAccessorTest {

    @Test
    public void jsonFileAccessorIsImmutable() {
        assertInstancesOf(JsonFileAccessor.class, areImmutable(), provided(File.class, ObjectMapper.class)
                .areAlsoImmutable());
    }

    @Order(1)
    @Test
    public void readUserBeansFromFile() throws URISyntaxException, IOException {
        final URL resource = getClass().getResource("user_beans.json");
        final File jsonUsersFile = new File(resource.toURI());
        final FileAccessor<UserBean> jsonFileAccessor = new JsonFileAccessor<>(jsonUsersFile);
        final UserBean[] userBeans = jsonFileAccessor.readBeansFromFile(UserBean[].class);
        assertThat(userBeans).hasSize(2);
        final String[] groupNames = userBeans[0].getGroups();
        assertThat(groupNames).hasSize(2);
    }

    @Order(2)
    @Test
    public void writeUserBeansToFile() throws IOException {
        final List<UserBean> users = new ArrayList<>(2);
        users.add(createDummyUser(createDummyPassword(), createDummyGroupBean()));
        final File jsonUsersFile = new File(System.getProperty("java.io.tmpdir") + "/user_beans.json");
        final UserBean user = createDummyUser(createDummyPassword(), createDummyGroupBean());
        user.setName("bar");
        user.setDisplayName("Bar");
        users.add(user);

        final FileAccessor<UserBean> jsonFileAccessor = new JsonFileAccessor<>(jsonUsersFile);
        jsonFileAccessor.writeBeansToFile(users);

        final UserBean[] userBeans = jsonFileAccessor.readBeansFromFile(UserBean[].class);
        assertThat(userBeans.length).isEqualTo(users.size());
        assertThat(users).containsExactly(userBeans);
    }

    private static GroupBean createDummyGroupBean() {
        final GroupBean result = new GroupBean();
        result.setName("users");
        result.setDisplayName("Benutzer");
        return result;
    }

    private static PasswordBean createDummyPassword() {
        final PasswordBean result = new PasswordBean();
        result.setDigest("SHA256");
        result.setHashedPassword("asdfjklö");
        return result;
    }

    private static UserBean createDummyUser(final PasswordBean password,
            final GroupBean group,
            final GroupBean... additionalGroups) {
        final UserBean result = new UserBean();
        result.setName("foo");
        result.setDisplayName("Foo");

        final GroupBean[] groups = Arrays.copyOf(additionalGroups, additionalGroups.length + 1);
        groups[groups.length - 1] = group;
        final String[] groupNames = new String[groups.length];
        for (int i = 0; i < groups.length; i++) {
            groupNames[i] = groups[i].getName();
        }
        result.setGroups(groupNames);
        result.setPassword(password);
        return result;
    }

    // TODO Complement test cases.

}
//CHECKSTYLE:ON
