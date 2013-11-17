/**
 * Copyright 2013 Juergen Fickel <steinpfeffer@gmx.de>.
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

import javax.annotation.concurrent.Immutable;

/**
 * An enumeration of {@link Digest}s.
 * 
 * @author Juergen Fickel
 * @since 1.0.0
 */
@Immutable
public enum Digests implements Digest {

    /** The SHA-1 digest. */
    SHA1("SHA-1"),

    /** The SHA-256 digest. */
    SHA256("SHA-256"),

    /** The SHA-384 digest. */
    SHA384("SHA-384"),

    /** The SHA-512 digest. */
    SHA512("SHA-512");

    private final String digestName;

    private Digests(final String theAlgorithmName) {
        digestName = theAlgorithmName;
    }

    @Override
    public String getDigestName() {
        return digestName;
    }

}
