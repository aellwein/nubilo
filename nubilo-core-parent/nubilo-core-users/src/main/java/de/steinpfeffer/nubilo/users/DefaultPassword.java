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

import static de.steinpfeffer.utilities.validation.Validator.argumentNotEmpty;
import static de.steinpfeffer.utilities.validation.Validator.argumentNotNull;

import javax.annotation.concurrent.Immutable;

import de.steinpfeffer.utilities.hashcode.BaseHashCodeBuilder;
import de.steinpfefffer.utilities.string.DefaultToStringBuilder;

/**
 * Default implementation of {@link Password}.
 * 
 * @author Juergen Fickel
 * @since 1.0.0
 */
@Immutable
public final class DefaultPassword implements Password {

    private final Digest digest;
    private final String passwordHash;

    private DefaultPassword(final Digest theDigest, final String thePasswordHash) {
        argumentNotNull(theDigest, "theDigest");
        argumentNotNull(thePasswordHash, "thePasswordHash");
        argumentNotEmpty(thePasswordHash, "thePasswordHash");
        digest = theDigest;
        passwordHash = thePasswordHash;
    }

    /**
     * Delivers an instance of {@link Password} based on the provided
     * arguments.
     * 
     * @param digest
     *            the {@link Digest} which was used to create the
     *            {@code passwordHash}; must not be {@code null}!
     * @param passwordHash
     *            the hashed password; must neither be {@code null}
     *            nor empty!
     * @return a {@link Password} instance.
     */
    public static Password getInstance(final Digest digest, final String passwordHash) {
        return new DefaultPassword(digest, passwordHash);
    }

    @Override
    public Digest getDigest() {
        return digest;
    }

    @Override
    public String getHashedPassword() {
        return passwordHash;
    }

    @Override
    public int hashCode() {
        return BaseHashCodeBuilder.newInstance().hash(digest).hash(passwordHash).getHashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof DefaultPassword)) {
            return false;
        }
        DefaultPassword other = (DefaultPassword) obj;
        if (!digest.equals(other.digest)) {
            return false;
        }
        if (!passwordHash.equals(other.passwordHash)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return DefaultToStringBuilder.newInstance(getClass()).add("digest", digest).add("passwordHash", "*****")
                .toString();
    }

}
