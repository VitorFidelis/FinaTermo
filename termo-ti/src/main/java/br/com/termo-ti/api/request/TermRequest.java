package br.com.TermoTI.api.request;

import java.util.List;

public record TermRequest(
                String employeeName,
                String department,
                List<HadwareRequest> hardwares) {

}
