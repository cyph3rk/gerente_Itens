package br.com.fiap.gerente_itens.facade;

import br.com.fiap.gerente_itens.dto.ItensDto;
import br.com.fiap.gerente_itens.model.Itens;
import br.com.fiap.gerente_itens.repositorio.IItensRepositorio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
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
        itens.setQtd(itensDto.getQtd());

        itensRepositorio.save(itens);

        return itens.getId();
    }

    private ItensDto converter (Itens itens) {
        ItensDto result = new ItensDto();
        result.setId(itens.getId());
        result.setNome(itens.getNome());
        result.setValor(itens.getValor());
        result.setQtd(itens.getQtd());

        return result;
    }

}
