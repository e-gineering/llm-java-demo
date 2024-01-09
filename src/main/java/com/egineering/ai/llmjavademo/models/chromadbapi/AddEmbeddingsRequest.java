package com.egineering.ai.llmjavademo.models.chromadbapi;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AddEmbeddingsRequest {

    private final List<String> ids;
    private final List<float[]> embeddings;
    private final List<String> documents;
    private final List<Map<String, String>> metadatas;

    public AddEmbeddingsRequest(AddEmbeddingsRequest.Builder builder) {
        this.ids = builder.ids;
        this.embeddings = builder.embeddings;
        this.documents = builder.documents;
        this.metadatas = builder.metadatas;
    }

    public static AddEmbeddingsRequest.Builder builder() {
        return new AddEmbeddingsRequest.Builder();
    }

    public static class Builder {

        private List<String> ids = new ArrayList<>();
        private List<float[]> embeddings = new ArrayList<>();
        private List<String> documents = new ArrayList<>();
        private List<Map<String, String>> metadatas = new ArrayList<>();

        public AddEmbeddingsRequest.Builder ids(List<String> ids) {
            if (ids != null) {
                this.ids = ids;
            }
            return this;
        }

        public AddEmbeddingsRequest.Builder embeddings(List<float[]> embeddings) {
            if (embeddings != null) {
                this.embeddings = embeddings;
            }
            return this;
        }

        public AddEmbeddingsRequest.Builder documents(List<String> documents) {
            if (documents != null) {
                this.documents = documents;
            }
            return this;
        }

        public AddEmbeddingsRequest.Builder metadatas(List<Map<String, String>> metadatas) {
            if (metadatas != null) {
                this.metadatas = metadatas;
            }
            return this;
        }

        AddEmbeddingsRequest build() {
            return new AddEmbeddingsRequest(this);
        }
    }
}
