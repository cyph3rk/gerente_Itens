package br.com.fiap.gerente_itens.controller;

import br.com.fiap.gerente_itens.controller.form.ItensForm;
import br.com.fiap.gerente_itens.dto.ItensDto;
import br.com.fiap.gerente_itens.facade.ItensFacade;
import br.com.fiap.gerente_itens.model.Itens;
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
    public ResponseEntity<Object> cadastraItens(@PathVariable Long id,
                                                 @RequestBody ItensForm itensForm) {

        logger.info("POST - Try : Cadastro de um novo Item: Nome: " + itensForm.getNome());

        Map<Path, String> violacoesToMap = validar(itensForm);
        if (!violacoesToMap.isEmpty()) {
            return ResponseEntity.badRequest().body(violacoesToMap);
        }

        ItensDto itensDTO = new ItensDto(itensForm.getNome(),
                itensForm.getValor(), itensForm.getQtd());
        Long resp = itensFacade.salvar(itensDTO);
        if ( resp == -1) {
            return ResponseEntity.badRequest().body("{\"Erro\": \"Item JÁ cadastrado.\"}");
        }

        logger.info("POST - Sucesso : Cadastro Item: Nome: " + itensDTO.getNome() + "Id: " + resp);
        return ResponseEntity.status(HttpStatus.CREATED).body("{\"Messagem\": \"Item CADASTRADO com sucesso.\", " +
                "\"id\": \"" + resp +"\"}");
    }

}
