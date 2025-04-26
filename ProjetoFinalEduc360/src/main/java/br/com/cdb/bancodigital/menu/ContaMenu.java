package br.com.cdb.bancodigital.menu;

import br.com.cdb.bancodigital.model.Conta;
import br.com.cdb.bancodigital.model.ContaCorrente;
import br.com.cdb.bancodigital.model.ContaPoupanca;
import br.com.cdb.bancodigital.service.ContaService;
import br.com.cdb.bancodigital.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;

@Component
public class ContaMenu {
    private final Scanner scanner;
    private final ContaService contaService;
    private final ClienteService clienteService;

    @Autowired
    public ContaMenu(ContaService contaService, ClienteService clienteService) {
        this.scanner = new Scanner(System.in);
        this.contaService = contaService;
        this.clienteService = clienteService;
    }

    public void mostrarMenu() {
        int opcao;
        do {
            System.out.println("\n======== Menu Contas ========");
            System.out.println("1 - Abrir Conta Corrente");
            System.out.println("2 - Abrir Conta Poupança");
            System.out.println("3 - Listar Contas");
            System.out.println("4 - Consultar Saldo");
            System.out.println("5 - Realizar Depósito");
            System.out.println("6 - Realizar Saque");
            System.out.println("7 - Realizar Transferência PIX");
            System.out.println("8 - Fechar Conta");
            System.out.println("9 - Voltar ao Menu Principal");
            System.out.print("Escolha uma opção: ");

            opcao = lerOpcao();

            switch (opcao) {
                case 1:
                    abrirContaCorrente();
                    break;
                case 2:
                    abrirContaPoupanca();
                    break;
                case 3:
                    listarContas();
                    break;
                case 4:
                    consultarSaldo();
                    break;
                case 5:
                    depositar();
                    break;
                case 6:
                    sacar();
                    break;
                case 7:
                    transferirPix();
                    break;
                case 8:
                    fecharConta();
                    break;
                case 9:
                    System.out.println("Retornando ao menu principal...");
                    break;
                default:
                    System.out.println("Opção inválida! Tente novamente.");
            }
        } while (opcao != 9);
    }

