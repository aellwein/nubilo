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

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import javax.annotation.concurrent.ThreadSafe;

import de.steinpfeffer.nubilo.users.beans.GroupBean;

/**
 * TODO Complete Javadoc comment
 * 
 * @author Juergen Fickel
 * @since 1.0.0
 */
@ThreadSafe
final class JsonGroupManagementService extends AbstractJsonEntityManagementService<GroupBean, Group> {

    public JsonGroupManagementService(final FileAccessor<GroupBean> fileAccessor,
            final EntityCache<Group> groupsCache,
            final EntityConverter entityConverter) {
        super(fileAccessor, groupsCache, entityConverter);
    }

    public JsonGroupManagementService(final int dirtyLimit,
            final FileAccessor<GroupBean> fileAccessor,
            final EntityCache<Group> groupsCache,
            final EntityConverter entityConverter) {
        super(dirtyLimit, fileAccessor, groupsCache, entityConverter);
    }

    @Override
    protected GroupBean[] readEntityBeansFromFile(final FileAccessor<GroupBean> fileAccessor) throws IOException {
        return fileAccessor.readBeansFromFile(GroupBean[].class);
    }

    @Override
    protected EntityCache<Group> initialiseCache(final GroupBean[] groupBeans,
            final EntityConverter entityConverter,
            final EntityCache<Group> groupsCache) {
        for (final GroupBean groupBean : groupBeans) {
            final Group group = entityConverter.convertToGroup(groupBean);
            groupsCache.add(group);
        }
        return groupsCache;
    }

    @Override
    protected List<GroupBean> convertEntitiesToBeans(final EntityConverter entityConverter,
            final Collection<Group> allGroups) {
        return entityConverter.convertToGroupBeans(allGroups);
    }

}
