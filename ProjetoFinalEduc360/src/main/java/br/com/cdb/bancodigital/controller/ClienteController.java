package br.com.cdb.bancodigital.controller;

import br.com.cdb.bancodigital.dto.ClienteDTO;
import br.com.cdb.bancodigital.dto.ClienteDtoResponse;
import br.com.cdb.bancodigital.dto.MensagemResponse;
import br.com.cdb.bancodigital.model.Cliente;
import br.com.cdb.bancodigital.service.ClienteService;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @GetMapping
    public ResponseEntity<List<ClienteDtoResponse>> listarTodos() {
        List<Cliente> clientes = clienteService.listarTodos();
        List<ClienteDtoResponse> clientesDTO = clientes.stream()
                .map(this::converterParaResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(clientesDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClienteDtoResponse> buscarPorId(@PathVariable Long id) {
        try {
            Cliente cliente = clienteService.buscarPorId(id);
            return ResponseEntity.ok(converterParaResponseDTO(cliente));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/cpf/{cpf}")
    public ResponseEntity<ClienteDtoResponse> buscarPorCpf(@PathVariable String cpf) {
        return clienteService.buscarPorCpf(cpf)
                .map(cliente -> ResponseEntity.ok(converterParaResponseDTO(cliente)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> cadastrar(@Valid @RequestBody ClienteDTO clienteDTO) {
        try {
            ClienteDtoResponse clienteSalvo = clienteService.cadastrarCliente(clienteDTO);
            String mensagem = String.format("Cliente %s cadastrado com sucesso!\nCPF: %s\nCategoria: %s",
                    clienteSalvo.getNome(),
                    clienteSalvo.getCpf(),
                    clienteSalvo.getCategoria());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new MensagemResponse("Cadastro realizado com sucesso", mensagem));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MensagemResponse("Erro no cadastro", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Long id, @Valid @RequestBody ClienteDTO clienteDTO) {
        try {
            Cliente cliente = converterParaEntidade(clienteDTO);
            Cliente clienteAtualizado = clienteService.atualizar(id, cliente);
            return ResponseEntity.ok(converterParaResponseDTO(clienteAtualizado));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MensagemResponse("Erro na atualização", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        try {
            clienteService.excluir(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/validar-cpf/{cpf}")
    public ResponseEntity<Boolean> validarCpf(@PathVariable String cpf) {
        boolean cpfValido = clienteService.validarCpf(cpf);
        return ResponseEntity.ok(cpfValido);
    }

    private ClienteDtoResponse converterParaResponseDTO(Cliente cliente) {
        return new ClienteDtoResponse(
                cliente.getId(),
                cliente.getCpf(),
                cliente.getNome(),
                cliente.getDataNascimento(),
                cliente.getRua(),
                cliente.getNumero(),
                cliente.getComplemento(),
                cliente.getCidade(),
                cliente.getEstado(),
                cliente.getCep(),
                cliente.getCategoria());
    }

    private Cliente converterParaEntidade(ClienteDTO dto) {
        Cliente cliente = new Cliente();
        BeanUtils.copyProperties(dto, cliente);
        return cliente;
    }
}