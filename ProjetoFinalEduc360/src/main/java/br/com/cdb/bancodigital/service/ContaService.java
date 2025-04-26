package br.com.cdb.bancodigital.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import br.com.cdb.bancodigital.model.Cliente;
import br.com.cdb.bancodigital.model.Conta;
import br.com.cdb.bancodigital.model.ContaCorrente;
import br.com.cdb.bancodigital.model.ContaPoupanca;
import br.com.cdb.bancodigital.repository.ContaRepository;

@Service
public class ContaService {

    @Autowired
    private ContaRepository contaRepository;

    @Autowired
    private ClienteService clienteService;

    public List<Conta> listarTodas() {
        return contaRepository.findAll();
    }

    public Conta buscarPorId(Long id) {
        return contaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Conta não encontrada"));
    }

    public Optional<Conta> buscarPorNumeroConta(String numeroConta) {
        return contaRepository.findByNumeroConta(numeroConta);
    }

    public List<Conta> listarContasPorCliente(Long clienteId) {
        Cliente cliente = clienteService.buscarPorId(clienteId);
        return contaRepository.findByCliente(cliente);
    }

    @Transactional
    public ContaCorrente criarContaCorrente(Long clienteId) {
        Cliente cliente = clienteService.buscarPorId(clienteId);

        boolean possuiContaCorrente = cliente.getContas().stream()
                .anyMatch(conta -> conta instanceof ContaCorrente && conta.isAtiva());

        if (possuiContaCorrente) {
            throw new RuntimeException("Cliente já possui uma conta corrente ativa");
        }

        ContaCorrente contaCorrente = new ContaCorrente();
        contaCorrente.setNumeroConta(gerarNumeroConta());
        contaCorrente.setCliente(cliente);
        contaCorrente.setSaldo(BigDecimal.ZERO);
        contaCorrente.setDataAbertura(LocalDate.now());
        contaCorrente.setAtiva(true);

        return contaRepository.save(contaCorrente);
    }

    @Transactional
    public ContaPoupanca criarContaPoupanca(Long clienteId) {
        Cliente cliente = clienteService.buscarPorId(clienteId);

        boolean possuiContaPoupanca = cliente.getContas().stream()
                .anyMatch(conta -> conta instanceof ContaPoupanca && conta.isAtiva());

        if (possuiContaPoupanca) {
            throw new RuntimeException("Cliente já possui uma conta poupança ativa");
        }

        ContaPoupanca contaPoupanca = new ContaPoupanca();
        contaPoupanca.setNumeroConta(gerarNumeroConta());
        contaPoupanca.setCliente(cliente);
        contaPoupanca.setSaldo(BigDecimal.ZERO);
        contaPoupanca.setDataAbertura(LocalDate.now());
        contaPoupanca.setAtiva(true);

        return contaRepository.save(contaPoupanca);
    }

    @Transactional
    public <T extends Conta> T salvarConta(T conta) {
        return contaRepository.save(conta);
    }

    @Transactional
    public void fecharConta(Long id) {
        Conta conta = buscarPorId(id);

        if (conta.getSaldo().compareTo(BigDecimal.ZERO) > 0) {
            throw new RuntimeException("Conta com saldo positivo não pode ser fechada. Realize um saque primeiro.");
        }

        conta.setAtiva(false);
        contaRepository.save(conta);
    }

    @Transactional
    public BigDecimal consultarSaldo(Long id) {
        Conta conta = buscarPorId(id);
        return conta.exibirSaldo();
    }

    @Transactional
    public void depositar(Long id, BigDecimal valor) {
        if (valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Valor de depósito deve ser maior que zero");
        }

        Conta conta = buscarPorId(id);

        if (!conta.isAtiva()) {
            throw new RuntimeException("Conta inativa não pode receber depósitos");
        }

        conta.setSaldo(conta.getSaldo().add(valor));
        contaRepository.save(conta);
    }

    @Transactional
    public void sacar(Long id, BigDecimal valor) {
        if (valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Valor de saque deve ser maior que zero");
        }

        Conta conta = buscarPorId(id);

        if (!conta.isAtiva()) {
            throw new RuntimeException("Conta inativa não pode realizar saques");
        }

        if (conta.getSaldo().compareTo(valor) < 0) {
            throw new RuntimeException("Saldo insuficiente para saque");
        }

        conta.setSaldo(conta.getSaldo().subtract(valor));
        contaRepository.save(conta);
    }

