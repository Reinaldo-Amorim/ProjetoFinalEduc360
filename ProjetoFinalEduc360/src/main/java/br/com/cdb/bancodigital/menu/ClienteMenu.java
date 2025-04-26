package br.com.cdb.bancodigital.menu;

import br.com.cdb.bancodigital.dto.ClienteDTO;
import br.com.cdb.bancodigital.model.Cliente;
import br.com.cdb.bancodigital.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.Period;
import java.util.Scanner;

@Component
public class ClienteMenu {
    private final Scanner scanner;
    private final ClienteService clienteService;

    @Autowired
    public ClienteMenu(ClienteService clienteService) {
        this.scanner = new Scanner(System.in);
        this.clienteService = clienteService;
    }

    public void mostrarMenu() {
        int opcao;
        do {
            System.out.println("\n---- Menu Clientes ----");
            System.out.println("1 - Cadastrar cliente");
            System.out.println("2 - Listar clientes");
            System.out.println("3 - Atualizar cliente");
            System.out.println("4 - Excluir cliente");
            System.out.println("5 - Buscar cliente por CPF");
            System.out.println("6 - Voltar");
            System.out.print("Escolha uma opção: ");

            opcao = lerOpcao();

            switch (opcao) {
                case 1:
                    cadastrarCliente();
                    break;
                case 2:
                    listarClientes();
                    break;
                case 3:
                    atualizarCliente();
                    break;
                case 4:
                    excluirCliente();
                    break;
                case 5:
                    buscarClientePorCpf();
                    break;
                case 6:
                    System.out.println("Voltando ao menu principal...");
                    break;
                default:
                    System.out.println("Opção inválida! Tente novamente.");
            }
        } while (opcao != 6);
    }

    private int lerOpcao() {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private void cadastrarCliente() {
        ClienteDTO dto = new ClienteDTO();

        while (true) {
            System.out.print("Nome: ");
            String nome = scanner.nextLine();
            if (nome.matches("^[a-zA-ZÀ-ÿ\\s]{2,100}$")) {
                dto.setNome(nome);
                break;
            }
            System.out.println("Nome inválido! Digite apenas letras e espaços (2 a 100 caracteres).");
        }

        while (true) {
            System.out.print("CPF (apenas números): ");
            String cpfInput = scanner.nextLine();
            String cpfFormatado = clienteService.formatarCpf(cpfInput);
            if (clienteService.validarCpf(cpfInput) && !clienteService.buscarPorCpf(cpfFormatado).isPresent()) {
                dto.setCpf(cpfFormatado);
                break;
            }
            if (clienteService.buscarPorCpf(cpfFormatado).isPresent()) {
                System.out.println("CPF já cadastrado! Digite outro CPF.");
            } else {
                System.out.println("CPF inválido! Digite um CPF válido.");
            }
        }

        while (true) {
            System.out.print("Data de nascimento (dd/MM/yyyy): ");
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                LocalDate dataNascimento = LocalDate.parse(scanner.nextLine(), formatter);
                if (Period.between(dataNascimento, LocalDate.now()).getYears() >= 18) {
                    dto.setDataNascimento(dataNascimento);
                    break;
                }
                System.out.println("Cliente deve ser maior de idade (18 anos ou mais).");
            } catch (DateTimeParseException e) {
                System.out.println("Data inválida! Use o formato dd/MM/yyyy.");
            }
        }

        while (true) {
            System.out.print("Rua: ");
            String rua = scanner.nextLine();
            if (!rua.trim().isEmpty()) {
                dto.setRua(rua);
                break;
            }
            System.out.println("A rua não pode estar vazia.");
        }

        while (true) {
            System.out.print("Número: ");
            String numero = scanner.nextLine();
            if (!numero.trim().isEmpty()) {
                dto.setNumero(numero);
                break;
            }
            System.out.println("O número não pode estar vazio.");
        }

        System.out.print("Complemento (opcional): ");
        dto.setComplemento(scanner.nextLine());

        while (true) {
            System.out.print("Cidade: ");
            String cidade = scanner.nextLine();
            if (!cidade.trim().isEmpty()) {
                dto.setCidade(cidade);
                break;
            }
            System.out.println("A cidade não pode estar vazia.");
        }

        while (true) {
            System.out.print("Estado: ");
            String estado = scanner.nextLine();
            if (!estado.trim().isEmpty()) {
                dto.setEstado(estado);
                break;
            }
            System.out.println("O estado não pode estar vazio.");
        }

        while (true) {
            System.out.print("CEP (apenas números): ");
            String cepInput = scanner.nextLine();
            String cepFormatado = formatarCep(cepInput);
            if (cepFormatado.matches("\\d{5}-\\d{3}")) {
                dto.setCep(cepFormatado);
                break;
            }
            System.out.println("CEP inválido! Digite apenas números (8 dígitos).");
        }

        while (true) {
            System.out.print("Categoria (COMUM, SUPER, PREMIUM): ");
            String categoriaInput = scanner.nextLine().toUpperCase();
            try {
                dto.setCategoria(Cliente.CategoriaCliente.valueOf(categoriaInput));
                break;
            } catch (IllegalArgumentException e) {
                System.out.println("Categoria inválida! As opções são: COMUM, SUPER ou PREMIUM");
            }
        }

        try {
            clienteService.cadastrarCliente(dto);
            System.out.println("Cliente cadastrado com sucesso!");
        } catch (Exception e) {
            System.out.println("Erro ao cadastrar cliente: " + e.getMessage());
        }
    }

