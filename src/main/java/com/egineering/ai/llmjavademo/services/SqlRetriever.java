package com.egineering.ai.llmjavademo.services;

import com.egineering.ai.llmjavademo.agents.SqlAgent;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.retriever.Retriever;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SqlRetriever implements Retriever<TextSegment> {

    private static final String DDL_SQL = """
            SELECT
              'CREATE TABLE ' || relname || ' (' ||
              array_to_string(array_agg(column_name || ' ' ||  type || ' ' || not_null), ', ') || ');'
            from
            (
              SELECT
                c.relname, a.attname AS column_name,
                pg_catalog.format_type(a.atttypid, a.atttypmod) as type,
                case
                  when a.attnotnull
                then 'NOT NULL'
                else 'NULL'
                END as not_null\s
              FROM pg_class c,
               pg_attribute a,
               pg_type t
               WHERE c.relname in('wide_flange_beams', 'square_tubing')
               AND a.attnum > 0
               AND a.attrelid = c.oid
               AND a.atttypid = t.oid
             ORDER BY a.attnum
            ) as tabledefinition
            group by relname
            """;

    private final JdbcTemplate jdbcTemplate;
    private final SqlAgent sqlAgent;

    public SqlRetriever(JdbcTemplate jdbcTemplate, SqlAgent sqlAgent) {
        this.jdbcTemplate = jdbcTemplate;
        this.sqlAgent = sqlAgent;
    }

    @Override
    public List<TextSegment> findRelevant(String text) {

        List<Map<String, Object>> tableDdlResult = jdbcTemplate.queryForList(DDL_SQL);

        String tableDdl = tableDdlResult.stream()
                .map(m -> m.values().stream()
                        .map(Object::toString)
                        .collect(Collectors.joining()))
                .collect(Collectors.joining("\n"));

        String sql = sqlAgent.generate(tableDdl, text);

        List<Map<String, Object>> results = jdbcTemplate.queryForList(sql);

        return results.stream()
                .map(stringObjectMap -> stringObjectMap.entrySet().stream()
                        .map(stringObjectEntry -> stringObjectEntry.getKey() + ": " + stringObjectEntry.getValue().toString()).collect(Collectors.joining(",")))
                .map(row -> new TextSegment(row, new Metadata()))
                .toList();
    }
}
