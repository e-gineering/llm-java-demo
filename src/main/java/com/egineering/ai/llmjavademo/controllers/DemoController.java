package com.egineering.ai.llmjavademo.controllers;

import com.egineering.ai.llmjavademo.dtos.LlmResponse;
import com.egineering.ai.llmjavademo.dtos.MessageForm;
import com.egineering.ai.llmjavademo.services.DemoService;
import org.springframework.ai.client.AiClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
