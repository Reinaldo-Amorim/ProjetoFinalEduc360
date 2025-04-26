package br.com.cdb.bancodigital.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import br.com.cdb.bancodigital.model.Cartao;
import br.com.cdb.bancodigital.model.CartaoCredito;
import br.com.cdb.bancodigital.model.CartaoDebito;
import br.com.cdb.bancodigital.model.Conta;
import br.com.cdb.bancodigital.model.SeguroFraude;
import br.com.cdb.bancodigital.repository.CartaoRepository;

@Service
public class CartaoService {

    @Autowired
    private CartaoRepository cartaoRepository;

    @Autowired
    private ContaService contaService;

    public List<Cartao> listarTodos() {
        return cartaoRepository.findAll();
    }

    public Cartao buscarPorId(Long id) {
        return cartaoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cartão não encontrado"));
    }

    public Optional<Cartao> buscarPorNumeroCartao(String numeroCartao) {
        return cartaoRepository.findByNumeroCartao(numeroCartao);
    }

    public List<Cartao> listarCartoesPorConta(Long contaId) {
        Conta conta = contaService.buscarPorId(contaId);
        return cartaoRepository.findByConta(conta);
    }

    @Transactional
    public CartaoCredito emitirCartaoCredito(Long contaId) {
        Conta conta = contaService.buscarPorId(contaId);

        if (!conta.isAtiva()) {
            throw new RuntimeException("Não é possível emitir cartão para uma conta inativa ou fechada.");
        }

        CartaoCredito cartaoCredito = new CartaoCredito(conta);
        cartaoCredito.setNumeroCartao(gerarNumeroCartao());
        cartaoCredito.setNomeTitular(conta.getCliente().getNome());
        cartaoCredito.setDataValidade(LocalDate.now().plusYears(5));
        cartaoCredito.setSenha(gerarSenhaAleatoria());

        cartaoCredito.setCvv(gerarCVV());
        cartaoCredito.setValidade(LocalDate.now().plusYears(5));

        CartaoCredito cartaoPersistido = cartaoRepository.save(cartaoCredito);

        try {
            SeguroFraude seguroFraude = new SeguroFraude(cartaoPersistido);
            cartaoPersistido.setSeguroFraude(seguroFraude);
            seguroFraude.setCartaoCredito(cartaoPersistido);

            return cartaoRepository.save(cartaoPersistido);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao configurar seguro fraude: " + e.getMessage(), e);
        }
    }

    @Transactional
    public CartaoDebito emitirCartaoDebito(Long contaId) {
        Conta conta = contaService.buscarPorId(contaId);

        if (!conta.isAtiva()) {
            throw new RuntimeException("Não é possível emitir cartão para uma conta inativa ou fechada.");
        }

        CartaoDebito cartaoDebito = new CartaoDebito();
        cartaoDebito.setNumeroCartao(gerarNumeroCartao());
        cartaoDebito.setNomeTitular(conta.getCliente().getNome());
        cartaoDebito.setDataValidade(LocalDate.now().plusYears(5));
        cartaoDebito.setSenha(gerarSenhaAleatoria());
        cartaoDebito.setConta(conta);

        return cartaoRepository.save(cartaoDebito);
    }

    @Transactional
    public void alterarSenha(Long id, String novaSenha) {
        Cartao cartao = buscarPorId(id);
        cartao.alterarSenha(novaSenha);
        cartaoRepository.save(cartao);
    }

    @Transactional
    public void alterarStatus(Long id, boolean ativo) {
        Cartao cartao = buscarPorId(id);
        cartao.alterarStatus(ativo);
        cartaoRepository.save(cartao);
    }

    @Transactional
    public boolean realizarPagamento(Long id, BigDecimal valor) {
        if (valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Valor de pagamento deve ser maior que zero");
        }

        Cartao cartao = buscarPorId(id);
        boolean sucesso = cartao.realizarPagamento(valor);

        if (sucesso) {
            cartaoRepository.save(cartao);
        }

        return sucesso;
    }

    @Transactional
    public void alterarLimiteDiarioDebito(Long id, BigDecimal novoLimite) {
        Cartao cartao = buscarPorId(id);

        if (!(cartao instanceof CartaoDebito)) {
            throw new RuntimeException("Operação permitida apenas para cartões de débito");
        }

        CartaoDebito cartaoDebito = (CartaoDebito) cartao;
        cartaoDebito.alterarLimiteDiario(novoLimite);
        cartaoRepository.save(cartaoDebito);
    }

