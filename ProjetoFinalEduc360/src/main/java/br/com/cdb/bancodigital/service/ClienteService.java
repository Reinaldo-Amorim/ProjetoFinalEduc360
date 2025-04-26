package br.com.cdb.bancodigital.service;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import br.com.cdb.bancodigital.dto.ClienteDTO;
import br.com.cdb.bancodigital.dto.ClienteDtoResponse;
import br.com.cdb.bancodigital.model.Cliente;
import br.com.cdb.bancodigital.repository.ClienteRepository;

@Service
@Transactional
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    public List<Cliente> listarTodos() {
        return clienteRepository.findAll();
    }

    public Cliente buscarPorId(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));
    }

    public Optional<Cliente> buscarPorCpf(String cpf) {
        return clienteRepository.findByCpf(cpf);
    }

    @Transactional
    public Cliente cadastrar(Cliente cliente) {
        validarCliente(cliente);
        return clienteRepository.save(cliente);
    }

    @Transactional
    public ClienteDtoResponse cadastrarCliente(ClienteDTO dto) {
        if (clienteRepository.findByCpf(dto.getCpf()).isPresent()) {
            throw new RuntimeException("CPF já cadastrado.");
        }

        String cpfFormatado = formatarCpf(dto.getCpf());

        Cliente cliente = new Cliente();
        cliente.setNome(dto.getNome());
        cliente.setCpf(cpfFormatado);
        cliente.setDataNascimento(dto.getDataNascimento());
        cliente.setRua(dto.getRua());
        cliente.setNumero(dto.getNumero());
        cliente.setComplemento(dto.getComplemento());
        cliente.setCidade(dto.getCidade());
        cliente.setEstado(dto.getEstado());
        cliente.setCep(dto.getCep());
        cliente.setCategoria(dto.getCategoria() != null ? dto.getCategoria() : Cliente.CategoriaCliente.COMUM);

        validarCliente(cliente);

        cliente = clienteRepository.save(cliente);

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

    @Transactional
    public void excluir(Long id) {
        Cliente cliente = buscarPorId(id);
        clienteRepository.delete(cliente);
    }

    public void validarCliente(Cliente cliente) {
        if (cliente.getNome() == null || cliente.getNome().trim().isEmpty()) {
            throw new RuntimeException("Nome é obrigatório");
        }

        if (!cliente.getNome().matches("^[a-zA-ZÀ-ÿ\\s]*$")) {
            throw new RuntimeException("Nome deve conter apenas letras e espaços");
        }

        if (cliente.getNome().length() < 2 || cliente.getNome().length() > 100) {
            throw new RuntimeException("Nome deve ter entre 2 e 100 caracteres");
        }

        if (cliente.getId() == null && clienteRepository.existsByCpf(cliente.getCpf())) {
            throw new RuntimeException("CPF já cadastrado");
        }

        if (cliente.getCpf() == null || cliente.getCpf().trim().isEmpty()) {
            throw new RuntimeException("CPF é obrigatório");
        }

        if (!cliente.getCpf().matches("\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}")) {
            throw new RuntimeException("CPF deve estar no formato xxx.xxx.xxx-xx");
        }

        LocalDate dataAtual = LocalDate.now();
        Period periodo = Period.between(cliente.getDataNascimento(), dataAtual);
        if (periodo.getYears() < 18) {
            throw new RuntimeException("Cliente deve ser maior de idade (18 anos ou mais)");
        }

        if (!cliente.getCep().matches("\\d{5}-\\d{3}")) {
            throw new RuntimeException("CEP deve estar no formato xxxxx-xxx");
        }
    }

    public String formatarCpf(String cpf) {
        cpf = cpf.replaceAll("[^0-9]", "");

        if (cpf.length() != 11) {
            return cpf;
        }

        return cpf.substring(0, 3) + "." +
                cpf.substring(3, 6) + "." +
                cpf.substring(6, 9) + "-" +
                cpf.substring(9);
    }

    public boolean validarCpf(String cpf) {
        cpf = cpf.replaceAll("[^0-9]", "");

        if (cpf.length() != 11) {
            return false;
        }

        boolean todosDigitosIguais = true;
        for (int i = 1; i < 11; i++) {
            if (cpf.charAt(i) != cpf.charAt(0)) {
                todosDigitosIguais = false;
                break;
            }
        }
        if (todosDigitosIguais) {
            return false;
        }

        int soma = 0;
        for (int i = 0; i < 9; i++) {
            soma += Character.getNumericValue(cpf.charAt(i)) * (10 - i);
        }
        int resto = soma % 11;
        int digito1 = resto < 2 ? 0 : 11 - resto;

        if (Character.getNumericValue(cpf.charAt(9)) != digito1) {
            return false;
        }

        soma = 0;
        for (int i = 0; i < 10; i++) {
            soma += Character.getNumericValue(cpf.charAt(i)) * (11 - i);
        }
        resto = soma % 11;
        int digito2 = resto < 2 ? 0 : 11 - resto;

        return Character.getNumericValue(cpf.charAt(10)) == digito2;
    }

    @Transactional
    public Cliente atualizar(Long id, Cliente clienteAtualizado) {
        Cliente clienteExistente = buscarPorId(id);

        if (!clienteExistente.getCpf().equals(clienteAtualizado.getCpf()) &&
                clienteRepository.findByCpf(clienteAtualizado.getCpf()).isPresent()) {
            throw new RuntimeException("CPF já cadastrado para outro cliente");
        }

        clienteAtualizado.setCpf(formatarCpf(clienteAtualizado.getCpf()));

        validarCliente(clienteAtualizado);

        clienteExistente.setNome(clienteAtualizado.getNome());
        clienteExistente.setCpf(clienteAtualizado.getCpf());
        clienteExistente.setDataNascimento(clienteAtualizado.getDataNascimento());
        clienteExistente.setRua(clienteAtualizado.getRua());
        clienteExistente.setNumero(clienteAtualizado.getNumero());
        clienteExistente.setComplemento(clienteAtualizado.getComplemento());
        clienteExistente.setCidade(clienteAtualizado.getCidade());
        clienteExistente.setEstado(clienteAtualizado.getEstado());
        clienteExistente.setCep(clienteAtualizado.getCep());
        clienteExistente.setCategoria(clienteAtualizado.getCategoria());

        return clienteRepository.save(clienteExistente);
    }
}
