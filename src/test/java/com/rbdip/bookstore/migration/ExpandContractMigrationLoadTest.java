package com.rbdip.bookstore.migration;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationInfo;
import org.junit.jupiter.api.Test;
import org.postgresql.ds.PGSimpleDataSource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
class ExpandContractMigrationLoadTest {

    private static final int READER_THREADS = 4;
    private static final long TIMEOUT_SECONDS = 5;

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("bookstore_load")
            .withUsername("bookstore")
            .withPassword("bookstore");

    @Test
    void expandStepsServeOldColumnWhileMigrationRuns() throws Exception {
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setUrl(postgres.getJdbcUrl());
        dataSource.setUser(postgres.getUsername());
        dataSource.setPassword(postgres.getPassword());

        Flyway flyway = Flyway.configure()
                .dataSource(dataSource)
                .locations("classpath:db/migration")
                .load();

        MigrationInfo[] pending = flyway.info().pending();
        assertThat(pending.length).isGreaterThan(1);

        Flyway.configure().dataSource(dataSource).target("1").load().migrate();
        String lastBeforeContract = pending[pending.length - 2].getVersion().getVersion();

        List<Exception> errors = new CopyOnWriteArrayList<>();
        AtomicBoolean keepReading = new AtomicBoolean(true);
        CountDownLatch started = new CountDownLatch(1);
        ExecutorService pool = Executors.newFixedThreadPool(READER_THREADS);

        Runnable reader = () -> {
            started.countDown();
            while (keepReading.get()) {
                try (Connection connection = dataSource.getConnection();
                        Statement statement = connection.createStatement();
                        ResultSet resultSet = statement.executeQuery(
                                "SELECT customer_full_name FROM orders LIMIT 1")) {
                    resultSet.next();
                } catch (SQLException e) {
                    errors.add(e);
                }
            }
        };
        for (int i = 0; i < READER_THREADS; i++) {
            pool.submit(reader);
        }
        started.await(TIMEOUT_SECONDS, TimeUnit.SECONDS);

        Flyway.configure().dataSource(dataSource).target(lastBeforeContract).load().migrate();

        keepReading.set(false);
        pool.shutdown();
        pool.awaitTermination(TIMEOUT_SECONDS, TimeUnit.SECONDS);

        assertThat(errors)
                .as("Старые читатели customer_full_name не должны падать во время expand-миграций")
                .isEmpty();

        Flyway.configure().dataSource(dataSource).load().migrate();

        List<String> orderColumns = columnsOf(dataSource, "orders");
        List<String> customerColumns = columnsOf(dataSource, "customers");
        assertThat(orderColumns).doesNotContain("customer_full_name");
        assertThat(customerColumns).contains("first_name", "last_name").doesNotContain("full_name");
    }

    private static List<String> columnsOf(PGSimpleDataSource dataSource, String table) throws SQLException {
        List<String> columns = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(
                        "SELECT column_name FROM information_schema.columns WHERE table_name = '" + table + "'")) {
            while (resultSet.next()) {
                columns.add(resultSet.getString(1));
            }
        }
        return columns;
    }
}
