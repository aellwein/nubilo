package de.steinpfeffer.nubilo.core.persistence;

import static java.lang.String.format;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class to facilitate database related testing.
 * 
 * @author Juergen Fickel
 * @since 1.0.0
 */
final class DatabaseUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseUtil.class);

    private DatabaseUtil() {
        super();
    }

    public static DatabaseUtil getInstance() {
        return new DatabaseUtil();
    }

    public int executeSqlScript(final String scriptPath, final Statement statement) {
        int result = 0;
        final Iterable<String> scriptStatements = tryToReadFile(scriptPath);
        for (final String scriptStatement : scriptStatements) {
            result += tryToExecuteStatement(statement, scriptStatement);
        }
        return result;
    }

    private static Iterable<String> tryToReadFile(final String filePath) {
        try {
            return readFile(filePath);
        } catch (final IOException e) {
            LOGGER.error(format("Unable to read file '%s'.", filePath), e);
            return Collections.emptyList();
        }
    }

    private static Iterable<String> readFile(final String filePath) throws IOException {
        final ArrayList<String> result = new ArrayList<>();
        final String lineBreak = "\n";
        final BufferedReader in = new BufferedReader(new FileReader(filePath));
        String str = in.readLine();
        StringBuilder stringBuilder = new StringBuilder();
        while (null != str) {
            if (!str.isEmpty()) {
                stringBuilder.append(str).append(lineBreak);
                if (isEndOfStatement(str)) {
                    result.add(stringBuilder.toString());
                    stringBuilder = new StringBuilder();
                }
            }
            str = in.readLine();
        }
        in.close();
        result.trimToSize();
        return result;
    }

    private static boolean isEndOfStatement(final String statementLine) {
        return statementLine.endsWith(";");
    }

    private int tryToExecuteStatement(final Statement statement, final String fileContent) {
        try {
            return statement.executeUpdate(fileContent);
        } catch (final SQLException e) {
            LOGGER.error("Unable to execute the given statement.", e);
            return -1;
        }
    }

}
