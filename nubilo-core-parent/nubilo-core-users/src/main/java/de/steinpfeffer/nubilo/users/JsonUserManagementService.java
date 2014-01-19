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

import static de.steinpfeffer.utilities.validation.Validator.argumentNotNull;
import static java.lang.String.format;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.steinpfeffer.nubilo.users.beans.UserBean;

/**
 * An implementation of the {@link UserManagementService} which uses
 * JSON files for data management. Because of the fact that this
 * implementation is backed by a JSON file it offers the additional
 * method {@link #tryToWwriteUsersCacheToFile()} to trigger
 * persisting. Also the users data is written to the file when a
 * certain amount of changes occurred.
 * 
 * @author Juergen Fickel
 * @since 1.0.0
 */
@ThreadSafe
final class JsonUserManagementService implements UserManagementService {

    /* Write users cache to file if this amount of changes occurred. */
    private static final int DIRTY_LIMIT = 10;

    private final Logger logger;
    private final File usersDataFile;
    @GuardedBy("this")
    private final ConcurrentMap<String, User> usersCache;
    private final ObjectMapper objectMapper;
    private final AtomicInteger dirtyCounter;

    /**
     * Constructs a new {@link JsonUserManagementService} object.
     */
    public JsonUserManagementService(final File theUsersDataFile) {
        argumentNotNull(theUsersDataFile, "The users data file must not be null!");
        usersDataFile = theUsersDataFile;
        logger = LoggerFactory.getLogger(getClass());
        usersCache = new ConcurrentHashMap<>();
        objectMapper = new ObjectMapper();
        dirtyCounter = new AtomicInteger();
        initialiseUsersCache();
    }

    private void initialiseUsersCache() {
        final UserBean[] userBeans = tryToReadUsersDataFile();
        final UserEntityConverter userEntityConverter = new UserEntityConverter();
        for (final UserBean userBean : userBeans) {
            final User user = userEntityConverter.convertToUser(userBean);
            usersCache.put(user.getName(), user);
        }
    }

    private UserBean[] tryToReadUsersDataFile() {
        try {
            return readUsersDataFile();
        } catch (final IOException e) {
            final String msgTemplate = "Unable to read JSON file with users data: %s";
            throw new UserManagementException(format(msgTemplate, usersDataFile.getAbsolutePath()), e);
        }
    }

    private UserBean[] readUsersDataFile() throws IOException {
        final UserBean[] result;
        if (usersDataFile.exists()) {
            result = objectMapper.readValue(usersDataFile, UserBean[].class);
        } else {
            result = new UserBean[0];
        }
        logger.debug("Read {} user bean(s).", result.length);
        return result;
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<User>(usersCache.values());
    }

    @Override
    public List<Group> getAllGroups() {
        final Set<Group> groups = new HashSet<>();
        for (final User user : usersCache.values()) {
            groups.addAll(user.getGroups());
        }
        return new ArrayList<Group>(groups);
    }

    @Override
    public User getUser(final String name) {
        return usersCache.get(name);
    }

    @Override
    public void manageUser(final User user) throws UserManagementException {
        if (null == user) {
            return;
        }
        synchronized (this) {
            final String username = user.getName();
            if (usersCache.containsKey(username)) {
                final String msgTemplate = "A user with the name '%s' is already managed by Nubilo.";
                throw new UserManagementException(format(msgTemplate, username));
            }
            usersCache.put(username, user);
            dirtyCounter.incrementAndGet();
        }
        writeUsersCacheToFileIfDirtyEnough();
    }

    private void writeUsersCacheToFileIfDirtyEnough() {
        if (isDirtyEnough()) {
            tryToWriteUsersCacheToFile();
            dirtyCounter.set(0);
        }
    }

    private boolean isDirtyEnough() {
        return DIRTY_LIMIT <= dirtyCounter.intValue();
    }

    private void tryToWriteUsersCacheToFile() {
        try {
            writeUsersCacheToFile();
        } catch (final IOException e) {
            final String msgTemplate = "Unable to write users data to JSON file: %s";
            throw new UserManagementException(format(msgTemplate, usersDataFile.getAbsolutePath()), e);
        }
    }

    private void writeUsersCacheToFile() throws IOException {
        createUsersDataFileIfNecessary();
        final UserBean[] userBeans = createUserBeans();
        objectMapper.writeValue(usersDataFile, userBeans);
        logger.debug("Wrote {} user bean(s) to {}.", userBeans.length, usersDataFile.getAbsolutePath());
    }

    private void createUsersDataFileIfNecessary() throws IOException {
        if (!usersDataFile.exists()) {
            usersDataFile.createNewFile();
        }
    }

    private UserBean[] createUserBeans() {
        final Collection<User> users = usersCache.values();
        final UserBean[] userBeans = new UserBean[users.size()];
        final UserEntityConverter userEntityConverter = new UserEntityConverter();
        int i = 0;
        for (final User user : users) {
            userBeans[i] = userEntityConverter.convertToUserBean(user);
            i++;
        }
        return userBeans;
    }

    @Override
    public void updateUser(final User user) {
        if (null == user) {
            return;
        }
        final String username = user.getName();
        synchronized (this) {
            if (!usersCache.containsKey(username)) {
                final String msgTemplate = "A user with the name '%s' is unknown!";
                throw new UserManagementException(format(msgTemplate, username));
            }
            usersCache.put(username, user);
            dirtyCounter.incrementAndGet();
        }
        writeUsersCacheToFileIfDirtyEnough();
    }

    @Override
    public void deleteUser(final User user) throws UserManagementException {
        if (null == user) {
            return;
        }
        final String username = user.getName();
        synchronized (this) {
            if (!usersCache.containsKey(username)) {
                final String msgTemplate = "A user with the name '%s' is unknown!";
                throw new UserManagementException(format(msgTemplate, username));
            }
            usersCache.remove(username);
            dirtyCounter.incrementAndGet();
        }
        writeUsersCacheToFileIfDirtyEnough();
    }

    public void writeToFileIfDirty() {
        if (0 < dirtyCounter.intValue()) {
            tryToWriteUsersCacheToFile();
        }
    }

}