    @Transactional
    public void aplicarTaxaTodasCartoes() {
        List<Cartao> cartoes = listarTodos();

        for (Cartao cartao : cartoes) {
            cartao.aplicarTaxa();
            cartaoRepository.save(cartao);
        }
    }

    @Transactional
    public void desbloquearCartao(Long id) {
        Cartao cartao = buscarPorId(id);
        cartao.desbloquearCartao();
        cartaoRepository.save(cartao);
    }

    @Transactional
    public void bloquearCartao(Long id) {
        Cartao cartao = buscarPorId(id);
        cartao.bloquearCartao();
        cartaoRepository.save(cartao);
    }

    @Transactional
    public void alterarLimiteCredito(Long id, BigDecimal novoLimite) {
        Cartao cartao = buscarPorId(id);

        if (!(cartao instanceof CartaoCredito)) {
            throw new RuntimeException("Operação permitida apenas para cartões de crédito");
        }

        CartaoCredito cartaoCredito = (CartaoCredito) cartao;

        if (novoLimite.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("O limite deve ser maior que zero");
        }

        cartaoCredito.setLimite(novoLimite);
        cartaoRepository.save(cartaoCredito);
    }

    @Transactional
    public Map<String, Object> consultarFatura(Long id) {
        Cartao cartao = buscarPorId(id);

        if (!(cartao instanceof CartaoCredito)) {
            throw new RuntimeException("Operação permitida apenas para cartões de crédito");
        }

        CartaoCredito cartaoCredito = (CartaoCredito) cartao;

        Map<String, Object> fatura = new HashMap<>();
        fatura.put("numero_cartao", cartaoCredito.getNumeroCartao());
        fatura.put("nome_titular", cartaoCredito.getNomeTitular());
        fatura.put("vencimento", LocalDate.now().plusDays(7));
        fatura.put("valor_total", cartaoCredito.getFaturaAtual());
        fatura.put("limite_disponivel", cartaoCredito.getLimiteDisponivel());
        fatura.put("limite_total", cartaoCredito.getLimite());

        return fatura;
    }

    @Transactional
    public void pagarFatura(Long id, BigDecimal valor) {
        Cartao cartao = buscarPorId(id);

        if (!(cartao instanceof CartaoCredito)) {
            throw new RuntimeException("Operação permitida apenas para cartões de crédito");
        }

        CartaoCredito cartaoCredito = (CartaoCredito) cartao;

        if (valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("O valor do pagamento deve ser maior que zero");
        }

        if (valor.compareTo(cartaoCredito.getFaturaAtual()) > 0) {
            throw new RuntimeException("O valor do pagamento não pode ser maior que o valor da fatura");
        }

        BigDecimal novaFatura = cartaoCredito.getFaturaAtual().subtract(valor);
        cartaoCredito.setFaturaAtual(novaFatura);

        cartaoCredito.setLimiteDisponivel(
                cartaoCredito.getLimite().subtract(cartaoCredito.getFaturaAtual()));

        cartaoRepository.save(cartaoCredito);
    }

    private String gerarNumeroCartao() {
        Random random = new Random();
        StringBuilder numeroCartao = new StringBuilder();

        for (int i = 0; i < 16; i++) {
            numeroCartao.append(random.nextInt(10));

            if ((i + 1) % 4 == 0 && i < 15) {
                numeroCartao.append(" ");
            }
        }

        while (buscarPorNumeroCartao(numeroCartao.toString()).isPresent()) {
            numeroCartao = new StringBuilder();
            for (int i = 0; i < 16; i++) {
                numeroCartao.append(random.nextInt(10));

                if ((i + 1) % 4 == 0 && i < 15) {
                    numeroCartao.append(" ");
                }
            }
        }

        return numeroCartao.toString();
    }

    private String gerarSenhaAleatoria() {
        Random random = new Random();
        StringBuilder senha = new StringBuilder();

        for (int i = 0; i < 4; i++) {
            senha.append(random.nextInt(10));
        }

        return senha.toString();
    }

    private String gerarCVV() {
        Random random = new Random();
        StringBuilder cvv = new StringBuilder();

        for (int i = 0; i < 3; i++) {
            cvv.append(random.nextInt(10));
        }

        return cvv.toString();
    }
}