    private String formatarCep(String cep) {
        cep = cep.replaceAll("[^0-9]", "");

        if (cep.length() != 8) {
            return cep;
        }

        return cep.substring(0, 5) + "-" + cep.substring(5);
    }

    private void listarClientes() {
        try {
            var clientes = clienteService.listarTodos();
            if (clientes.isEmpty()) {
                System.out.println("Nenhum cliente cadastrado.");
                return;
            }

            System.out.println("\n===== Lista de Clientes =====");
            clientes.forEach(cliente -> {
                System.out.println("ID: " + cliente.getId());
                System.out.println("Nome: " + cliente.getNome());
                System.out.println("CPF: " + cliente.getCpf());
                System.out.println("Categoria: " + cliente.getCategoria());
                System.out.println("-------------------------");
            });
        } catch (Exception e) {
            System.out.println("Erro ao listar clientes: " + e.getMessage());
        }
    }

    private void atualizarCliente() {
        System.out.print("Digite o ID do cliente que deseja atualizar: ");
        try {
            Long id = Long.parseLong(scanner.nextLine());
            Cliente cliente = clienteService.buscarPorId(id);

            System.out.println("Cliente encontrado: " + cliente.getNome() + " (CPF: " + cliente.getCpf() + ")");

            Cliente clienteAtualizado = new Cliente();
            clienteAtualizado.setId(id);

            System.out.println("Digite os novos dados (pressione ENTER para manter o valor atual):");

            System.out.print("Nome [" + cliente.getNome() + "]: ");
            String nome = scanner.nextLine();
            clienteAtualizado.setNome(nome.isEmpty() ? cliente.getNome() : nome);

            System.out.print("CPF [" + cliente.getCpf() + "]: ");
            String cpf = scanner.nextLine();
            clienteAtualizado.setCpf(cpf.isEmpty() ? cliente.getCpf() : clienteService.formatarCpf(cpf));

            System.out.print("Data de nascimento [" + cliente.getDataNascimento() + "] (dd/MM/yyyy): ");
            String dataNascimentoInput = scanner.nextLine();
            if (dataNascimentoInput.isEmpty()) {
                clienteAtualizado.setDataNascimento(cliente.getDataNascimento());
            } else {
                try {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                    LocalDate dataNascimento = LocalDate.parse(dataNascimentoInput, formatter);
                    clienteAtualizado.setDataNascimento(dataNascimento);
                } catch (DateTimeParseException e) {
                    System.out.println("Data inválida! Usando data atual.");
                    clienteAtualizado.setDataNascimento(cliente.getDataNascimento());
                }
            }

            System.out.print("Rua [" + cliente.getRua() + "]: ");
            String rua = scanner.nextLine();
            clienteAtualizado.setRua(rua.isEmpty() ? cliente.getRua() : rua);

            System.out.print("Número [" + cliente.getNumero() + "]: ");
            String numero = scanner.nextLine();
            clienteAtualizado.setNumero(numero.isEmpty() ? cliente.getNumero() : numero);

            System.out.print("Complemento [" + cliente.getComplemento() + "]: ");
            String complemento = scanner.nextLine();
            clienteAtualizado.setComplemento(complemento.isEmpty() ? cliente.getComplemento() : complemento);

            System.out.print("Cidade [" + cliente.getCidade() + "]: ");
            String cidade = scanner.nextLine();
            clienteAtualizado.setCidade(cidade.isEmpty() ? cliente.getCidade() : cidade);

            System.out.print("Estado [" + cliente.getEstado() + "]: ");
            String estado = scanner.nextLine();
            clienteAtualizado.setEstado(estado.isEmpty() ? cliente.getEstado() : estado);

            System.out.print("CEP [" + cliente.getCep() + "]: ");
            String cep = scanner.nextLine();
            clienteAtualizado.setCep(cep.isEmpty() ? cliente.getCep() : formatarCep(cep));

            System.out.print("Categoria [" + cliente.getCategoria() + "] (COMUM, SUPER, PREMIUM): ");
            String categoriaInput = scanner.nextLine();
            try {
                if (categoriaInput.isEmpty()) {
                    clienteAtualizado.setCategoria(cliente.getCategoria());
                } else {
                    clienteAtualizado.setCategoria(Cliente.CategoriaCliente.valueOf(categoriaInput.toUpperCase()));
                }
            } catch (IllegalArgumentException e) {
                System.out.println("Categoria inválida! Usando categoria atual.");
                clienteAtualizado.setCategoria(cliente.getCategoria());
            }

            Cliente atualizado = clienteService.atualizar(id, clienteAtualizado);
            System.out.println("Cliente atualizado com sucesso!");
        } catch (NumberFormatException e) {
            System.out.println("ID inválido!");
        } catch (Exception e) {
            System.out.println("Erro ao atualizar cliente: " + e.getMessage());
        }
    }

