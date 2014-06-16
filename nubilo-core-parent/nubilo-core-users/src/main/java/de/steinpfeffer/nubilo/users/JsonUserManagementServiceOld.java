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
import static de.steinpfeffer.utilities.validation.Validator.validateState;
import static java.lang.String.format;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.concurrent.ThreadSafe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
final class JsonUserManagementServiceOld implements UserManagementService {

    /* Write users cache to file if this amount of changes occurred. */
    private static final int DEFAULT_DIRTY_LIMIT = 10;

    private final int dirtyLimit;
    private final FileAccessor<UserBean> fileAccessor;
    private final GroupManagementService groupManagementService;
    private final EntityConverter entityConverter;
    private final Logger logger;
    private final EntityCache<User> usersCache;
    private final AtomicInteger dirtyCounter;

    public JsonUserManagementServiceOld(final JsonFileAccessor<UserBean> fileAccessor,
            final EntityCache<User> usersCache,
            final GroupManagementService groupManagementService,
            final EntityConverter entityConverter) {
        this(DEFAULT_DIRTY_LIMIT, fileAccessor, usersCache, groupManagementService, entityConverter);
    }

    /**
     * Constructs a new {@link JsonUserManagementServiceOld} object.
     * 
     * @throws IOException
     */
    public JsonUserManagementServiceOld(final int theDirtyLimit,
            final JsonFileAccessor<UserBean> theFileAccessor,
            final EntityCache<User> theUsersCache,
            final GroupManagementService theGroupManagementService,
            final EntityConverter theEntityConverter) {
        validateState(1 <= theDirtyLimit);
        argumentNotNull(theFileAccessor);
        argumentNotNull(theEntityConverter);
        argumentNotNull(theGroupManagementService);
        argumentNotNull(theEntityConverter);
        dirtyLimit = theDirtyLimit;
        fileAccessor = theFileAccessor;
        groupManagementService = theGroupManagementService;
        entityConverter = theEntityConverter;
        logger = LoggerFactory.getLogger(getClass());
        usersCache = theUsersCache;
        dirtyCounter = new AtomicInteger();
        initialiseUsersCache();
    }

    private void initialiseUsersCache() {
        final Map<String, Group> allGroups = getAllGroupsAsMap();
        for (final UserBean userBean : tryToReadUsersDataFile()) {
            final User user = entityConverter.convertToUser(userBean, allGroups);
            usersCache.add(user);
        }
    }

    private Map<String, Group> getAllGroupsAsMap() {
        final List<Group> allGroups = groupManagementService.getAllGroups();
        final Map<String, Group> result = new HashMap<>(allGroups.size());
        for (final Group group : allGroups) {
            result.put(group.getName(), group);
        }
        return result;
    }

    private UserBean[] tryToReadUsersDataFile() {
        try {
            return readUsersFromFile();
        } catch (final IOException e) {
            final String msgTemplate = "Unable to read JSON file with users data: %s";
            throw new UserManagementException(format(msgTemplate, fileAccessor.getDataSourceIdentification()), e);
        }
    }

    private UserBean[] readUsersFromFile() throws IOException {
        final UserBean[] result = fileAccessor.readBeansFromFile(UserBean[].class);
        logger.debug("Read {} user bean(s).", result.length);
        return result;
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<User>(usersCache.getAll());
    }

    @Override
    public User getUser(final String name) {
        return usersCache.get(name);
    }

    @Override
    public void manageUser(final User user) throws UserManagementException {
        usersCache.add(user);
        dirtyCounter.incrementAndGet();
        writeUsersCacheToFileIfDirtyEnough();
    }

    private void writeUsersCacheToFileIfDirtyEnough() {
        if (isDirtyEnough()) {
            tryToWriteUsersCacheToFile();
            dirtyCounter.set(0);
        }
    }

    private boolean isDirtyEnough() {
        return dirtyLimit <= dirtyCounter.intValue();
    }

    private void tryToWriteUsersCacheToFile() {
        try {
            writeUsersCacheToFile();
        } catch (final IOException e) {
            final String msgTemplate = "Unable to write users data to JSON file: %s";
            throw new UserManagementException(format(msgTemplate, fileAccessor.getDataSourceIdentification()), e);
        }
    }

    private void writeUsersCacheToFile() throws IOException {
        final List<UserBean> userBeans = entityConverter.convertToUserBeans(usersCache.getAll());
        fileAccessor.writeBeansToFile(userBeans);
        logger.debug("Wrote {} user bean(s) to {}.", userBeans.size(), fileAccessor.getDataSourceIdentification());
    }

    @Override
    public void updateUser(final User user) {
        if (null == user) {
            return;
        }
        usersCache.add(user);
        dirtyCounter.incrementAndGet();
        writeUsersCacheToFileIfDirtyEnough();
    }

    @Override
    public void deleteUser(final User user) throws UserManagementException {
        usersCache.remove(user);
        dirtyCounter.incrementAndGet();
        writeUsersCacheToFileIfDirtyEnough();
    }

    public void writeToFileIfDirty() {
        if (0 < dirtyCounter.intValue()) {
            tryToWriteUsersCacheToFile();
        }
    }

}
