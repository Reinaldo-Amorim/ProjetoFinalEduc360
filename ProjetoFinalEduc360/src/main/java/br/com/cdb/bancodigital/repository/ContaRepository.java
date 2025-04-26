package br.com.cdb.bancodigital.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.cdb.bancodigital.model.Cliente;
import br.com.cdb.bancodigital.model.Conta;

@Repository
public interface ContaRepository extends JpaRepository<Conta, Long> {

    Optional<Conta> findByNumeroConta(String numeroConta);

    List<Conta> findByCliente(Cliente cliente);
}
