package com.nova.aichat.user;

import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepository {
    private final JdbcTemplate jdbcTemplate;

    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<UserAccount> findByEmail(String email) {
        List<UserAccount> users = jdbcTemplate.query(
            "SELECT USER_ID, EMAIL, PASSWORD, NAME, STATUS FROM USERS WHERE EMAIL = ?",
            (resultSet, rowNumber) -> new UserAccount(
                resultSet.getLong("USER_ID"), resultSet.getString("EMAIL"),
                resultSet.getString("PASSWORD"), resultSet.getString("NAME"),
                resultSet.getString("STATUS")
            ), email
        );
        return users.stream().findFirst();
    }

    public void save(String email, String encodedPassword, String name) {
        // USER_ID는 Oracle Identity 컬럼이므로 INSERT 대상에서 제외한다.
        jdbcTemplate.update(
            "INSERT INTO USERS (EMAIL, PASSWORD, NAME) VALUES (?, ?, ?)",
            email, encodedPassword, name
        );
    }
}
