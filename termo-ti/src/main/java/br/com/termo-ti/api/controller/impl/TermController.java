package br.com.TermoTI.api.controller.impl;

import br.com.TermoTI.api.controller.TermApi;
import br.com.TermoTI.api.request.TermRequest;
import br.com.TermoTI.api.response.TermResponse;
import br.com.TermoTI.application.service.impl.TermServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/terms")
public class TermController implements TermApi {

        private final TermServiceImpl termoService;

        @Override
        public ResponseEntity<TermResponse> createTerm(
                        TermRequest termoRequest) {
                return ResponseEntity
                                .status(HttpStatus.CREATED)
                                .body(termoService.createTermo(termoRequest));
        }

        @Override
        public ResponseEntity<Void> uploadAssinaturas(
                        UUID id,
                        MultipartFile employeeSignature,
                        MultipartFile responsibleItSignature) {
                termoService.uploadAssinaturas(
                                id,
                                employeeSignature,
                                responsibleItSignature);
                return ResponseEntity.noContent().build();
        }
}