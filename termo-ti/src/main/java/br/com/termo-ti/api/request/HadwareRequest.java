package br.com.TermoTI.api.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record HadwareRequest(

        @NotBlank(message = "The DESCRIPTION field cannot be blank") 
        String description,

        @Min(value = 1, message = "The QUANTITY field cannot be null") 
        int qty,

        @NotBlank(message = "The DESCRIPTION field cannot be blank")
        @Pattern(regexp = "^^[1-9][0-9][0-9]*$", message = "The ASSET TAG field must contain only numbers") 
        String assetTag
) {

}
