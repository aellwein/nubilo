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

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.concurrent.ThreadSafe;

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
final class JsonUserManagementService extends AbstractJsonEntityManagementService<UserBean, User> {

    private final EntityManagementService<Group> groupManagementService;

    public JsonUserManagementService(final JsonFileAccessor<UserBean> fileAccessor,
            final EntityCache<User> usersCache,
            final EntityManagementService<Group> theGroupManagementService,
            final EntityConverter entityConverter) {
        super(fileAccessor, usersCache, entityConverter);
        argumentNotNull(theGroupManagementService);
        groupManagementService = theGroupManagementService;
    }

    public JsonUserManagementService(final int dirtyLimit,
            final JsonFileAccessor<UserBean> fileAccessor,
            final EntityCache<User> usersCache,
            final EntityManagementService<Group> theGroupManagementService,
            final EntityConverter entityConverter) {
        super(dirtyLimit, fileAccessor, usersCache, entityConverter);
        argumentNotNull(theGroupManagementService);
        groupManagementService = theGroupManagementService;
    }

    @Override
    protected UserBean[] readEntityBeansFromFile(final FileAccessor<UserBean> fileAccessor) throws IOException {
        return fileAccessor.readBeansFromFile(UserBean[].class);
    }

    @Override
    protected EntityCache<User> initialiseCache(final UserBean[] userBeans,
            final EntityConverter entityConverter,
            final EntityCache<User> usersCache) {
        final Map<String, Group> allGroups = getAllGroupsAsMap();
        for (final UserBean userBean : userBeans) {
            final User user = entityConverter.convertToUser(userBean, allGroups);
            usersCache.add(user);
        }
        return usersCache;
    }

    private Map<String, Group> getAllGroupsAsMap() {
        final List<Group> allGroups = groupManagementService.getAllEntities();
        final Map<String, Group> result = new HashMap<>(allGroups.size());
        for (final Group group : allGroups) {
            result.put(group.getName(), group);
        }
        return result;
    }

    @Override
    protected List<UserBean> convertEntitiesToBeans(final EntityConverter entityConverter,
            final Collection<User> allUsers) {
        return entityConverter.convertToUserBeans(allUsers);
    }

}
