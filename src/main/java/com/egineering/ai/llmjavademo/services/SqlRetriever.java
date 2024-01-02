package com.egineering.ai.llmjavademo.services;

import com.egineering.ai.llmjavademo.agents.SqlAgent;
import com.egineering.ai.llmjavademo.repositories.WideFlangeBeamRepository;
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
              'CREATE TABLE ' || relname || E'\\n(\\n' ||
              array_to_string(
                array_agg(
                  '    ' || column_name || ' ' ||  type || ' '|| not_null
                )
                , E',\\n'
              ) || E'\\n);\\n'
            from
            (
              SELECT\s
                c.relname, a.attname AS column_name,
                pg_catalog.format_type(a.atttypid, a.atttypmod) as type,
                case\s
                  when a.attnotnull
                then 'NOT NULL'
                else 'NULL'
                END as not_null\s
              FROM pg_class c,
               pg_attribute a,
               pg_type t
               WHERE c.relname = 'wide_flange_beams'
               AND a.attnum > 0
               AND a.attrelid = c.oid
               AND a.atttypid = t.oid
             ORDER BY a.attnum
            ) as tabledefinition
            group by relname
            """;

    private final JdbcTemplate jdbcTemplate;
    private final WideFlangeBeamRepository dataBase;
    private final SqlAgent sqlAgent;

    public SqlRetriever(JdbcTemplate jdbcTemplate, WideFlangeBeamRepository dataBase, SqlAgent sqlAgent) {
        this.jdbcTemplate = jdbcTemplate;
        this.dataBase = dataBase;
        this.sqlAgent = sqlAgent;
    }

    @Override
    public List<TextSegment> findRelevant(String text) {

        String tableDdl = jdbcTemplate.queryForObject(DDL_SQL, String.class);

        String sql = sqlAgent.generate(tableDdl, text);

        List<Map<String, Object>> results = jdbcTemplate.queryForList(sql);

        return results.stream()
                .map(stringObjectMap -> stringObjectMap.entrySet().stream()
                        .map(stringObjectEntry -> stringObjectEntry.getKey() + ": " + stringObjectEntry.getValue().toString()).collect(Collectors.joining(",")))
                .map(row -> new TextSegment(row, new Metadata()))
                .toList();
    }
}
