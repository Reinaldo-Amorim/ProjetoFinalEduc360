package br.com.cdb.bancodigital.controller;

import br.com.cdb.bancodigital.dto.ContaDTO;
import br.com.cdb.bancodigital.model.Cliente;
import br.com.cdb.bancodigital.model.Conta;
import br.com.cdb.bancodigital.model.ContaCorrente;
import br.com.cdb.bancodigital.model.ContaPoupanca;
import br.com.cdb.bancodigital.service.ClienteService;
import br.com.cdb.bancodigital.service.ContaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/contas")
public class ContaController {

    @Autowired
    private ContaService contaService;

    @Autowired
    private ClienteService clienteService;

    @GetMapping
    public ResponseEntity<List<ContaDTO>> listarTodas() {
        List<Conta> contas = contaService.listarTodas();
        List<ContaDTO> contasDTO = contas.stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(contasDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContaDTO> buscarPorId(@PathVariable Long id) {
        try {
            Conta conta = contaService.buscarPorId(id);
            return ResponseEntity.ok(converterParaDTO(conta));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<ContaDTO>> listarContasPorCliente(@PathVariable Long clienteId) {
        try {
            List<Conta> contas = contaService.listarContasPorCliente(clienteId);
            List<ContaDTO> contasDTO = contas.stream()
                    .map(this::converterParaDTO)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(contasDTO);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/corrente")
    public ResponseEntity<?> criarContaCorrente(@Valid @RequestBody ContaDTO contaDTO) {
        try {
            Cliente cliente = clienteService.buscarPorId(contaDTO.getClienteId());
            ContaCorrente contaCorrente = contaService.criarContaCorrente(cliente.getId());

            if (contaDTO.getDataAbertura() != null) {
                contaCorrente.setDataAbertura(contaDTO.getDataAbertura());
            } else {
                contaCorrente.setDataAbertura(LocalDate.now());
            }

            contaCorrente.setAtiva(contaDTO.isAtiva());

            if (contaDTO.getSaldo() != null && contaDTO.getSaldo().compareTo(BigDecimal.ZERO) > 0) {
                contaCorrente.setSaldo(contaDTO.getSaldo());
            }

            ContaCorrente contaSalva = contaService.salvarConta(contaCorrente);
            return ResponseEntity.status(HttpStatus.CREATED).body(converterParaDTO(contaSalva));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/poupanca")
    public ResponseEntity<?> criarContaPoupanca(@Valid @RequestBody ContaDTO contaDTO) {
        try {
            Cliente cliente = clienteService.buscarPorId(contaDTO.getClienteId());
            ContaPoupanca contaPoupanca = contaService.criarContaPoupanca(cliente.getId());

            if (contaDTO.getDataAbertura() != null) {
                contaPoupanca.setDataAbertura(contaDTO.getDataAbertura());
            } else {
                contaPoupanca.setDataAbertura(LocalDate.now());
            }

            contaPoupanca.setAtiva(contaDTO.isAtiva());

            if (contaDTO.getSaldo() != null && contaDTO.getSaldo().compareTo(BigDecimal.ZERO) > 0) {
                contaPoupanca.setSaldo(contaDTO.getSaldo());
            }

            ContaPoupanca contaSalva = contaService.salvarConta(contaPoupanca);
            return ResponseEntity.status(HttpStatus.CREATED).body(converterParaDTO(contaSalva));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> fecharConta(@PathVariable Long id) {
        try {
            contaService.fecharConta(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}/saldo")
    public ResponseEntity<?> consultarSaldo(@PathVariable Long id) {
        try {
            BigDecimal saldo = contaService.consultarSaldo(id);
            return ResponseEntity.ok().body(saldo);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}/deposito")
    public ResponseEntity<?> depositar(@PathVariable Long id, @RequestBody BigDecimal valor) {
        try {
            contaService.depositar(id, valor);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{id}/saque")
    public ResponseEntity<?> sacar(@PathVariable Long id, @RequestBody BigDecimal valor) {
        try {
            contaService.sacar(id, valor);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{id}/pix")
    public ResponseEntity<?> transferirPix(
            @PathVariable Long id,
            @RequestParam String numeroContaDestino,
            @RequestBody BigDecimal valor) {
        try {
            contaService.transferirPix(id, numeroContaDestino, valor);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    static class TransferenciaRequest {
        private Long contaDestinoId;
        private BigDecimal valor;

        public Long getContaDestinoId() {
            return contaDestinoId;
        }

        public void setContaDestinoId(Long contaDestinoId) {
            this.contaDestinoId = contaDestinoId;
        }

        public BigDecimal getValor() {
            return valor;
        }

        public void setValor(BigDecimal valor) {
            this.valor = valor;
        }
    }

    @PostMapping("/{id}/transferencia")
    public ResponseEntity<?> realizarTransferencia(
            @PathVariable Long id,
            @RequestBody TransferenciaRequest request) {
        try {
            contaService.transferir(id, request.getContaDestinoId(), request.getValor());
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}/manutencao")
    public ResponseEntity<?> aplicarTaxaManutencao(@PathVariable Long id) {
        try {
            contaService.aplicarTaxaManutencaoIndividual(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}/rendimentos")
    public ResponseEntity<?> aplicarRendimentos(@PathVariable Long id) {
        try {
            contaService.aplicarRendimentoIndividual(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    private ContaDTO converterParaDTO(Conta conta) {
        ContaDTO dto = new ContaDTO();
        dto.setId(conta.getId());
        dto.setNumeroConta(conta.getNumeroConta());
        dto.setSaldo(conta.getSaldo());
        dto.setClienteId(conta.getCliente().getId());
        dto.setDataAbertura(conta.getDataAbertura());
        dto.setAtiva(conta.isAtiva());
        return dto;
    }
}