    @Transactional
    public void transferirPix(Long contaOrigemId, String numeroContaDestino, BigDecimal valor) {
        if (valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Valor de transferência deve ser maior que zero");
        }

        Conta contaOrigem = buscarPorId(contaOrigemId);
        Conta contaDestino = buscarPorNumeroConta(numeroContaDestino)
                .orElseThrow(() -> new RuntimeException("Conta destino não encontrada"));

        if (!contaOrigem.isAtiva()) {
            throw new RuntimeException("Conta origem inativa não pode realizar transferências");
        }

        if (!contaDestino.isAtiva()) {
            throw new RuntimeException("Conta destino inativa não pode receber transferências");
        }

        if (contaOrigem.getSaldo().compareTo(valor) < 0) {
            throw new RuntimeException("Saldo insuficiente para transferência");
        }

        contaOrigem.setSaldo(contaOrigem.getSaldo().subtract(valor));
        contaDestino.setSaldo(contaDestino.getSaldo().add(valor));

        contaRepository.save(contaOrigem);
        contaRepository.save(contaDestino);
    }

    @Transactional
    public void aplicarTaxaMensalTodasContas() {
        List<Conta> contas = listarTodas();

        for (Conta conta : contas) {
            if (conta.isAtiva()) {
                conta.aplicarTaxaMensal();
                contaRepository.save(conta);
            }
        }
    }

    @Transactional
    public void transferir(Long contaOrigemId, Long contaDestinoId, BigDecimal valor) {
        if (valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Valor de transferência deve ser maior que zero");
        }

        Conta contaOrigem = buscarPorId(contaOrigemId);
        Conta contaDestino = buscarPorId(contaDestinoId);

        if (!contaOrigem.isAtiva()) {
            throw new RuntimeException("Conta origem inativa não pode realizar transferências");
        }

        if (!contaDestino.isAtiva()) {
            throw new RuntimeException("Conta destino inativa não pode receber transferências");
        }

        if (contaOrigem.getSaldo().compareTo(valor) < 0) {
            throw new RuntimeException("Saldo insuficiente para transferência");
        }

        contaOrigem.setSaldo(contaOrigem.getSaldo().subtract(valor));
        contaDestino.setSaldo(contaDestino.getSaldo().add(valor));

        contaRepository.save(contaOrigem);
        contaRepository.save(contaDestino);
    }

    @Transactional
    public void aplicarTaxaManutencaoIndividual(Long id) {
        Conta conta = buscarPorId(id);

        if (!conta.isAtiva()) {
            throw new RuntimeException("Não é possível aplicar taxa em uma conta inativa");
        }

        if (!(conta instanceof ContaCorrente)) {
            throw new RuntimeException("Taxa de manutenção aplicável apenas a contas correntes");
        }

        conta.aplicarTaxaMensal();
        contaRepository.save(conta);
    }

    @Transactional
    public void aplicarRendimentoIndividual(Long id) {
        Conta conta = buscarPorId(id);

        if (!conta.isAtiva()) {
            throw new RuntimeException("Não é possível aplicar rendimentos em uma conta inativa");
        }

        if (!(conta instanceof ContaPoupanca)) {
            throw new RuntimeException("Rendimentos aplicáveis apenas a contas poupança");
        }

        ContaPoupanca contaPoupanca = (ContaPoupanca) conta;
        contaPoupanca.aplicarRendimento();
        contaRepository.save(contaPoupanca);
    }

    private String gerarNumeroConta() {
        Random random = new Random();
        StringBuilder numeroConta = new StringBuilder();

        for (int i = 0; i < 8; i++) {
            numeroConta.append(random.nextInt(10));
        }

        while (buscarPorNumeroConta(numeroConta.toString()).isPresent()) {
            numeroConta = new StringBuilder();
            for (int i = 0; i < 8; i++) {
                numeroConta.append(random.nextInt(10));
            }
        }

        return numeroConta.toString();
    }
}