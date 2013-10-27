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

/**
 * TODO Complete Javadoc comment
 * 
 * @author Juergen Fickel
 * @since 1.0.0
 */
public enum Digest {

    SHA1("SHA-1"),

    SHA256("SHA-256"),

    SHA384("SHA-384"),

    SHA512("SHA-512");

    private final String algorithmName;

    private Digest(final String theAlgorithmName) {
        algorithmName = theAlgorithmName;
    }

    public String getAlgorithmName() {
        return algorithmName;
    }

}
