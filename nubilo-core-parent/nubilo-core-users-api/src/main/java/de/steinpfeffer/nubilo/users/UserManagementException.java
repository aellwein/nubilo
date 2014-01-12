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
