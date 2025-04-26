package br.com.cdb.bancodigital.controller;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import br.com.cdb.bancodigital.model.Cartao;
import br.com.cdb.bancodigital.model.CartaoCredito;
import br.com.cdb.bancodigital.model.CartaoDebito;
import br.com.cdb.bancodigital.service.CartaoService;

@RestController
@RequestMapping("/api/cartoes")
public class CartaoController {

    @Autowired
    private CartaoService cartaoService;

    @GetMapping
    public ResponseEntity<List<Cartao>> listarTodos() {
        List<Cartao> cartoes = cartaoService.listarTodos();
        return ResponseEntity.ok(cartoes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cartao> buscarPorId(@PathVariable Long id) {
        try {
            Cartao cartao = cartaoService.buscarPorId(id);
            return ResponseEntity.ok(cartao);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/numero/{numeroCartao}")
    public ResponseEntity<Cartao> buscarPorNumeroCartao(@PathVariable String numeroCartao) {
        return cartaoService.buscarPorNumeroCartao(numeroCartao)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/conta/{contaId}")
    public ResponseEntity<List<Cartao>> listarCartoesPorConta(@PathVariable Long contaId) {
        try {
            List<Cartao> cartoes = cartaoService.listarCartoesPorConta(contaId);
            return ResponseEntity.ok(cartoes);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/credito/conta/{contaId}")
    public ResponseEntity<CartaoCredito> emitirCartaoCredito(@PathVariable Long contaId) {
        try {
            CartaoCredito cartaoCredito = cartaoService.emitirCartaoCredito(contaId);
            return ResponseEntity.status(HttpStatus.CREATED).body(cartaoCredito);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/debito/conta/{contaId}")
    public ResponseEntity<CartaoDebito> emitirCartaoDebito(@PathVariable Long contaId) {
        try {
            CartaoDebito cartaoDebito = cartaoService.emitirCartaoDebito(contaId);
            return ResponseEntity.status(HttpStatus.CREATED).body(cartaoDebito);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    static class AlterarSenhaRequest {
        private String novaSenha;

        public String getNovaSenha() {
            return novaSenha;
        }

        public void setNovaSenha(String novaSenha) {
            this.novaSenha = novaSenha;
        }
    }

    @PutMapping("/{id}/senha")
    public ResponseEntity<Void> alterarSenha(@PathVariable Long id, @RequestBody AlterarSenhaRequest request) {
        try {
            cartaoService.alterarSenha(id, request.getNovaSenha());
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    static class AlterarStatusRequest {
        private boolean ativo;

        public boolean isAtivo() {
            return ativo;
        }

        public void setAtivo(boolean ativo) {
            this.ativo = ativo;
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Void> alterarStatus(@PathVariable Long id, @RequestBody AlterarStatusRequest request) {
        try {
            cartaoService.alterarStatus(id, request.isAtivo());
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    static class PagamentoRequest {
        private BigDecimal valor;

        public BigDecimal getValor() {
            return valor;
        }

        public void setValor(BigDecimal valor) {
            this.valor = valor;
        }
    }

    @PostMapping("/{id}/pagamento")
    public ResponseEntity<?> realizarPagamento(@PathVariable Long id, @RequestBody PagamentoRequest request) {
        try {
            boolean sucesso = cartaoService.realizarPagamento(id, request.getValor());

            if (sucesso) {
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.badRequest()
                        .body("Não foi possível realizar o pagamento. Verifique o limite disponível.");
            }
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/debito/{id}/limite-diario")
    public ResponseEntity<?> alterarLimiteDiarioDebito(@PathVariable Long id, @RequestBody BigDecimal novoLimite) {
        try {
            cartaoService.alterarLimiteDiarioDebito(id, novoLimite);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/aplicar-taxa")
    public ResponseEntity<Void> aplicarTaxaTodasCartoes() {
        cartaoService.aplicarTaxaTodasCartoes();
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/limite")
    public ResponseEntity<?> alterarLimiteCredito(@PathVariable Long id, @RequestBody BigDecimal novoLimite) {
        try {
            cartaoService.alterarLimiteCredito(id, novoLimite);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}/fatura")
    public ResponseEntity<?> consultarFatura(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(cartaoService.consultarFatura(id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{id}/fatura/pagamento")
    public ResponseEntity<?> pagarFatura(@PathVariable Long id, @RequestBody BigDecimal valor) {
        try {
            cartaoService.pagarFatura(id, valor);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}