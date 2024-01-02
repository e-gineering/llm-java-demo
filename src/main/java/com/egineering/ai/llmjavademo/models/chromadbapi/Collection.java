package com.egineering.ai.llmjavademo.models.chromadbapi;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Collection {

    @JsonProperty
    private String name;

    @JsonProperty
    private String id;

    @JsonProperty
    private Metadata metadata;

    @JsonProperty
    private String tenant;

    @JsonProperty
    private String database;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Metadata {

        @JsonAlias("hnsw:space")
        private String hnswSpace;
    }
}
