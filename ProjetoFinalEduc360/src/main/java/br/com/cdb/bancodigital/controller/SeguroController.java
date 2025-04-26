package br.com.cdb.bancodigital.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import br.com.cdb.bancodigital.model.Seguro;
import br.com.cdb.bancodigital.model.SeguroFraude;
import br.com.cdb.bancodigital.model.SeguroVida;
import br.com.cdb.bancodigital.service.SeguroService;

@RestController
@RequestMapping("/api/seguros")
public class SeguroController {

    @Autowired
    private SeguroService seguroService;

    @GetMapping
    public ResponseEntity<List<Seguro>> listarTodos() {
        List<Seguro> seguros = seguroService.listarTodos();
        return ResponseEntity.ok(seguros);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Seguro> buscarPorId(@PathVariable Long id) {
        try {
            Seguro seguro = seguroService.buscarPorId(id);
            return ResponseEntity.ok(seguro);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/fraude/cartao/{cartaoId}")
    public ResponseEntity<SeguroFraude> contratarSeguroFraude(@PathVariable Long cartaoId) {
        try {
            SeguroFraude seguroFraude = seguroService.contratarSeguroFraude(cartaoId);
            return ResponseEntity.status(HttpStatus.CREATED).body(seguroFraude);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/vida/cliente/{clienteId}")
    public ResponseEntity<SeguroVida> contratarSeguroVida(@PathVariable Long clienteId) {
        try {
            SeguroVida seguroVida = seguroService.contratarSeguroVida(clienteId);
            return ResponseEntity.status(HttpStatus.CREATED).body(seguroVida);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}/cancelar")
    public ResponseEntity<Void> cancelarSeguro(@PathVariable Long id) {
        try {
            seguroService.cancelarSeguro(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
}