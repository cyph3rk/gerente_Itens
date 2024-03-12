package br.com.fiap.gerente_itens.controller;

import br.com.fiap.gerente_itens.model.Item;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

@RestController
public class ItenController {

    private List<Item> itens = Arrays.asList(

            new Item(1, "brinquedo 1", "brinquedo 1"),
            new Item(2, "brinquedo 2", "brinquedo 2"),
            new Item(3, "brinquedo 3", "brinquedo 3")
    );

    @GetMapping("/itens")
    public List<Item> listarIten() {
        return itens;
    }

    @GetMapping("/item/{id}")
    public Item getItem(@PathVariable int id) {
        return itens.stream().filter(item -> item.getId() == id)
                .findFirst()
                .orElse(null);
    }

}
