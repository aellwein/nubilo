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

import javax.annotation.concurrent.NotThreadSafe;

/**
 * Java Bean for user a user group.
 * 
 * @author Juergen Fickel
 * @since 1.0.0
 */
@NotThreadSafe
public final class GroupBean {

    private String name;
    private String displayName;

    /**
     * Sets the (technical) name of the group.
     * 
     * @param name
     *            the name of the group to set.
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Returns the (technical) name of the group.
     * 
     * @return the name of the group.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the group which should be used for displaying.
     * 
     * @param displayName
     *            the name of the group for displaying to set.
     */
    public void setDisplayName(final String displayName) {
        this.displayName = displayName;
    }

    /**
     * Returns the name of the group which should be used for
     * displaying.
     * 
     * @return the name of the group for displaying.
     */
    public String getDisplayName() {
        return displayName;
    }

}
