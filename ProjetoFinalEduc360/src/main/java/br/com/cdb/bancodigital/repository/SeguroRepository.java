package br.com.cdb.bancodigital.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.cdb.bancodigital.model.CartaoCredito;
import br.com.cdb.bancodigital.model.Seguro;

@Repository
public interface SeguroRepository extends JpaRepository<Seguro, Long> {

    Optional<Seguro> findByNumeroApolice(String numeroApolice);

    List<Seguro> findByCartaoCredito(CartaoCredito cartaoCredito);
}
