package br.com.pdf_storage_worker.api;

import java.util.List;

public record CreateTermListenerDTO(
                String id,
                String employeeName,
                String department,
                List<HadwareListenerDTO> hardwares) {

}
