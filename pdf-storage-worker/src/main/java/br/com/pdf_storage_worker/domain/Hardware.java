package br.com.pdf_storage_worker.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Hardware {
    private int id;
    private String description;
    private int qty;
    private int assetTag;

}
