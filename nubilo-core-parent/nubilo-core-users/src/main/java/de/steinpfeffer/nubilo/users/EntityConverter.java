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

import static java.lang.String.format;

import java.util.*;

import javax.annotation.concurrent.Immutable;

import de.steinpfeffer.nubilo.users.beans.GroupBean;
import de.steinpfeffer.nubilo.users.beans.PasswordBean;
import de.steinpfeffer.nubilo.users.beans.UserBean;

/**
 * Factory for creating {@link User} instances .
 * 
 * @author Juergen Fickel
 * @since 1.0.0
 */
@Immutable
final class EntityConverter {

    public User convertToUser(final UserBean userBean, final Map<String, Group> allGroups) {
        final String name = userBean.getName();
        final String displayName = userBean.getDisplayName();
        final Set<Group> groups = new HashSet<>();
        for (final String groupName : userBean.getGroups()) {
            final Group group = allGroups.get(groupName);
            if (null == group) {
                throw new UserManagementException(format("The group '%s' does not exist!", groupName));
            } else {
                groups.add(group);
            }
        }
        final Password password = convertToUserPassword(userBean.getPassword());
        return DefaultUser.getInstance(name, displayName, groups, password);
    }

    private Password convertToUserPassword(final PasswordBean passwordBean) {
        final String hashedPassword = passwordBean.getHashedPassword();
        final String digestString = passwordBean.getDigest();
        for (final Digest digest : Digests.values()) {
            if (digest.getDigestName().equals(digestString)) {
                return DefaultPassword.getInstance(digest, hashedPassword);
            }
        }
        throw new UserManagementException("Unable to create a password from the given password bean.");
    }

    public Group convertToGroup(final GroupBean groupBean) {
        final String name = groupBean.getName();
        final String displayName = groupBean.getDisplayName();
        return DefaultGroup.getInstance(name, displayName);
    }

    public List<UserBean> convertToUserBeans(final Collection<User> users) {
        final List<UserBean> result = new ArrayList<>(users.size());
        for (final User user : users) {
            result.add(convertToUserBean(user));
        }
        return result;
    }

    private UserBean convertToUserBean(final User user) {
        final UserBean result = new UserBean();
        result.setName(user.getName());
        result.setDisplayName(user.getDisplayName());
        result.setGroups(getGroupNames(user.getGroups()));
        result.setPassword(convertToPasswordBean(user.getPassword()));
        return result;
    }

    private String[] getGroupNames(final Set<Group> groups) {
        final String[] result = new String[groups.size()];
        int i = 0;
        for (final Group group : groups) {
            result[i] = group.getName();
            i++;
        }
        return result;
    }

    private PasswordBean convertToPasswordBean(final Password password) {
        final PasswordBean result = new PasswordBean();
        result.setDigest(password.getDigest().getDigestName());
        result.setHashedPassword(password.getHashedPassword());
        return result;
    }

    public List<GroupBean> convertToGroupBeans(final Collection<Group> groups) {
        final List<GroupBean> result = new ArrayList<>(groups.size());
        for (final Group group : groups) {
            result.add(convertToGroupBean(group));
        }
        return result;
    }

    private GroupBean convertToGroupBean(final Group group) {
        final GroupBean result = new GroupBean();
        result.setName(group.getName());
        result.setDisplayName(group.getDisplayName());
        return result;
    }

}
