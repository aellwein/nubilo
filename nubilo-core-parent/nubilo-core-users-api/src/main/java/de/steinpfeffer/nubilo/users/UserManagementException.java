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

/**
 * This {@link RuntimeException} is thrown to indicate unexpected
 * behaviour in user management.
 * 
 * @author Juergen Fickel
 * @since 1.0.0
 */
public final class UserManagementException extends RuntimeException {

    private static final long serialVersionUID = 7969576234377316664L;

    /**
     * Constructs a new {@link UserManagementException} object.
     */
    public UserManagementException() {
        super();
    }

    /**
     * Constructs a new {@link UserManagementException} object.
     * 
     * @param message
     *            the details message. The detail message is saved for
     *            later retrieval by the {@link #getMessage()} method.
     */
    public UserManagementException(final String message) {
        super(message);
    }

    /**
     * Constructs a new {@link UserManagementException} object.
     * 
     * @param cause
     *            the cause (which is saved for later retrieval by the
     *            {@link #getCause()} method). (A {@code null} value
     *            is permitted, and indicates that the cause is
     *            nonexistent or unknown.)
     */
    public UserManagementException(final Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new {@link UserManagementException} object.
     * 
     * @param message
     *            the details message. The detail message is saved for
     *            later retrieval by the {@link #getMessage()} method.
     * @param cause
     *            the cause (which is saved for later retrieval by the
     *            {@link #getCause()} method). (A {@code null} value
     *            is permitted, and indicates that the cause is
     *            nonexistent or unknown.)
     */
    public UserManagementException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
