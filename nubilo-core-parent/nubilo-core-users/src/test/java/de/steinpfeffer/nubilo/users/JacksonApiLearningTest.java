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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;

import de.steinpfeffer.nubilo.users.beans.GroupBean;
import de.steinpfeffer.nubilo.users.beans.PasswordBean;
import de.steinpfeffer.nubilo.users.beans.UserBean;

/**
 * API learning test for <a
 * href="https://github.com/FasterXML/jackson-databind/">Jackson
 * databind API</a>.
 * 
 * @author Juergen Fickel
 * @since 1.0.0
 */
// CHECKSTYLE:OFF
public final class JacksonApiLearningTest {

    private final ObjectMapper mapper;

    public JacksonApiLearningTest() {
        mapper = new ObjectMapper();
    }

    @Test
    public void writeUserBeansToFile() throws JsonGenerationException, JsonMappingException, IOException {
        final UserBean[] users = new UserBean[2];
        users[0] = createDummyUser(createDummyPassword(), createDummyGroupBean());
        final File usersFile = new File(System.getProperty("java.io.tmpdir") + "/user_beans.json");
        final UserBean user = createDummyUser(createDummyPassword(), createDummyGroupBean());
        user.setName("bar");
        user.setDisplayName("Bar");
        users[1] = user;
        mapper.writeValue(usersFile, users);
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
        result.setGroups(groups);
        result.setPassword(password);
        return result;
    }

    @Test
    public void readUserBeansFromFile() throws JsonParseException, JsonMappingException, IOException {
        final InputStream resourceStream = getClass().getResourceAsStream("user_beans.json");
        final UserBean[] userBeans = mapper.readValue(resourceStream, UserBean[].class);
        assertThat(userBeans).hasSize(2);
    }

    @Test
    public void readUserBeansFromFileUsingObjectReader() throws JsonProcessingException, IOException {
        final InputStream resourceStream = getClass().getResourceAsStream("user_beans.json");
        final ObjectReader reader = mapper.reader(UserBean.class);
        final MappingIterator<Object> values = reader.readValues(resourceStream);
        int numberOfUsers = 0;
        while (values.hasNext()) {
            final Object user = values.next();
            if (null != user) {
                numberOfUsers++;
            }
        }
        assertThat(numberOfUsers).isEqualTo(2);
    }

    /**
     * If the same value is written twice to a file only one will
     * remain.
     */
    @Test
    public void writeSameUserTwice() throws JsonGenerationException, JsonMappingException, IOException {
        final File outputFile = new File(System.getProperty("java.io.tmpdir") + "/same_user_twice.json");
        final UserBean dummyUser = createDummyUser(createDummyPassword(), createDummyGroupBean());
        mapper.writeValue(outputFile, dummyUser);
        mapper.writeValue(outputFile, dummyUser);
        final UserBean userBean = mapper.readValue(outputFile, UserBean.class);
        assertThat(userBean.getDisplayName()).isEqualTo(dummyUser.getDisplayName());
    }

}
// CHECKSTYLE:OFF