    private void excluirCliente() {
        System.out.print("Digite o ID do cliente que deseja excluir: ");
        try {
            Long id = Long.parseLong(scanner.nextLine());

            Cliente cliente = clienteService.buscarPorId(id);
            System.out.println("Cliente encontrado: " + cliente.getNome() + " (CPF: " + cliente.getCpf() + ")");

            System.out.print("Confirma a exclusão? (S/N): ");
            String confirmacao = scanner.nextLine();

            if (confirmacao.equalsIgnoreCase("S")) {
                clienteService.excluir(id);
                System.out.println("Cliente excluído com sucesso!");
            } else {
                System.out.println("Operação cancelada.");
            }
        } catch (NumberFormatException e) {
            System.out.println("ID inválido!");
        } catch (Exception e) {
            System.out.println("Erro ao excluir cliente: " + e.getMessage());
        }
    }

    private void buscarClientePorCpf() {
        System.out.print("Digite o CPF do cliente (xxx.xxx.xxx-xx ou apenas números): ");
        String cpfInput = scanner.nextLine();

        try {
            String cpfFormatado = clienteService.formatarCpf(cpfInput);

            clienteService.buscarPorCpf(cpfFormatado)
                    .ifPresentOrElse(
                            cliente -> {
                                System.out.println("\n==== Cliente Encontrado ====");
                                System.out.println("ID: " + cliente.getId());
                                System.out.println("Nome: " + cliente.getNome());
                                System.out.println("CPF: " + cliente.getCpf());
                                System.out.println("Data de Nascimento: " + cliente.getDataNascimento());
                                System.out.println("Endereço: " + cliente.getRua() + ", " + cliente.getNumero());
                                System.out.println("Cidade/Estado: " + cliente.getCidade() + "/" + cliente.getEstado());
                                System.out.println("CEP: " + cliente.getCep());
                                System.out.println("Categoria: " + cliente.getCategoria());
                            },
                            () -> System.out.println("Cliente não encontrado."));
        } catch (Exception e) {
            System.out.println("Erro ao buscar cliente: " + e.getMessage());
        }
    }
}
