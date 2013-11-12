package de.steinpfeffer.nubilo.core.persistence;

import static org.fest.assertions.api.Assertions.assertThat;
import nl.renarj.jasdb.LocalDBSession;
import nl.renarj.jasdb.api.DBSession;
import nl.renarj.jasdb.core.exceptions.JasDBStorageException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.steinpfeffer.utilities.testing.Order;
import de.steinpfeffer.utilities.testing.junit.JUnitOrderedRunner;

/**
 * API learning test for the <a
 * href="http://www.oberasoftware.com/jasdb-2/">JasDB database</a>.
 * 
 * @author Juergen Fickel
 * @since 1.0.0
 */
@RunWith(JUnitOrderedRunner.class)
public final class JasDbApiLearningTest {

    private static DBSession session;
    private final String bagName;

    public JasDbApiLearningTest() {
        bagName = "MySpecialBag";
    }

    @BeforeClass
    public static void setUp() throws JasDBStorageException {
        session = new LocalDBSession();
    }

    @AfterClass
    public static void tearDown() throws JasDBStorageException {
        session.closeSession();
    }

    @Order(1)
    @Test
    public void createSessionAndGetBag() throws JasDBStorageException {
        session.createOrGetBag(bagName);
        assertThat(session.getBag(bagName)).isNotNull();
    }

    @Order(2)
    @Test
    public void deleteBag() throws JasDBStorageException {
        session.removeBag(bagName);
    }

    @Order(3)
    @Test
    public void getNonExistentBag() throws JasDBStorageException {
        assertThat(session.getBag(bagName)).isNull();
    }

    // TODO Add more tests.

}
