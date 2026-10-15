package br.com.TermoTI.application.service.impl;

import br.com.TermoTI.api.request.TermRequest;
import br.com.TermoTI.api.response.TermResponse;
import br.com.TermoTI.application.mapper.TermMapper;
import br.com.TermoTI.application.service.TermService;
import br.com.TermoTI.domain.exception.EmployeeSignatureIsEmptyException;
import br.com.TermoTI.domain.exception.ErrorWhileSavingImageException;
import br.com.TermoTI.domain.exception.ResponsibleITSignatureIsEmptyException;
import br.com.TermoTI.domain.exception.TermNotFoundException;
import br.com.TermoTI.domain.model.Hardware;
import br.com.TermoTI.domain.model.Term;
import br.com.TermoTI.domain.repository.TermRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TermServiceImpl implements TermService {

    private final TermRepository termoRepository;

    private final TermMapper termoMapper;

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.queue.name}")
    private String QUEUE_NAME;

    @Override
    public TermResponse createTermo(TermRequest termoRequest) {
        // converte DTO para Entidade
        Term termoEntity = this.termoMapper.toEntity(termoRequest);

        // verifica se a lista de hardwares não está nula ou vazia
        if (termoEntity.getHardwares() != null && !termoEntity.getHardwares().isEmpty()) {

            // percorre TODOS os hardwares da lista
            for (Hardware hardware : termoEntity.getHardwares()) {
                String assetTag = hardware.getAssetTag();

                if (assetTag == null || assetTag.isBlank()) {
                    hardware.setAssetTag("0000");
                } else {
                    hardware.setAssetTag("00" + assetTag.trim());
                }
            }
        }
        // salva a entidade no banco de dados
        Term saved = this.termoRepository.save(termoEntity);

        // Envia para a fila no RabbitMQ
        rabbitTemplate.convertAndSend(QUEUE_NAME, saved);

        // retorna o DTO de resposta
        return this.termoMapper.toResponse(saved);
    }

    @Override
    public List<TermResponse> findAll() {
        return List.of();
    }

    @Override
    public TermResponse findById(UUID uuid) {
        return null;
    }

    @Override
    public void deleteById(UUID uuid) {

    }

    @Override
    public void uploadAssinaturas(UUID id, MultipartFile employeeSignature, MultipartFile responsibleItSignature) {

        // Busca o termo pelo ID no repositório, lançando uma exceção se não for
        // encontrado
        var termo = this.termoRepository.findById(id)
                .orElseThrow(() -> new TermNotFoundException("term not found"));

        // Salva as assinaturas no sistema de arquivos e obtém os caminhos relativos
        String employeePath = this.savedImagePath(
                id,
                employeeSignature,
                "employee-signature");

        String responsiblePath = this.savedImagePath(
                id,
                responsibleItSignature,
                "responsible-it-signature");

        // Verifica se os arquivos de assinatura estão vazios e lança uma exceção se
        // estiverem,
        // caso contrário, define os caminhos das assinaturas no objeto termo
        if (employeeSignature.isEmpty()) {
            throw new EmployeeSignatureIsEmptyException("Employee signature file is empty");
        } else {
            termo.setEmployeeSignature(employeePath);
        }

        if (responsibleItSignature.isEmpty()) {
            throw new ResponsibleITSignatureIsEmptyException("Responsible IT signature file is empty");
        } else {
            termo.setResponsibleItSignature(responsiblePath);
        }

        // Salva o objeto termo atualizado no repositório
        termoRepository.save(termo);
    }

    private String savedImagePath(UUID termId, MultipartFile file, String subscriptionType) {
        try {

            // Cria a pasta especifica do termo
            Path path = Paths.get(
                    "uploads",
                    "assinaturas",
                    termId.toString());

            // Cria o diretório caso não existir
            Files.createDirectories(path);

            // Define o nome da assinatura
            String fileName = subscriptionType + ".png";

            // Caminho completo do arquivo
            Path destination = path.resolve(fileName);

            // Copia o arquivo para o diretório de destino, substituindo se já existir
            Files.copy(
                    file.getInputStream(),
                    destination,
                    StandardCopyOption.REPLACE_EXISTING);

            // Retorna o caminho que será armazenado no banco
            return "/uploads/termos/"
                    + termId
                    + "/"
                    + fileName;

        } catch (IOException e) {
            throw new ErrorWhileSavingImageException("Error while saving image", e);
        }
    }

}
