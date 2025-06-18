package org.ardev.url_shortener.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class HashRepository {

    private final JdbcTemplate jdbcTemplate;

    @Value("${hash.batch-size}")
    private int batchSize;

    public List<Long> getUniqueNumbers(int amount) {
        String sql = """
                SELECT nextval('unique_number_seq') AS seq
                FROM generate_series(1, ?)
                """;

        return jdbcTemplate.queryForList(sql, Long.class, amount);
    }

    public int[] save(List<String> hashes) {
        String sql = """
                INSERT INTO hash (hash) VALUES (?)
                """;
        List<Object[]> params = hashes.stream()
                .map(hash -> new Object[]{hash})
                .toList();

        return jdbcTemplate.batchUpdate(sql, params);
    }

    public List<String> getHashBatch() {
        String sql = """
                WITH picked AS (
                    SELECT hash
                    FROM hash
                    ORDER BY random()
                    LIMIT ?
                )
                DELETE FROM hash USING picked
                WHERE hash.hash = picked.hash
                RETURNING picked.hash
                """;

        return jdbcTemplate.queryForList(sql, String.class, batchSize);
    }
}
