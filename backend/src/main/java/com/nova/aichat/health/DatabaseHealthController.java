package com.nova.aichat.health;

import java.util.Collections;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Oracle 연결 상태를 확인하는 상태 점검 API를 제공한다. */
@RestController
@RequestMapping("/api/health")
public class DatabaseHealthController {

    // Spring이 구성한 Oracle DataSource를 통해 SQL을 실행한다.
    private final JdbcTemplate jdbcTemplate;

    /** 상태 확인 SQL을 실행할 JdbcTemplate을 주입받는다. */
    public DatabaseHealthController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /** Oracle DUAL 조회에 성공하면 UP 상태를 반환한다. */
    @GetMapping("/database")
    public Map<String, String> databaseHealth() {
        // Oracle 전용 DUAL 테이블을 조회하여 실제 연결 가능 여부를 확인한다.
        Integer result = jdbcTemplate.queryForObject("SELECT 1 FROM DUAL", Integer.class);

        if (!Integer.valueOf(1).equals(result)) {
            throw new IllegalStateException("Unexpected Oracle health check result");
        }

        return Collections.singletonMap("status", "UP");
    }
}
