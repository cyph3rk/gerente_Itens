package br.com.fiap.gerente_itens.facade;

import br.com.fiap.gerente_itens.dto.ItensDto;
import br.com.fiap.gerente_itens.model.Itens;
import br.com.fiap.gerente_itens.repositorio.IItensRepositorio;

import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ItensFacade {

    private static final Logger logger = LoggerFactory.getLogger(ItensFacade.class);

    private final IItensRepositorio itensRepositorio;

    @Autowired
    public ItensFacade(IItensRepositorio itensRepositorio) {
        this.itensRepositorio = itensRepositorio;
    }

    public List<ItensDto> buscarPorNome(String nome) {
        List<Itens> listaItens = itensRepositorio.findByNome(nome);

        return listaItens.stream()
                .map(this::converter).collect(Collectors.toList());
    }

    public Long salvar(ItensDto itensDto) {

        List<ItensDto> encontrado = buscarPorNome(itensDto.getNome());
        if (encontrado.size() >= 1) {
            return -1L;
        }

        Itens itens = new Itens();
        itens.setNome(itensDto.getNome());
        itens.setValor(itensDto.getValor());
        itens.setEstoque(itensDto.getEstoque());

        itensRepositorio.save(itens);

        return itens.getId();
    }

    private ItensDto converter (Itens itens) {
        ItensDto result = new ItensDto();
        result.setId(itens.getId());
        result.setNome(itens.getNome());
        result.setValor(itens.getValor());
        result.setEstoque(itens.getEstoque());

        return result;
    }

    public Object getAll() {
        return itensRepositorio
                .findAll()
                .stream()
                .map(this::converter).collect(Collectors.toList());
    }

    public Optional<ItensDto> buscarPorId(Long id) {
        try {
            Itens itens = itensRepositorio.getReferenceById(id);

            ItensDto itensDto = new ItensDto();
            itensDto.setId(itens.getId());
            itensDto.setNome(itens.getNome());
            itensDto.setValor(itens.getValor());
            itensDto.setEstoque(itens.getEstoque());

            return Optional.of(itensDto);
        } catch (EntityNotFoundException ex) {
            logger.info("ItensFacade - buscarPorId Id: " + id + (" Não cadastrado"));
            return Optional.empty();
            //todo: fazer o lancamento exception 35:00
        }
    }

    public void remove(Long id) {
        //Todo: Implementar a verificação se cadastro existe antes de deletar
        itensRepositorio.deleteById(id);
    }

    public Long altera(ItensDto itensDto, String qtd) {

        Itens itens = itensRepositorio.getReferenceById(itensDto.getId());
        itens.setId(itensDto.getId());
        itens.setNome(itensDto.getNome());
        itens.setValor(itensDto.getValor());

        long qtdAtual = Long.parseLong(itensDto.getEstoque());
        long qtdSomar = Long.parseLong(qtd);
        long total = qtdAtual + qtdSomar;
        String qtdTotal = Long.toString(total);

        itens.setEstoque(qtdTotal);

        itensRepositorio.save(itens);

        return itens.getId();

    }
}
