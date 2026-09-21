package com.nova.aichat.knowledge;

import java.sql.PreparedStatement;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

/** 지식, 문서, 문서 청크를 Oracle에 저장하고 조회한다. */
@Repository
public class KnowledgeRepository {
    private final JdbcTemplate jdbcTemplate;

    /** 지식 SQL 실행에 사용할 JdbcTemplate을 주입받는다. */
    public KnowledgeRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /** 사용자 소유 지식을 저장하고 생성된 ID를 반환한다. */
    public long saveKnowledge(Long userId, String name) {
        return insertAndReturnId(
            "INSERT INTO KNOWLEDGES (USER_ID, NAME) VALUES (?, ?)",
            "KNOWLEDGE_ID", userId, name
        );
    }

    /** 지식에 속한 원본 문서를 저장하고 생성된 ID를 반환한다. */
    public long saveDocument(Long knowledgeId, String fileName, String fileType) {
        return insertAndReturnId(
            "INSERT INTO DOCUMENTS (KNOWLEDGE_ID, FILE_NAME, FILE_TYPE) VALUES (?, ?, ?)",
            "DOCUMENT_ID", knowledgeId, fileName, fileType
        );
    }

    /** 분할된 문서 내용을 순서대로 일괄 저장한다. */
    public void saveChunks(Long documentId, List<String> chunks) {
        for (int index = 0; index < chunks.size(); index++) {
            jdbcTemplate.update(
                "INSERT INTO DOCUMENT_CHUNKS (DOCUMENT_ID, CHUNK_INDEX, CONTENT) VALUES (?, ?, ?)",
                documentId, index, chunks.get(index)
            );
        }
    }

    /** 사용자의 최근 문서 청크를 RAG 후보 개수만큼 조회한다. */
    public List<RagChunk> findRecentChunks(Long userId, int limit) {
        String sql = "SELECT CHUNK_ID, FILE_NAME, CONTENT FROM (" +
            "SELECT c.CHUNK_ID, d.FILE_NAME, c.CONTENT " +
            "FROM DOCUMENT_CHUNKS c " +
            "JOIN DOCUMENTS d ON d.DOCUMENT_ID = c.DOCUMENT_ID " +
            "JOIN KNOWLEDGES k ON k.KNOWLEDGE_ID = d.KNOWLEDGE_ID " +
            "WHERE k.USER_ID = ? AND k.STATUS = 'ACTIVE' AND d.STATUS = 'ACTIVE' " +
            "ORDER BY c.CHUNK_ID DESC) WHERE ROWNUM <= ?";
        return jdbcTemplate.query(sql, (resultSet, rowNumber) -> new RagChunk(
            resultSet.getLong("CHUNK_ID"),
            resultSet.getString("FILE_NAME"),
            resultSet.getString("CONTENT")
        ), userId, limit);
    }

    /** Oracle 생성 키를 요청해 INSERT하고 숫자 ID를 반환한다. */
    private long insertAndReturnId(String sql, String idColumn, Object... parameters) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(
                sql, new String[] { idColumn }
            );
            for (int index = 0; index < parameters.length; index++) {
                statement.setObject(index + 1, parameters[index]);
            }
            return statement;
        }, keyHolder);
        Number key = keyHolder.getKey();
        if (key == null) {
            throw new IllegalStateException("Oracle이 생성한 ID를 반환하지 않았습니다.");
        }
        return key.longValue();
    }
}
