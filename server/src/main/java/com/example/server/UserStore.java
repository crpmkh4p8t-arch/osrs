package com.example.server;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class UserStore {
    private final UserRepository repo;

    public UserStore(UserRepository repo) {
        this.repo = repo;
    }

    public boolean register(String username, String password) {
        if (username == null || username.isBlank() || password == null || password.length() < 6) return false;
        String hash = BCrypt.withDefaults().hashToString(12, password.toCharArray());
        repo.save(username, hash);
        return true;
    }

    public boolean validate(String username, String password) {
        String hash = repo.findHashByUsername(username);
        if (hash == null) return false;
        BCrypt.Result res = BCrypt.verifyer().verify(password.toCharArray(), hash);
        return res.verified;
    }
}