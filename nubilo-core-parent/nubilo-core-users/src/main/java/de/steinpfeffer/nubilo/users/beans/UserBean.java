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
package de.steinpfeffer.nubilo.users.beans;

import java.util.Arrays;

import javax.annotation.concurrent.NotThreadSafe;

import de.steinpfeffer.utilities.api.hashcode.HashCodeBuilder;
import de.steinpfeffer.utilities.hashcode.BaseHashCodeBuilder;

/**
 * Java Bean for a user.
 * 
 * @author Juergen Fickel
 * @since 1.0.0
 */
@NotThreadSafe
public final class UserBean {

    private String name;
    private String displayName;
    private String[] groups;
    private PasswordBean password;

    /**
     * Sets the (technical) name of the user.
     * 
     * @param name
     *            the name of the user.
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Returns the (technical) name of the user.
     * 
     * @return the name of the user.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the user which should be used for displaying.
     * 
     * @param displayName
     *            the name of the user for displaying to set.
     */
    public void setDisplayName(final String displayName) {
        this.displayName = displayName;
    }

    /**
     * Returns the name of the user which should be used for
     * displaying.
     * 
     * @return the name of the user for displaying.
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Sets an array of the user's groups.
     * 
     * @param groups
     *            the names of the groups to which the user belongs.
     */
    public void setGroups(final String[] groups) {
        this.groups = groups;
    }

    /**
     * Returns an array of the user's groups.
     * 
     * @return the groups to which this user belongs.
     */
    public String[] getGroups() {
        return groups;
    }

    /**
     * Sets the password of the user.
     * 
     * @param password
     *            the password of the user to set.
     */
    public void setPassword(final PasswordBean password) {
        this.password = password;
    }

    /**
     * Gets the password of the user.
     * 
     * @return the password of the user.
     */
    public PasswordBean getPassword() {
        return password;
    }

    @Override
    public int hashCode() {
        final HashCodeBuilder builder = BaseHashCodeBuilder.newInstance();
        builder.hash(name).hash(displayName).hash(groups).hash(password);
        return builder.getHashCode();
    }

    // CHECKSTYLE:OFF
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof UserBean)) {
            return false;
        }
        final UserBean other = (UserBean) obj;
        if (displayName == null) {
            if (other.displayName != null) {
                return false;
            }
        } else if (!displayName.equals(other.displayName)) {
            return false;
        }
        if (!Arrays.equals(groups, other.groups)) {
            return false;
        }
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        if (password == null) {
            if (other.password != null) {
                return false;
            }
        } else if (!password.equals(other.password)) {
            return false;
        }
        return true;
    }
    // CHECKSTYLE:OFF

}
