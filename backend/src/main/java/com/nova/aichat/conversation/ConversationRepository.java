package com.nova.aichat.conversation;

import java.sql.PreparedStatement;
import java.util.List;

import com.nova.aichat.conversation.dto.ConversationSummary;
import com.nova.aichat.conversation.dto.StoredMessage;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

/** Oracle의 CONVERSATIONS와 MESSAGES 테이블을 관리한다. */
@Repository
public class ConversationRepository {
    private final JdbcTemplate jdbcTemplate;

    /** 대화 SQL 실행에 사용할 JdbcTemplate을 주입받는다. */
    public ConversationRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /** 새 대화를 생성하고 Oracle이 만든 대화 ID를 반환한다. */
    public long create(Long userId, String title) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO CONVERSATIONS (USER_ID, TITLE) VALUES (?, ?)",
                new String[] { "CONVERSATION_ID" }
            );
            statement.setLong(1, userId);
            statement.setString(2, title);
            return statement;
        }, keyHolder);
        Number key = keyHolder.getKey();
        if (key == null) {
            throw new IllegalStateException("Oracle이 생성한 대화 ID를 반환하지 않았습니다.");
        }
        return key.longValue();
    }

    /** 해당 대화가 지정된 사용자의 소유인지 확인한다. */
    public boolean belongsTo(Long conversationId, Long userId) {
        Integer count = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM CONVERSATIONS " +
                "WHERE CONVERSATION_ID = ? AND USER_ID = ? AND STATUS = 'ACTIVE'",
            Integer.class, conversationId, userId
        );
        return count != null && count > 0;
    }

    /** 사용자 또는 AI 메시지를 지정된 대화에 저장한다. */
    public void saveMessage(Long conversationId, String role, String content) {
        jdbcTemplate.update(
            "INSERT INTO MESSAGES (CONVERSATION_ID, ROLE, CONTENT) VALUES (?, ?, ?)",
            conversationId, role, content
        );
        jdbcTemplate.update(
            "UPDATE CONVERSATIONS SET UPDATED_AT = CURRENT_TIMESTAMP WHERE CONVERSATION_ID = ?",
            conversationId
        );
    }

    /** 사용자의 활성 대화를 최근 수정 순서로 조회한다. */
    public List<ConversationSummary> findAll(Long userId) {
        return jdbcTemplate.query(
            "SELECT CONVERSATION_ID, TITLE FROM CONVERSATIONS " +
                "WHERE USER_ID = ? AND STATUS = 'ACTIVE' " +
                "ORDER BY NVL(UPDATED_AT, CREATED_AT) DESC",
            (resultSet, rowNumber) -> new ConversationSummary(
                resultSet.getLong("CONVERSATION_ID"), resultSet.getString("TITLE")
            ), userId
        );
    }

    /** 소유권을 확인하면서 특정 대화의 메시지를 시간순으로 조회한다. */
    public List<StoredMessage> findMessages(Long conversationId, Long userId) {
        return jdbcTemplate.query(
            "SELECT m.MESSAGE_ID, m.ROLE, m.CONTENT, m.CREATED_AT " +
                "FROM MESSAGES m JOIN CONVERSATIONS c " +
                "ON c.CONVERSATION_ID = m.CONVERSATION_ID " +
                "WHERE m.CONVERSATION_ID = ? AND c.USER_ID = ? AND c.STATUS = 'ACTIVE' " +
                "ORDER BY m.MESSAGE_ID",
            (resultSet, rowNumber) -> new StoredMessage(
                resultSet.getLong("MESSAGE_ID"), resultSet.getString("ROLE"),
                resultSet.getString("CONTENT"), resultSet.getTimestamp("CREATED_AT").toString()
            ), conversationId, userId
        );
    }
}