    private int lerOpcao() {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private void listarClientes() {
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
            System.out.println("-------------------------");
        });
    }

    private void abrirContaCorrente() {
        System.out.println("\n=== Abertura de Conta Corrente ===");
        listarClientes();

        String idInput;
        Long clienteId;
        do {
            System.out.print("Digite o ID do cliente: ");
            idInput = scanner.nextLine();
            try {
                clienteId = Long.parseLong(idInput);
                ContaCorrente conta = contaService.criarContaCorrente(clienteId);
                System.out.println("Conta corrente criada com sucesso!");
                System.out.println("Número da conta: " + conta.getNumeroConta());
                break;
            } catch (NumberFormatException e) {
                System.out.println("ID inválido! Digite apenas números.");
            } catch (Exception e) {
                System.out.println("Erro ao criar conta: " + e.getMessage());
            }
        } while (true);
    }

    private void abrirContaPoupanca() {
        System.out.println("\n=== Abertura de Conta Poupança ===");
        listarClientes();

        String idInput;
        Long clienteId;
        do {
            System.out.print("Digite o ID do cliente: ");
            idInput = scanner.nextLine();
            try {
                clienteId = Long.parseLong(idInput);
                ContaPoupanca conta = contaService.criarContaPoupanca(clienteId);
                System.out.println("Conta poupança criada com sucesso!");
                System.out.println("Número da conta: " + conta.getNumeroConta());
                break;
            } catch (NumberFormatException e) {
                System.out.println("ID inválido! Digite apenas números.");
            } catch (Exception e) {
                System.out.println("Erro ao criar conta: " + e.getMessage());
            }
        } while (true);
    }

    private void listarContas() {
        List<Conta> contas = contaService.listarTodas();
        if (contas.isEmpty()) {
            System.out.println("Nenhuma conta cadastrada.");
            return;
        }

        System.out.println("\n===== Lista de Contas =====");
        contas.forEach(conta -> {
            System.out.println("ID: " + conta.getId());
            System.out.println("Número: " + conta.getNumeroConta());
            System.out.println("Cliente: " + conta.getCliente().getNome());
            System.out.println("Tipo: " + (conta instanceof ContaCorrente ? "Corrente" : "Poupança"));
            System.out.println("Saldo: R$ " + conta.getSaldo());
            System.out.println("Status: " + (conta.isAtiva() ? "Ativa" : "Inativa"));
            System.out.println("-------------------------");
        });
    }

    private void consultarSaldo() {
        String idInput;
        Long id;
        do {
            System.out.print("Digite o ID da conta: ");
            idInput = scanner.nextLine();
            try {
                id = Long.parseLong(idInput);
                BigDecimal saldo = contaService.consultarSaldo(id);
                System.out.println("Saldo atual: R$ " + saldo);
                break;
            } catch (NumberFormatException e) {
                System.out.println("ID inválido! Digite apenas números.");
            } catch (Exception e) {
                System.out.println("Erro ao consultar saldo: " + e.getMessage());
            }
        } while (true);
    }

    private void depositar() {
        String idInput, valorInput;
        Long id;
        BigDecimal valor;

        do {
            System.out.print("Digite o ID da conta: ");
            idInput = scanner.nextLine();
            try {
                id = Long.parseLong(idInput);
                do {
                    System.out.print("Digite o valor a depositar (R$): ");
                    valorInput = scanner.nextLine().replace(",", ".");
                    try {
                        valor = new BigDecimal(valorInput);
                        if (valor.compareTo(BigDecimal.ZERO) <= 0) {
                            System.out.println("O valor deve ser maior que zero!");
                            continue;
                        }
                        contaService.depositar(id, valor);
                        System.out.println("Depósito realizado com sucesso!");
                        BigDecimal saldoAtual = contaService.consultarSaldo(id);
                        System.out.println("Saldo atual: R$ " + saldoAtual);
                        return;
                    } catch (NumberFormatException e) {
                        System.out.println("Valor inválido! Use apenas números e ponto/vírgula.");
                    }
                } while (true);
            } catch (NumberFormatException e) {
                System.out.println("ID inválido! Digite apenas números.");
            } catch (Exception e) {
                System.out.println("Erro ao realizar depósito: " + e.getMessage());
            }
        } while (true);
    }

    private void sacar() {
        String idInput, valorInput;
        Long id;
        BigDecimal valor;

        do {
            System.out.print("Digite o ID da conta: ");
            idInput = scanner.nextLine();
            try {
                id = Long.parseLong(idInput);
                do {
                    System.out.print("Digite o valor a sacar (R$): ");
                    valorInput = scanner.nextLine().replace(",", ".");
                    try {
                        valor = new BigDecimal(valorInput);
                        if (valor.compareTo(BigDecimal.ZERO) <= 0) {
                            System.out.println("O valor deve ser maior que zero!");
                            continue;
                        }
                        contaService.sacar(id, valor);
                        System.out.println("Saque realizado com sucesso!");
                        BigDecimal saldoAtual = contaService.consultarSaldo(id);
                        System.out.println("Saldo atual: R$ " + saldoAtual);
                        return;
                    } catch (NumberFormatException e) {
                        System.out.println("Valor inválido! Use apenas números e ponto/vírgula.");
                    }
                } while (true);
            } catch (NumberFormatException e) {
                System.out.println("ID inválido! Digite apenas números.");
            } catch (Exception e) {
                System.out.println("Erro ao realizar saque: " + e.getMessage());
            }
        } while (true);
    }

    private void transferirPix() {
        String idInput, numeroContaDestino, valorInput;
        Long idOrigem;
        BigDecimal valor;

        do {
            System.out.print("Digite o ID da conta de origem: ");
            idInput = scanner.nextLine();
            try {
                idOrigem = Long.parseLong(idInput);
                do {
                    System.out.print("Digite o número da conta de destino: ");
                    numeroContaDestino = scanner.nextLine();
                    if (numeroContaDestino.trim().isEmpty()) {
                        System.out.println("O número da conta de destino não pode estar vazio!");
                        continue;
                    }
                    do {
                        System.out.print("Digite o valor a transferir (R$): ");
                        valorInput = scanner.nextLine().replace(",", ".");
                        try {
                            valor = new BigDecimal(valorInput);
                            if (valor.compareTo(BigDecimal.ZERO) <= 0) {
                                System.out.println("O valor deve ser maior que zero!");
                                continue;
                            }
                            contaService.transferirPix(idOrigem, numeroContaDestino, valor);
                            System.out.println("Transferência realizada com sucesso!");
                            BigDecimal saldoAtual = contaService.consultarSaldo(idOrigem);
                            System.out.println("Saldo atual: R$ " + saldoAtual);
                            return;
                        } catch (NumberFormatException e) {
                            System.out.println("Valor inválido! Use apenas números e ponto/vírgula.");
                        }
                    } while (true);
                } while (true);
            } catch (NumberFormatException e) {
                System.out.println("ID inválido! Digite apenas números.");
            } catch (Exception e) {
                System.out.println("Erro ao realizar transferência: " + e.getMessage());
            }
        } while (true);
    }

    private void fecharConta() {
        String idInput;
        Long id;

        do {
            System.out.print("Digite o ID da conta que deseja fechar: ");
            idInput = scanner.nextLine();
            try {
                id = Long.parseLong(idInput);
                System.out.print("Esta operação é irreversível. Confirmar? (S/N): ");
                String confirmacao = scanner.nextLine();
                if (confirmacao.equalsIgnoreCase("S")) {
                    contaService.fecharConta(id);
                    System.out.println("Conta fechada com sucesso!");
                    return;
                } else {
                    System.out.println("Operação cancelada.");
                    return;
                }
            } catch (NumberFormatException e) {
                System.out.println("ID inválido! Digite apenas números.");
            } catch (Exception e) {
                System.out.println("Erro ao fechar conta: " + e.getMessage());
            }
        } while (true);
    }
}
