package br.com.TermoTI.api.controller;

import br.com.TermoTI.api.request.TermRequest;
import br.com.TermoTI.api.response.TermResponse;
import jakarta.validation.Valid;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface TermApi {

        @PostMapping
        ResponseEntity<TermResponse> createTerm(
                @Valid 
                @RequestBody(required = true) TermRequest termoRequest
        );

        @PostMapping(value = "/{id}/signatures", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
        ResponseEntity<Void> uploadAssinaturas(
                @Valid @PathVariable UUID id,
                @RequestPart(required = true, name = "employeeSignature") MultipartFile employeeSignature,
                @RequestPart(required = true, name = "responsibleItSignature") MultipartFile responsibleItSignature
        );
}
