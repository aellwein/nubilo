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
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.concurrent.ThreadSafe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.steinpfeffer.nubilo.users.beans.GroupBean;

/**
 * TODO Complete Javadoc comment
 * 
 * @author Juergen Fickel
 * @since 1.0.0
 */
@ThreadSafe
final class JsonGroupManagementServiceOld implements GroupManagementService {

    /* Write groups cache to file if this amount of changes occurred. */
    private static final int DEFAULT_DIRTY_LIMIT = 10;

    private final int dirtyLimit;
    private final FileAccessor<GroupBean> fileAccessor;
    private final EntityConverter entityConverter;
    private final Logger logger;
    private final EntityCache<Group> groupsCache;
    private final AtomicInteger dirtyCounter;

    public JsonGroupManagementServiceOld(final JsonFileAccessor<GroupBean> fileAccessor,
            final EntityCache<Group> groupsCache,
            final EntityConverter entityConverter) {
        this(DEFAULT_DIRTY_LIMIT, fileAccessor, groupsCache, entityConverter);
    }

    public JsonGroupManagementServiceOld(final int theDirtyLimit,
            final JsonFileAccessor<GroupBean> theFileAccessor,
            final EntityCache<Group> theGroupsCache,
            final EntityConverter theEntityConverter) {
        validateState(1 <= theDirtyLimit);
        argumentNotNull(theFileAccessor);
        argumentNotNull(theGroupsCache);
        argumentNotNull(theEntityConverter);
        dirtyLimit = theDirtyLimit;
        fileAccessor = theFileAccessor;
        entityConverter = theEntityConverter;
        logger = LoggerFactory.getLogger(getClass());
        groupsCache = theGroupsCache;
        dirtyCounter = new AtomicInteger();
        initialiseGroupsCache();
    }

    private void initialiseGroupsCache() {
        for (final GroupBean groupBean : tryToReadGroupsDataFile()) {
            final Group group = entityConverter.convertToGroup(groupBean);
            groupsCache.add(group);
        }
    }

    private GroupBean[] tryToReadGroupsDataFile() {
        try {
            return readGroupsFromFile();
        } catch (final IOException e) {
            final String msgTemplate = "Unable to read JSON file with groups data: %s";
            throw new UserManagementException(format(msgTemplate, fileAccessor.getDataSourceIdentification()), e);
        }
    }

    private GroupBean[] readGroupsFromFile() throws IOException {
        final GroupBean[] result = fileAccessor.readBeansFromFile(GroupBean[].class);
        logger.debug("Read {} group bean(s).", result.length);
        return result;
    }

    @Override
    public List<Group> getAllGroups() {
        return new ArrayList<>(groupsCache.getAll());
    }

    @Override
    public Group getGroup(final String name) {
        return groupsCache.get(name);
    }

    @Override
    public void manageGroup(final Group group) throws UserManagementException {
        groupsCache.add(group);
        dirtyCounter.incrementAndGet();
        writeGroupsCacheToFileIfDirtyEnough();
    }

    private void writeGroupsCacheToFileIfDirtyEnough() {
        if (isDirtyEnough()) {
            tryToWriteGroupsCacheToFile();
            dirtyCounter.set(0);
        }
    }

    private boolean isDirtyEnough() {
        return dirtyLimit <= dirtyCounter.intValue();
    }

    private void tryToWriteGroupsCacheToFile() {
        try {
            writeGroupsCacheToFile();
        } catch (final IOException e) {
            final String msgTemplate = "Unable to write groups data to JSON file: %s";
            throw new UserManagementException(format(msgTemplate, fileAccessor.getDataSourceIdentification()), e);
        }
    }

    private void writeGroupsCacheToFile() throws IOException {
        final List<GroupBean> groupBeans = entityConverter.convertToGroupBeans(groupsCache.getAll());
        fileAccessor.writeBeansToFile(groupBeans);
        logger.debug("Wrote {} group bean(s) to {}.", groupBeans.size(), fileAccessor.getDataSourceIdentification());
    }

    @Override
    public void updateGroup(final Group group) throws UserManagementException {
        if (null == group) {
            return;
        }
        groupsCache.add(group);
        dirtyCounter.incrementAndGet();
        writeGroupsCacheToFileIfDirtyEnough();
    }

    @Override
    public void deleteGroup(final Group group) throws UserManagementException {
        groupsCache.remove(group);
        dirtyCounter.incrementAndGet();
        writeGroupsCacheToFileIfDirtyEnough();
    }

    public void writeToFileIfDirty() {
        if (0 < dirtyCounter.intValue()) {
            tryToWriteGroupsCacheToFile();
        }
    }

}
