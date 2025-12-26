package com.example.server;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserRepository {
    private final DataSource ds;

    public UserRepository(DataSource ds) {
        this.ds = ds;
        initSchema();
    }

    private void initSchema() {
        try (Connection c = ds.getConnection();
             PreparedStatement s = c.prepareStatement(
                     "CREATE TABLE IF NOT EXISTS users (username VARCHAR PRIMARY KEY, hash VARCHAR)")) {
            s.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void save(String username, String hash) {
        try (Connection c = ds.getConnection();
             PreparedStatement s = c.prepareStatement("MERGE INTO users (username, hash) KEY(username) VALUES (?, ?)") ) {
            s.setString(1, username);
            s.setString(2, hash);
            s.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public String findHashByUsername(String username) {
        try (Connection c = ds.getConnection();
             PreparedStatement s = c.prepareStatement("SELECT hash FROM users WHERE username = ?")) {
            s.setString(1, username);
            try (ResultSet rs = s.executeQuery()) {
                if (rs.next()) return rs.getString(1);
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}