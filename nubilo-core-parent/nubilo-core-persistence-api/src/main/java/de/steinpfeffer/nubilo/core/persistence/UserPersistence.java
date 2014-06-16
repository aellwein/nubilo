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
package de.steinpfeffer.nubilo.core.persistence;

import de.steinpfeffer.nubilo.users.Group;
import de.steinpfeffer.nubilo.users.Password;
import de.steinpfeffer.nubilo.users.User;

/**
 * TODO Complete Javadoc comment
 * 
 * @author Juergen Fickel
 * @since 1.0.0
 */
public interface UserPersistence {

    User createUser(String username, String displayName, Group group, Password password);

    User getUserByName(String username);

}
