package br.com.TermoTI.api.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record TermResponse(
                UUID id,
                String employeeName,
                String department,
                List<HadwareResponse> hardwares,
                LocalDateTime dateReceipt

) {

}
