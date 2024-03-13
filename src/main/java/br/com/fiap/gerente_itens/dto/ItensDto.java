package br.com.fiap.gerente_itens.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ItensDto {

    private Long id;
    private String nome;
    private String valor;
    private String qtd;

    public ItensDto() {

    }

    public ItensDto(String nome, String valor, String qtd) {
        this.nome = nome;
        this.valor = valor;
        this.qtd = qtd;
    }

}
