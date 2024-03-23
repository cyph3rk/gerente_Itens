package br.com.fiap.gerente_itens.controller;

import br.com.fiap.gerente_itens.controller.form.ItensForm;
import br.com.fiap.gerente_itens.dto.ItensDto;
import br.com.fiap.gerente_itens.facade.ItensFacade;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Path;
import jakarta.validation.Validator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/itens")
public class ItenController {

    private static final String ERRO = "Item NÃO encontrado.";
    private static final String SUCESSO = "Item CADASTRADO com sucesso.";
    private static final String DELETE_SUCESSO = "Item DELETADO com sucesso";
    private static final String ALTERADO_SUCESSO = "Item ALTERADO com sucesso.";
    private static final String JA_CADASTRADO = "Item JÁ cadastrado.";
    private static final String ERRO_INESPERADO = "Erro inesperado.";

    private final Validator validator;

    private final ItensFacade itensFacade;

    public ItenController(Validator validator, ItensFacade itensFacade) {
        this.validator = validator;
        this.itensFacade = itensFacade;
    }

    private <T> Map<Path, String> validar(T form) {
        Set<ConstraintViolation<T>> violacoes = validator.validate(form);

        return violacoes.stream().collect(Collectors.toMap(
                ConstraintViolation::getPropertyPath, ConstraintViolation::getMessage));
    }

    @PostMapping
    public ResponseEntity<Object> cadastraItens(@RequestBody ItensForm itensForm) {

        Map<Path, String> violacoesToMap = validar(itensForm);
        if (!violacoesToMap.isEmpty()) {
            return ResponseEntity.badRequest().body(violacoesToMap);
        }

        ItensDto itensDTO = new ItensDto(itensForm.getNome(),
                itensForm.getValor(), itensForm.getEstoque());
        Long resp = itensFacade.salvar(itensDTO);
        if ( resp == -1) {
            return ResponseEntity.badRequest().body(JA_CADASTRADO);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body("{\"Messagem\": \"" + SUCESSO + "\", " +
                "\"id\": \"" + resp +"\"}");
    }


    @GetMapping
    public ResponseEntity<String> getAllItens() {
        String json = "Erro Inesperado";
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            json = objectMapper.writeValueAsString(itensFacade.getAll());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return ResponseEntity.ok(String.format(json));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getItensPorId(@PathVariable Long id) {

        Optional<ItensDto> itensDto = itensFacade.buscarPorId(id);

        boolean existeRegistro = itensDto.isPresent();
        if (!existeRegistro) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ERRO);
        }

        return ResponseEntity.status(HttpStatus.OK).body(itensDto);
    }

    @GetMapping("/nome/{nome}")
    public ResponseEntity<Object> getItensPorNome(@PathVariable String nome) {

        List<ItensDto> itensDto = itensFacade.buscarPorNome(nome);

        if (itensDto.isEmpty()) {
            return ResponseEntity.badRequest().body(ERRO);
        }

        return ResponseEntity.status(HttpStatus.OK).body(itensDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteItensPorId(@PathVariable Long id) {

        Optional<ItensDto> itensDto = itensFacade.buscarPorId(id);

        boolean existeRegistro = itensDto.isPresent();
        if (!existeRegistro) {
            return ResponseEntity.badRequest().body(ERRO);
        }

        itensFacade.remove(id);
        return ResponseEntity.ok(DELETE_SUCESSO);
    }

    @PutMapping("/{id}/{qtd}")
    public ResponseEntity<Object> addStoqueItensPorId(@PathVariable Long id, @PathVariable String qtd) {

        Optional<ItensDto> itensDtoold = itensFacade.buscarPorId(id);
        boolean existeRegistro = itensDtoold.isPresent();
        if (!existeRegistro) {
            return ResponseEntity.badRequest().body(ERRO);
        }

        ItensDto itensDtonew = new ItensDto();
        itensDtonew.setId(id);
        itensDtonew.setNome(itensDtoold.get().getNome());
        itensDtonew.setValor(itensDtoold.get().getValor());
        itensDtonew.setEstoque(itensDtoold.get().getEstoque());

        Long resp = itensFacade.altera(itensDtonew, qtd);
        if ( resp == -1) {
            return ResponseEntity.badRequest().body(ERRO_INESPERADO);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(ALTERADO_SUCESSO);
    }

}
