package br.com.TermoTI.application.service;

import br.com.TermoTI.api.request.TermRequest;
import br.com.TermoTI.api.response.TermResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface TermService {
    TermResponse createTermo(TermRequest termoRequest);

    List<TermResponse> findAll();

    TermResponse findById(UUID uuid);

    void deleteById(UUID uuid);

    public void uploadAssinaturas(
            UUID id,
            MultipartFile employeeSignature,
            MultipartFile responsibleItSignature);
}
