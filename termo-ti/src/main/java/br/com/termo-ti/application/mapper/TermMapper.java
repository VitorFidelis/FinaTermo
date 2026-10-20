package br.com.TermoTI.application.mapper;

import br.com.TermoTI.api.request.TermRequest;
import br.com.TermoTI.api.response.HadwareResponse;
import br.com.TermoTI.api.response.TermResponse;
import br.com.TermoTI.domain.model.Hardware;
import br.com.TermoTI.domain.model.Term;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TermMapper {

    private final HadwareMapper hadwareMapper;

    public final Term toEntity(TermRequest termoRequest) {

        // Faz a conversão da lista de HadwareRequest para uma lista de Hardware usando
        // o HadwareMapper
        List<Hardware> hardwareList = termoRequest.hardwares().stream()
                .map(hadwareMapper::toEntity)
                .toList();

        // Cria uma nova instância de Term e define seus atributos com base no
        // TermRequest
        Term termo = new Term();
        termo.setEmployeeName(termoRequest.employeeName());
        termo.setDepartment(termoRequest.department());
        termo.setDateReceipt(LocalDateTime.now());
        termo.setHardwares(hardwareList);
        return termo;
    }

    public final TermResponse toResponse(Term termo) {

        // Faz a conversão da lista de Hardware para uma lista de HadwareResponse usando
        // o HadwareMapper
        List<HadwareResponse> hardwareList = termo.getHardwares()
                .stream()
                .map(hadwareMapper::toResponse)
                .toList();

        // Cria uma nova instância de TermResponse e define seus atributos com base no
        // Termo
        return new TermResponse(
                termo.getId(),
                termo.getEmployeeName(),
                termo.getDepartment(),
                hardwareList,
                termo.getDateReceipt());
    }
}
