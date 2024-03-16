package br.com.fiap.gerente_itens.controller.form;

import br.com.fiap.gerente_itens.dto.ItensDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ItensForm {

    @JsonProperty
    @NotBlank(message = "Campo NOME é obrigatorio")
    private String nome;

    @JsonProperty
    @NotBlank(message = "Campo VALOR é obrigatorio")
    private String valor;

    @JsonProperty
    @NotBlank(message = "Campo QUANTIDADE é obrigatorio")
    private String estoque;

    public ItensDto toItensDto() {
        return new ItensDto(nome, valor, estoque);
    }

}
