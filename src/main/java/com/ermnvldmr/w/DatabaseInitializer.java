package com.ermnvldmr.w;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@SuppressWarnings("unused")
public class DatabaseInitializer implements ApplicationRunner {
    private final JdbcTemplate jdbcTemplate;

    public DatabaseInitializer(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(ApplicationArguments args) {
        String columnName = "password_sha";
        String tableName = "user";

        // Check if the column already exists
        String checkColumnSql = "SELECT COUNT(*) FROM information_schema.COLUMNS " +
                "WHERE TABLE_NAME = ? AND COLUMN_NAME = ?";

        Integer count = jdbcTemplate.queryForObject(checkColumnSql, new Object[]{tableName, columnName}, Integer.class);

        // If the column does not exist, add it
        if (count != null && count == 0) {
            String sql = "ALTER TABLE `" + tableName + "` ADD `" + columnName + "` VARCHAR(64) NULL AFTER `login`;";
            jdbcTemplate.execute(sql);
        }
    }
}