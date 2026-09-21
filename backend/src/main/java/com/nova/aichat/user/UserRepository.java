package com.nova.aichat.user;

import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/** Oracle USERS 테이블의 사용자 데이터를 조회하고 저장한다. */
@Repository
public class UserRepository {
    private final JdbcTemplate jdbcTemplate;

    /** 사용자 SQL 실행에 사용할 JdbcTemplate을 주입받는다. */
    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /** 정규화된 이메일로 사용자 한 명을 조회한다. */
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

    /** BCrypt로 암호화된 비밀번호와 사용자 정보를 저장한다. */
    public void save(String email, String encodedPassword, String name) {
        // USER_ID는 Oracle Identity 컬럼이므로 INSERT 대상에서 제외한다.
        jdbcTemplate.update(
            "INSERT INTO USERS (EMAIL, PASSWORD, NAME) VALUES (?, ?, ?)",
            email, encodedPassword, name
        );
    }
}
