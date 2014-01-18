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
 * Java Bean for a password.
 * 
 * @author Juergen Fickel
 * @since 1.0.0
 */
@NotThreadSafe
public final class PasswordBean {

    private String digest;
    private String hashedPassword;

    /**
     * Sets the digest which was used to hash a user's password.
     * 
     * @param digest
     *            the digest which was used to hash a user's password.
     * 
     * @see PasswordBean#getHashedPassword()
     */
    public void setDigest(final String digest) {
        this.digest = digest;
    }

    /**
     * Returns the digest which was used to hash a user's password.
     * 
     * @return the digest which was used to hash a user's password.
     * 
     * @see {@link #getHashedPassword()}
     */
    public String getDigest() {
        return digest;
    }

    /**
     * Sets the hashed password of a user.
     * 
     * @param hashedPassword
     *            the hashed password of a user.
     */
    public void setHashedPassword(final String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }

    /**
     * Returns the hashed password of a user.
     * 
     * @return the hashed password of a user.
     */
    public String getHashedPassword() {
        return hashedPassword;
    }

}
