package br.com.fiap.gerente_itens.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "itens")
public class Itens {

    @JsonProperty
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "serial")
    private long id;

    @JsonProperty
    private String nome;

    @JsonProperty
    private String valor;

    @JsonProperty
    private String estoque;

    public Itens(String nome, String valor, String estoque) {
        this.nome = nome;
        this.valor = valor;
        this.estoque = estoque;
    }

    public Itens() {

    }

}
