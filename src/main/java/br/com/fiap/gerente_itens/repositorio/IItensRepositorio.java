package br.com.fiap.gerente_itens.repositorio;

import br.com.fiap.gerente_itens.model.Itens;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IItensRepositorio extends JpaRepository<Itens, Long> {

    List<Itens> findByNome(String nome);

}
