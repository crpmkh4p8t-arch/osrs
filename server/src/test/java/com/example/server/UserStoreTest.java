package com.example.server;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;

public class UserStoreTest {
    static HikariDataSource ds;

    @BeforeAll
    public static void setup() {
        HikariConfig cfg = new HikariConfig();
        cfg.setJdbcUrl("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1");
        cfg.setUsername("sa");
        cfg.setPassword("");
        ds = new HikariDataSource(cfg);
    }

    @AfterAll
    public static void tearDown() {
        if (ds != null) ds.close();
    }

    @Test
    public void registerAndValidate() {
        UserRepository repo = new UserRepository(ds);
        UserStore store = new UserStore(repo);

        boolean ok = store.register("alice", "s3cret!");
        Assertions.assertTrue(ok);

        Assertions.assertTrue(store.validate("alice", "s3cret!"));
    }

    @Test
    public void rejectsShortPassword() {
        UserRepository repo = new UserRepository(ds);
        UserStore store = new UserStore(repo);
        Assertions.assertFalse(store.register("bob", "123"));
    }

    @Test
    public void unknownUserFails() {
        UserRepository repo = new UserRepository(ds);
        UserStore store = new UserStore(repo);
        Assertions.assertFalse(store.validate("nobody", "nope"));
    }
}