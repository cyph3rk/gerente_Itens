package br.com.fiap.gerente_itens.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Item {

    private int id;

    private String nome;

    private String descricao;

}
