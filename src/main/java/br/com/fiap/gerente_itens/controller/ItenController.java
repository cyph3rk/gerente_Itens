package br.com.fiap.gerente_itens.controller;

import br.com.fiap.gerente_itens.controller.form.ItensForm;
import br.com.fiap.gerente_itens.dto.ItensDto;
import br.com.fiap.gerente_itens.facade.ItensFacade;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Path;
import jakarta.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/itens")
public class ItenController {

    private static final Logger logger = LoggerFactory.getLogger(ItenController.class);

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

        logger.info("POST - Try : Cadastro de um novo Item: Nome: " + itensForm.getNome());

        Map<Path, String> violacoesToMap = validar(itensForm);
        if (!violacoesToMap.isEmpty()) {
            return ResponseEntity.badRequest().body(violacoesToMap);
        }

        ItensDto itensDTO = new ItensDto(itensForm.getNome(),
                itensForm.getValor(), itensForm.getEstoque());
        Long resp = itensFacade.salvar(itensDTO);
        if ( resp == -1) {
            return ResponseEntity.badRequest().body("{\"Erro\": \"Item JÁ cadastrado.\"}");
        }

        logger.info("POST - Sucesso : Cadastro Item: Nome: " + itensDTO.getNome() + "Id: " + resp);
        return ResponseEntity.status(HttpStatus.CREATED).body("{\"Messagem\": \"Item CADASTRADO com sucesso.\", " +
                "\"id\": \"" + resp +"\"}");
    }


    @GetMapping
    public ResponseEntity<String> getAllItens() {
        logger.info("GET - Pedido de todos os Itens cadastrados");

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
        logger.info("GET - Pedido de Itens por Id: " + id);

        Optional<ItensDto> itensDto = itensFacade.buscarPorId(id);

        boolean existeRegistro = itensDto.isPresent();
        if (!existeRegistro) {
            return ResponseEntity.badRequest().body("{\"Erro\": \"Item NÃO cadastrado.\"}");
        }

        return ResponseEntity.status(HttpStatus.OK).body(itensDto);
    }

    @GetMapping("/nome/{nome}")
    public ResponseEntity<Object> getItensPorNome(@PathVariable String nome) {

        List<ItensDto> itensDto = itensFacade.buscarPorNome(nome);

        if (itensDto.size() == 0) {
            return ResponseEntity.badRequest().body("{\"Erro\": \"Item NÃO cadastrado.\"}");
        }

        return ResponseEntity.status(HttpStatus.OK).body(itensDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteItensPorId(@PathVariable Long id) {

        Optional<ItensDto> itensDto = itensFacade.buscarPorId(id);

        boolean existeRegistro = itensDto.isPresent();
        if (!existeRegistro) {
            return ResponseEntity.badRequest().body("{\"Erro\": \"Item NÃO cadastrado.\"}");
        }

        itensFacade.remove(id);
        return ResponseEntity.ok("{\"Mensagem\": \"Item DELETADO com sucesso.\"}");
    }

    @PutMapping("/{id}/{qtd}")
    public ResponseEntity<Object> addStoqueItensPorId(@PathVariable Long id, @PathVariable String qtd) {

        Optional<ItensDto> itensDto_old = itensFacade.buscarPorId(id);
        boolean existeRegistro = itensDto_old.isPresent();
        if (!existeRegistro) {
            return ResponseEntity.badRequest().body("{\"Erro\": \"Item NÃO cadastrado.\"}");
        }

        ItensDto itensDto_new = new ItensDto();
        itensDto_new.setId(id);
        itensDto_new.setNome(itensDto_old.get().getNome());
        itensDto_new.setValor(itensDto_old.get().getValor());
        itensDto_new.setEstoque(itensDto_old.get().getEstoque());

        Long resp = itensFacade.altera(itensDto_new, qtd);
        if ( resp == -1) {
            return ResponseEntity.badRequest().body("{\"Erro\": \"Erro inesperado.\"}");
        }

        logger.info("POST - Sucesso : Add Stoque Pessoa: Nome: " + itensDto_new.getNome() + "Id: " + resp);
        return ResponseEntity.status(HttpStatus.CREATED).body("{\"Messagem\": \"Add Item ALTERADO com sucesso.\", " +
                "\"id\": \"" + resp +"\"}");
    }

}
