package com.egineering.ai.llmjavademo.repositories;

import com.egineering.ai.llmjavademo.models.Faq;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface FaqRepository extends MongoRepository<Faq, String> {}
