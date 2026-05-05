package be.hers.pi.comprendre_et_parler.DAOs;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TestDatabaseHelper {

    private static final String SCRIPT_CREATION  = "database/Script_creation_BD.sql";
    private static final String SCRIPT_VIEWS      = "database/Script_view_BD.sql";
    private static final String SCRIPT_TRIGGERS   = "database/Script_trigger_BD.sql";

    /**
     * Resets the database to a clean state by reading and executing the project's
     * SQL scripts in the correct dependency order:
     * triggers dropped → views dropped → tables dropped →
     * tables created → views created → triggers created.
     * <p>
     * This method is the single source of truth for the schema: any change to the
     * SQL scripts is automatically picked up on the next test run without modifying
     * this class.
     * </p>
     * Must be called in each {@code @AfterAll} (and in the {@code @BeforeAll} of
     * test classes that depend on a clean schema) to ensure full isolation between
     * test classes.
     *
     * @throws SQLException        if any DDL statement fails during the reset
     * @throws IOException         if a script file cannot be read
     * @throws URISyntaxException  if a script file path cannot be resolved
     */
    public static void resetDatabase() throws SQLException, IOException, URISyntaxException {
        Connection conn = DatabaseConnector.getInstance();

        executeIgnoreErrors(conn, extractDropStatements(SCRIPT_TRIGGERS));
        executeIgnoreErrors(conn, extractDropStatements(SCRIPT_VIEWS));
        executeIgnoreErrors(conn, extractDropStatements(SCRIPT_CREATION));


        executeStrict(conn, extractCreateStatements(SCRIPT_CREATION));
        executeStrict(conn, extractCreateStatements(SCRIPT_VIEWS));

        List<String> triggers = extractPlsqlBlocks(SCRIPT_TRIGGERS);
        executeStrict(conn, triggers);
    }



    /**
     * Reads a SQL script file and extracts only the DROP statements.
     * Statements are delimited by {@code ;} and filtered by the {@code DROP} keyword.
     * The {@code commit} statement and blank lines are ignored.
     *
     * @param scriptPath the path to the SQL file relative to the project root,
     *                   must not be null
     * @return a list of DROP statements without their trailing {@code ;}
     * @throws IOException        if the file cannot be read
     * @throws URISyntaxException if the file path cannot be resolved
     */
    private static List<String> extractDropStatements(String scriptPath)
            throws IOException, URISyntaxException {
        List<String> result = new ArrayList<>();
        String content = readFile(scriptPath);

        for (String line : content.split("\\R")) {
            String trimmed = line.trim();
            if (trimmed.toUpperCase().startsWith("DROP")) {
                if (trimmed.endsWith(";")) {
                    trimmed = trimmed.substring(0, trimmed.length() - 1).trim();
                }
                result.add(trimmed);
            }
        }
        return result;
    }

    /**
     * Reads a SQL script file and extracts all non-DROP statements delimited by
     * {@code ;}. This covers {@code CREATE TABLE}, {@code ALTER TABLE},
     * {@code CREATE VIEW} and {@code INSERT} statements.
     * The {@code commit} statement and blank lines are ignored.
     *
     * @param scriptPath the path to the SQL file relative to the project root,
     *                   must not be null
     * @return a list of CREATE/ALTER/INSERT statements without their trailing {@code ;}
     * @throws IOException        if the file cannot be read
     * @throws URISyntaxException if the file path cannot be resolved
     */
    private static List<String> extractCreateStatements(String scriptPath)
            throws IOException, URISyntaxException {
        List<String> result = new ArrayList<>();
        for (String stmt : splitBySemicolon(readFile(scriptPath))) {
            String upper = stmt.toUpperCase();
            if (!upper.startsWith("DROP") && !upper.equals("COMMIT")) {
                result.add(stmt);
            }
        }
        return result;
    }

    /**
     * Reads a SQL script file and extracts PL/SQL blocks delimited by {@code /}
     * on a line by itself. Used exclusively for trigger scripts where each block
     * ends with {@code END;} followed by {@code /}.
     * DROP statements at the top of the file are excluded.
     *
     * @param scriptPath the path to the SQL file relative to the project root,
     *                   must not be null
     * @return a list of complete PL/SQL blocks (e.g. {@code CREATE TRIGGER ... END;})
     * @throws IOException        if the file cannot be read
     * @throws URISyntaxException if the file path cannot be resolved
     */
    private static List<String> extractPlsqlBlocks(String scriptPath)
            throws IOException, URISyntaxException {
        List<String> result = new ArrayList<>();
        String content = readFile(scriptPath);
        String[] parts = content.split("(?m)^/$");

        for (String part : parts) {
            String trimmed = part.trim();
            if (trimmed.isEmpty()) continue;
            int idx = trimmed.toUpperCase().indexOf("CREATE TRIGGER");
            if (idx >= 0) {
                result.add(trimmed.substring(idx).trim());
            }
        }
        return result;
    }

    /**
     * Executes each statement in the list, propagating any {@link SQLException}.
     * Use this for statements that must succeed (CREATE TABLE, CREATE VIEW,
     * CREATE TRIGGER, ALTER TABLE, INSERT).
     *
     * @param conn       the active database connection, must not be null
     * @param statements the SQL statements to execute, must not be null
     * @throws SQLException if any statement execution fails
     */
    private static void executeStrict(Connection conn, List<String> statements)
            throws SQLException {
        for (String sql : statements) {
            conn.prepareStatement(sql).executeUpdate();
        }
    }

    /**
     * Executes each statement in the list, silently ignoring any
     * {@link SQLException}. Use this for DROP statements that may fail if the
     * object does not exist (ORA-00942 for tables/views, ORA-04080 for triggers).
     *
     * @param conn       the active database connection, must not be null
     * @param statements the SQL statements to execute, must not be null
     */
    private static void executeIgnoreErrors(Connection conn, List<String> statements) {
        for (String sql : statements) {
            try {
                conn.prepareStatement(sql).executeUpdate();
            } catch (SQLException ignored) {}
        }
    }

    /**
     * Reads the content of a SQL script file located at the given path relative
     * to the project root directory.
     *
     * @param relativePath the path to the file relative to the project root,
     *                     must not be null (e.g. {@code "database/Script_creation_BD.sql"})
     * @return the full content of the file as a string
     * @throws IOException        if the file cannot be read
     * @throws URISyntaxException if the project root path cannot be resolved
     */
    private static String readFile(String relativePath)
            throws IOException, URISyntaxException {
        Path projectRoot = Paths.get(
                TestDatabaseHelper.class
                        .getProtectionDomain()
                        .getCodeSource()
                        .getLocation()
                        .toURI()
        ).getParent().getParent();

        Path filePath = projectRoot.resolve(relativePath);
        return Files.readString(filePath);
    }

    /**
     * Splits a SQL script into individual statements using {@code ;} as delimiter.
     * Empty statements, whitespace-only entries and the {@code commit} keyword
     * are filtered out. Each returned statement is trimmed.
     *
     * @param content the raw content of a SQL script, must not be null
     * @return a list of individual SQL statements without their trailing {@code ;}
     */
    private static List<String> splitBySemicolon(String content) {
        List<String> result = new ArrayList<>();
        for (String part : content.split(";")) {
            String trimmed = part.trim();
            if (!trimmed.isEmpty() && !trimmed.equalsIgnoreCase("commit")) {
                result.add(trimmed);
            }
        }
        return result;
    }
}