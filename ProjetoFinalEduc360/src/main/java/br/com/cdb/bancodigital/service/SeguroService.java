package br.com.cdb.bancodigital.service;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import br.com.cdb.bancodigital.model.Cartao;
import br.com.cdb.bancodigital.model.CartaoCredito;
import br.com.cdb.bancodigital.model.Cliente;
import br.com.cdb.bancodigital.model.Seguro;
import br.com.cdb.bancodigital.model.SeguroFraude;
import br.com.cdb.bancodigital.model.SeguroViagem;
import br.com.cdb.bancodigital.model.SeguroVida;
import br.com.cdb.bancodigital.repository.SeguroRepository;

@Service
public class SeguroService {

    @Autowired
    private SeguroRepository seguroRepository;

    @Autowired
    private CartaoService cartaoService;

    @Autowired
    private ClienteService clienteService;

    public List<Seguro> listarTodos() {
        return seguroRepository.findAll();
    }

    public Seguro buscarPorId(Long id) {
        return seguroRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Seguro não encontrado"));
    }

    public Optional<Seguro> buscarPorNumeroApolice(String numeroApolice) {
        return seguroRepository.findByNumeroApolice(numeroApolice);
    }

    public List<Seguro> listarSegurosPorCartaoCredito(Long cartaoCreditoId) {
        Cartao cartao = cartaoService.buscarPorId(cartaoCreditoId);

        if (!(cartao instanceof CartaoCredito)) {
            throw new RuntimeException("Operação permitida apenas para cartões de crédito");
        }

        CartaoCredito cartaoCredito = (CartaoCredito) cartao;
        return seguroRepository.findByCartaoCredito(cartaoCredito);
    }

    @Transactional
    public SeguroViagem contratarSeguroViagem(Long cartaoCreditoId) {
        Cartao cartao = cartaoService.buscarPorId(cartaoCreditoId);

        if (!(cartao instanceof CartaoCredito)) {
            throw new RuntimeException("Operação permitida apenas para cartões de crédito");
        }

        CartaoCredito cartaoCredito = (CartaoCredito) cartao;

        boolean possuiSeguroViagem = seguroRepository.findByCartaoCredito(cartaoCredito).stream()
                .anyMatch(seguro -> seguro instanceof SeguroViagem);

        if (possuiSeguroViagem) {
            throw new RuntimeException("Cliente já possui um seguro viagem ativo");
        }

        Cliente cliente = cartaoCredito.getConta().getCliente();
        SeguroViagem seguroViagem = new SeguroViagem(cartaoCredito);

        return seguroRepository.save(seguroViagem);
    }

    @Transactional
    public SeguroFraude contratarSeguroFraude(Long cartaoCreditoId) {
        Cartao cartao = cartaoService.buscarPorId(cartaoCreditoId);

        if (!(cartao instanceof CartaoCredito)) {
            throw new RuntimeException("Operação permitida apenas para cartões de crédito");
        }

        CartaoCredito cartaoCredito = (CartaoCredito) cartao;

        boolean possuiSeguroFraude = seguroRepository.findByCartaoCredito(cartaoCredito).stream()
                .anyMatch(seguro -> seguro instanceof SeguroFraude);

        if (possuiSeguroFraude) {
            throw new RuntimeException("Este cartão já possui um seguro fraude ativo");
        }

        SeguroFraude seguroFraude = new SeguroFraude(cartaoCredito);

        return seguroRepository.save(seguroFraude);
    }

    @Transactional
    public SeguroVida contratarSeguroVida(Long clienteId) {
        Cliente cliente = clienteService.buscarPorId(clienteId);

        boolean possuiSeguroVida = seguroRepository.findAll().stream()
                .filter(seguro -> seguro instanceof SeguroVida)
                .map(seguro -> (SeguroVida) seguro)
                .anyMatch(seguro -> seguro.getCliente().getId().equals(clienteId) && seguro.isAtivo());

        if (possuiSeguroVida) {
            throw new RuntimeException("Cliente já possui um seguro de vida ativo");
        }

        SeguroVida seguroVida = new SeguroVida(cliente);

        return seguroRepository.save(seguroVida);
    }

    @Transactional
    public void cancelarSeguro(Long id) {
        Seguro seguro = buscarPorId(id);

        if (seguro.getDataInicio().isBefore(java.time.LocalDate.now())) {
            throw new RuntimeException("Não é possível cancelar um seguro que já iniciou");
        }

        seguroRepository.delete(seguro);
    }
}