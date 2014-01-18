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

import java.util.HashSet;
import java.util.Set;

import de.steinpfeffer.nubilo.users.beans.GroupBean;
import de.steinpfeffer.nubilo.users.beans.PasswordBean;
import de.steinpfeffer.nubilo.users.beans.UserBean;

/**
 * Factory for creating {@link User} instances .
 * 
 * @author Juergen Fickel
 * @since 1.0.0
 */
final class UserEntityConverter {

    public User convertToUser(final UserBean userBean) {
        final String name = userBean.getName();
        final String displayName = userBean.getDisplayName();
        final Set<Group> groups = convertToUserGroups(userBean.getGroups());
        final Password password = convertToUserPassword(userBean.getPassword());
        return DefaultUser.getInstance(name, displayName, groups, password);
    }

    private Set<Group> convertToUserGroups(final GroupBean[] groupBeans) {
        final Set<Group> result = new HashSet<>(groupBeans.length);
        for (final GroupBean groupBean : groupBeans) {
            final String name = groupBean.getName();
            final String displayName = groupBean.getDisplayName();
            result.add(DefaultGroup.getInstance(name, displayName));
        }
        return result;
    }

    private Password convertToUserPassword(final PasswordBean passwordBean) {
        final String digestString = passwordBean.getDigest();
        final Digest digest = Digests.valueOf(digestString);
        final String hashedPassword = passwordBean.getHashedPassword();
        return DefaultPassword.getInstance(digest, hashedPassword);
    }

    public UserBean convertToUserBean(final User user) {
        final UserBean result = new UserBean();
        result.setName(user.getName());
        result.setDisplayName(user.getDisplayName());
        result.setGroups(convertToUserGroupBeans(user.getGroups()));
        result.setPassword(convertToPasswordBean(user.getPassword()));
        return result;
    }

    private GroupBean[] convertToUserGroupBeans(final Set<Group> groups) {
        return groups.toArray(new GroupBean[groups.size()]);
    }

    private PasswordBean convertToPasswordBean(final Password password) {
        final PasswordBean result = new PasswordBean();
        result.setDigest(password.getDigest().getDigestName());
        result.setHashedPassword(password.getHashedPassword());
        return result;
    }

}
