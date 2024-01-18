package com.egineering.ai.llmjavademo.controllers;

import com.egineering.ai.llmjavademo.dtos.MessageForm;
import com.egineering.ai.llmjavademo.dtos.StreamingLlmResponse;
import com.egineering.ai.llmjavademo.services.DemoService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.InputStream;

@RestController
@RequestMapping("/api")
public class DemoController {

    private final DemoService service;

    public DemoController(DemoService service) {
        this.service = service;
    }

    @GetMapping("/test")
    public ResponseEntity<?> getTest() {
        return ResponseEntity.ok(service.generateBasic(new MessageForm("Why is the sky blue?")));
    }

    @GetMapping("/files/{fileName}")
    public ResponseEntity<InputStreamResource> getFile(@PathVariable String fileName) {
        InputStream in = getClass().getResourceAsStream("/documents/%s".formatted(fileName));
        if (in != null) {
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(new InputStreamResource(in));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @MessageMapping("/basic/llmStreamingRequest")
    @SendTo("/topic/basic/llmResponse")
    public StreamingLlmResponse receiveLlmStreamingRequestBasic(MessageForm form) {
        return service.generateBasic(form);
    }

    @MessageMapping("/faq/llmStreamingRequest")
    @SendTo("/topic/faq/llmResponse")
    public StreamingLlmResponse receiveLlmStreamingRequestFaq(MessageForm form) {
        return service.generateFaq(form);
    }

    @MessageMapping("/documents/llmStreamingRequest")
    @SendTo("/topic/documents/llmResponse")
    public StreamingLlmResponse receiveLlmStreamingRequestDocuments(MessageForm form) {
        return service.generateDocuments(form);
    }

    @MessageMapping("/data/llmStreamingRequest")
    @SendTo("/topic/data/llmResponse")
    public StreamingLlmResponse receiveLlmStreamingRequestData(MessageForm form) {
        return service.generateData(form);
    }
}
