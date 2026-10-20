package br.com.pdf_storage_worker.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Term {
    private UUID id;
    private String employeeName;
    private String department;
    private List<Hardware> hardwares = new ArrayList<>();
    private LocalDateTime dateReceipt;
    private String employeeSignature;
    private String responsibleItSignature;

    public void getDateReceipt(LocalDateTime now) {
        this.dateReceipt = now;
    }
}
