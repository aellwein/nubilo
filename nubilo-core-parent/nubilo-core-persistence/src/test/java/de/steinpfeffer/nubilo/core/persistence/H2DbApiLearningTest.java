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

import static org.fest.assertions.api.Assertions.assertThat;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.steinpfeffer.utilities.testing.Order;
import de.steinpfeffer.utilities.testing.junit.JUnitOrderedRunner;

/**
 * Some tests to learn the JDBC API in conjunction with the H2
 * database.
 * 
 * @author Juergen Fickel
 * @since 1.0.0
 */
@Ignore
@RunWith(JUnitOrderedRunner.class)
public final class H2DbApiLearningTest {

    private static Connection connection;

    @BeforeClass
    public static void establishConnection() throws ClassNotFoundException, SQLException {
        Class.forName("org.h2.Driver");
        connection = DriverManager.getConnection("jdbc:h2:mem:", "sa", "");
    }

    @AfterClass
    public static void closeConnection() throws SQLException {
        connection.close();
    }

    @Order(0)
    @Test
    public void connectionIsValid() throws ClassNotFoundException, SQLException {
        assertThat(connection.isValid(0)).isTrue();
    }

    @Order(10)
    @Test
    public void createUsersTable() throws SQLException {
        final int numberOfTablesBefore = getNumberOfTables();
        final String tableName = "USERS";
        createTable(tableName);
        assertThat(getNumberOfTables()).isEqualTo(numberOfTablesBefore + 1);
    }

    private void createTable(final String tableName) throws SQLException {
        final StringBuilder createSqlString = new StringBuilder();
        createSqlString.append("CREATE TABLE ").append(tableName).append(" (");
        createSqlString.append(tableName).append("_ID VARCHAR(64) PRIMARY KEY").append(")");
        final Statement createStatement = connection.createStatement();
        createStatement.executeUpdate(createSqlString.toString());
        createStatement.close();
    }

    private int getNumberOfTables() throws SQLException {
        final ResultSet resultSet = showTables();
        int result = 0;
        while (resultSet.next()) {
            result++;
        }
        resultSet.close();
        return result;
    }

    private ResultSet showTables() throws SQLException {
        final Statement showStatement = connection.createStatement();
        showStatement.execute("SHOW TABLES");
        return showStatement.getResultSet();
    }

    @Order(20)
    @Test
    public void createGroupsTable() throws SQLException {
        final int numberOfTablesBefore = getNumberOfTables();
        final String tableName = "GROUPS";
        createTable(tableName);
        assertThat(getNumberOfTables()).isEqualTo(numberOfTablesBefore + 1);
    }

    @Order(30)
    @Test
    public void dropAllTables() throws SQLException {
        final int expectedNumberOfTablesBefore = 2;
        assertThat(getNumberOfTables()).isEqualTo(expectedNumberOfTablesBefore);
        final String dropTablesSqlString = createDropTableStatement("USERS", "GROUPS");
        final Statement dropStatement = connection.createStatement();
        dropStatement.executeUpdate(dropTablesSqlString);
        dropStatement.close();
        assertThat(getNumberOfTables()).isEqualTo(0);
    }

    private static String createDropTableStatement(final String firstTableName, final String... furtherTableNames) {
        final StringBuilder result = new StringBuilder();
        result.append("DROP TABLE ").append(firstTableName);
        final String separator = ", ";
        for (final String furtherTableName : furtherTableNames) {
            result.append(separator).append(furtherTableName);
        }
        return result.toString();
    }

}
