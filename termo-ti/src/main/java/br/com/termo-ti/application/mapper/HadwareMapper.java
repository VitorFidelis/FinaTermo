package br.com.TermoTI.application.mapper;

import br.com.TermoTI.api.request.HadwareRequest;
import br.com.TermoTI.api.response.HadwareResponse;
import br.com.TermoTI.domain.model.Hardware;
import org.springframework.stereotype.Component;

@Component
public class HadwareMapper {

    public final Hardware toEntity(HadwareRequest hadwareRequest) {
        Hardware hardware = new Hardware();
        hardware.setDescription(hadwareRequest.description());
        hardware.setQty(hadwareRequest.qty());
        hardware.setAssetTag(hadwareRequest.assetTag());
        return hardware;
    }

    public final HadwareResponse toResponse(Hardware hardware) {
        return new HadwareResponse(
                hardware.getDescription(),
                hardware.getQty(),
                hardware.getAssetTag());
    }
}
