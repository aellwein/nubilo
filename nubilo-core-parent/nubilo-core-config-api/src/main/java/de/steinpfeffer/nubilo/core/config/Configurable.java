package de.steinpfeffer.nubilo.core.config;

import java.util.Map;

/**
 * Implementors are supposed to register themselves on the Service
 * Registry for receiving configuration properties of the central
 * configuration facility (Whiteboard pattern).
 * 
 * @author Juergen Fickel
 * @since 1.0.0
 */
public interface Configurable {

    /**
     * Gets called when the central configuration properties have
     * changed. The implementor is responsible for filtering out the
     * properties it is interested in.
     * 
     * @param configurationProperties
     *            the configuration properties which are provided by
     *            the central configuration facility.
     */
    void configure(Map<String, Object> configurationProperties);

}
