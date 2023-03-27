package no.ding.pk.listener;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

import javax.sql.DataSource;
import jakarta.transaction.Transactional;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class CleanUpH2DatabaseService {
    public static final String H2_DB_PRODUCT_NAME = "H2";
    private final DataSource dataSource;


    @SneakyThrows
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public void cleanUp(String schemaName) {
        try (Connection connection = dataSource.getConnection(); Statement statement = connection.createStatement()) {
            if(isH2Database(connection)) {
                disableConstraints(statement);
                truncateTables(statement, schemaName);
                resetSequences(statement, schemaName);
                enableConstraints(statement);

            } else {
                log.warn("Skipping cleaning up database, because it's not a H2 database");
            }
        }
    }

    private void resetSequences(Statement statement, String schemaName) {
        getSchemaSequences(statement, schemaName).forEach(sequenceName ->
                executeStatement(statement, String.format("ALTER SEQUENCE %s RESTART WITH 1", sequenceName)));
    }

    private void truncateTables(Statement statement, String schemaName) {
        getSchemaTables(statement, schemaName)
                .forEach(tableName -> executeStatement(statement, "TRUNCATE TABLE " + tableName));
    }

    private void enableConstraints(Statement statement) {
        executeStatement(statement, "SET REFERENTIAL_INTEGRITY TRUE");
    }

    private void disableConstraints(Statement statement) {
        executeStatement(statement, "SET REFERENTIAL_INTEGRITY FALSE");
    }


    @SneakyThrows
    private boolean isH2Database(Connection connection) {
        return H2_DB_PRODUCT_NAME.equals(connection.getMetaData().getDatabaseProductName());
    }

    @SneakyThrows
    private void executeStatement(Statement statement, String sql) {
        statement.executeUpdate(sql);
    }

    @SneakyThrows
    private Set<String> getSchemaTables(Statement statement, String schemaName) {
        String sql = String.format("SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES  where TABLE_SCHEMA='%s'", schemaName);
        return queryForList(statement, sql);
    }

    @SneakyThrows
    private Set<String> getSchemaSequences(Statement statement, String schemaName) {
        String sql = String.format("SELECT SEQUENCE_NAME FROM INFORMATION_SCHEMA.SEQUENCES WHERE SEQUENCE_SCHEMA='%s'", schemaName);
        return queryForList(statement, sql);
    }

    @SneakyThrows
    private Set<String> queryForList(Statement statement, String sql) {
        Set<String> tables = new HashSet<>();
        try (ResultSet rs = statement.executeQuery(sql)) {
            while (rs.next()) {
                tables.add(rs.getString(1));
            }
        }
        return tables;
    }

}
