package com.egineering.ai.llmjavademo.models;


import org.springframework.data.annotation.Id;

public record Faq(@Id String id, String question, String answer) {
}
