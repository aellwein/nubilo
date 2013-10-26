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
package de.steinpfeffer.nubilo.core.persistence;

import static org.fest.assertions.api.Assertions.assertThat;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Some tests to learn the JDBC API in conjunction with the H2
 * database.
 * 
 * @author Juergen Fickel
 * @since 1.0.0
 */
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

    @Test
    public void connectionIsValid() throws ClassNotFoundException, SQLException {
        assertThat(connection.isValid(0)).isTrue();
    }

    @Test
    public void createEmployeesTable() throws SQLException {
        final String tableName = "EMPLOYEES";
        createTable(tableName);
        assertThat(getNameOfFirstTable()).isEqualTo(tableName);
    }

    private void createTable(final String tableName) throws SQLException {
        final StringBuilder createSqlString = new StringBuilder();
        createSqlString.append("CREATE TABLE ").append(tableName).append(" (");
        createSqlString.append(tableName).append("_ID INTEGER").append(")");
        final Statement createStatement = connection.createStatement();
        createStatement.executeUpdate(createSqlString.toString());
        createStatement.close();
    }

    private String getNameOfFirstTable() throws SQLException {
        final Statement showStatement = connection.createStatement();
        showStatement.execute("SHOW TABLES");
        final ResultSet resultSet = showStatement.getResultSet();
        resultSet.next();
        final String result = resultSet.getString(1);
        showStatement.close();
        return result;
    }
}
