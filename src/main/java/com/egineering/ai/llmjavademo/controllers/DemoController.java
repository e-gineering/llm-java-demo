package com.egineering.ai.llmjavademo.controllers;

import com.egineering.ai.llmjavademo.agents.DocumentAgent;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.InputStream;

@RestController
@RequestMapping("/api")
public class DemoController {

    private final DemoService service;
    private final DocumentAgent documentAgent;

    public DemoController(DemoService service, DocumentAgent documentAgent) {
        this.service = service;
        this.documentAgent = documentAgent;
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

    @PostMapping("/generateBasic")
    public ResponseEntity<?> postGenerateBasic(@RequestBody MessageForm form) {
        return ResponseEntity.ok(service.generateBasic(form));
    }

    @PostMapping("/generateFaq")
    public ResponseEntity<?> postGenerateFaq(@RequestBody MessageForm form) {
        return ResponseEntity.ok(service.generateFaq(form));
    }

    @PostMapping("/generateDocs")
    public ResponseEntity<?> postGenerateDocs(@RequestBody MessageForm form) {
        return ResponseEntity.ok(service.generateDocs(form));
    }

    @PostMapping("/generateData")
    public ResponseEntity<?> postGenerateData(@RequestBody MessageForm form) {
        return ResponseEntity.ok(service.generateData(form));
    }

    @MessageMapping("/llmStreamingRequest")
    @SendTo("/topic/llmResponse")
    public StreamingLlmResponse receiveLlmStreamingRequest(MessageForm form) {
        return documentAgent.generate(form);
    }
}
