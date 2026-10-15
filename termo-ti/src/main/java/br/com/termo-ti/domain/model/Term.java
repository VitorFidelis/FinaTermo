package br.com.TermoTI.domain.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Term {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "employee_name", nullable = false)
    private String employeeName;

    @Column(name = "department", nullable = false)
    private String department;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "termo_id", nullable = false)
    private List<Hardware> hardwares = new ArrayList<>();

    @Column(name = "date_receipt", nullable = false)
    @CreatedDate
    private LocalDateTime dateReceipt;

    @Column(name = "employee_signature", nullable = true)
    private String employeeSignature;

    @Column(name = "responsible_it_signature", nullable = true)
    private String responsibleItSignature;

    public void getDateReceipt(LocalDateTime now) {
        this.dateReceipt = now;
    }
}
