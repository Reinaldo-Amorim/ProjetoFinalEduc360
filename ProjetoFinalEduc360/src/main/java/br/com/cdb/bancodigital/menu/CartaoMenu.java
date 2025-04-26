package br.com.cdb.bancodigital.menu;

import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import br.com.cdb.bancodigital.model.CartaoCredito;
import br.com.cdb.bancodigital.model.CartaoDebito;
import br.com.cdb.bancodigital.model.Conta;
import br.com.cdb.bancodigital.service.CartaoService;
import br.com.cdb.bancodigital.service.ContaService;

@Component
public class CartaoMenu {

    private final Scanner scanner;
    private final CartaoService cartaoService;
    private final ContaService contaService;

    @Autowired
    public CartaoMenu(CartaoService cartaoService, ContaService contaService) {
        this.scanner = new Scanner(System.in);
        this.cartaoService = cartaoService;
        this.contaService = contaService;
    }

    public void mostrarMenu() {
        int opcao;
        do {
            System.out.println("\n======== MENU CARTÕES ========");
            System.out.println("1 - Listar todos os cartões");
            System.out.println("2 - Buscar cartão por ID");
            System.out.println("3 - Buscar cartão por número");
            System.out.println("4 - Listar cartões por conta");
            System.out.println("5 - Emitir cartão de crédito");
            System.out.println("6 - Emitir cartão de débito");
            System.out.println("7 - Alterar senha do cartão");
            System.out.println("8 - Alterar status do cartão");
            System.out.println("9 - Realizar pagamento com cartão");
            System.out.println("10 - Alterar limite diário do cartão de débito");
            System.out.println("11 - Aplicar taxa a todos os cartões");
            System.out.println("12 - Desbloquear cartão");
            System.out.println("13 - Bloquear cartão");
            System.out.println("0 - Voltar ao menu principal");
            System.out.print("Escolha uma opção: ");

            opcao = lerOpcao();

            switch (opcao) {
                case 1:
                    listarTodosCartoes();
                    break;
                case 2:
                    buscarCartaoPorId();
                    break;
                case 3:
                    buscarCartaoPorNumero();
                    break;
                case 4:
                    listarCartoesPorConta();
                    break;
                case 5:
                    emitirCartaoCredito();
                    break;
                case 6:
                    emitirCartaoDebito();
                    break;
                case 7:
                    alterarSenhaCartao();
                    break;
                case 8:
                    alterarStatusCartao();
                    break;
                case 9:
                    realizarPagamentoComCartao();
                    break;
                case 10:
                    alterarLimiteDiarioDebito();
                    break;
                case 11:
                    aplicarTaxaTodosCartoes();
                    break;
                case 12:
                    desbloquearCartao();
                    break;
                case 13:
                    bloquearCartao();
                    break;
                case 0:
                    System.out.println("Voltando ao menu principal...");
                    break;
                default:
                    System.out.println("Opção inválida! Tente novamente.");
            }
        } while (opcao != 0);
    }

    private int lerOpcao() {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Opção inválida! Digite um número inteiro.");
            return -1;
        }
    }

    private void listarTodosCartoes() {
        System.out.println("\n======== LISTA DE CARTÕES ========");
        cartaoService.listarTodos()
                .forEach(cartao -> System.out.println(cartao.getId() + " - Cartão: " + cartao.getNumeroCartao() +
                        " - Cliente: " + cartao.getConta().getCliente().getNome()));
    }

    private void buscarCartaoPorId() {
        System.out.print("Digite o ID do cartão: ");
        try {
            Long id = Long.parseLong(scanner.nextLine());
            System.out.println(cartaoService.buscarPorId(id));
        } catch (NumberFormatException e) {
            System.out.println("ID inválido! Digite um número válido.");
        } catch (Exception e) {
            System.out.println("Erro ao buscar cartão: " + e.getMessage());
        }
    }

    private void buscarCartaoPorNumero() {
        System.out.print("Digite o número do cartão: ");
        try {
            String numeroCartao = scanner.nextLine();
            cartaoService.buscarPorNumeroCartao(numeroCartao)
                    .ifPresentOrElse(
                            System.out::println,
                            () -> System.out.println("Cartão não encontrado com o número: " + numeroCartao));
        } catch (Exception e) {
            System.out.println("Erro ao buscar cartão: " + e.getMessage());
        }
    }

    private void listarContas() {
        System.out.println("\n======== LISTA DE CONTAS ========");
        contaService.listarTodas()
                .forEach(conta -> System.out.println(conta.getId() + " - Conta: " + conta.getNumeroConta() +
                        " - Cliente: " + conta.getCliente().getNome()));
    }

    private void listarCartoesPorConta() {
        listarContas();
        System.out.print("Digite o ID da conta: ");
        try {
            Long contaId = Long.parseLong(scanner.nextLine());
            List<br.com.cdb.bancodigital.model.Cartao> cartoes = cartaoService.listarCartoesPorConta(contaId);

            if (cartoes.isEmpty()) {
                System.out.println("Nenhum cartão encontrado para a conta com ID: " + contaId);
            } else {
                Conta conta = contaService.buscarPorId(contaId);
                System.out.println("\n======== CARTÕES DA CONTA " + contaId + " - Cliente: "
                        + conta.getCliente().getNome() + " ========");
                cartoes.forEach(cartao -> System.out.println(cartao.getId() + " - " + cartao.getNumeroCartao()));
            }
        } catch (NumberFormatException e) {
            System.out.println("ID inválido! Digite um número válido.");
        } catch (Exception e) {
            System.out.println("Erro ao listar cartões: " + e.getMessage());
        }
    }

    private void emitirCartaoCredito() {
        listarContas();
        System.out.print("Digite o ID da conta: ");
        try {
            Long contaId = Long.parseLong(scanner.nextLine());

            Conta conta = contaService.buscarPorId(contaId);
            if (!conta.isAtiva()) {
                System.out.println("\nNão é possível emitir cartão para uma conta inativa ou fechada.");
                return;
            }

            CartaoCredito cartao = cartaoService.emitirCartaoCredito(contaId);
            System.out.println("\nCartão de crédito emitido com sucesso!");
            System.out.println("Número do cartão: " + cartao.getNumeroCartao());
            System.out.println("Titular: " + cartao.getNomeTitular());
            System.out.println("Conta: " + cartao.getConta().getNumeroConta());
            System.out.println("Cliente: " + cartao.getConta().getCliente().getNome());
            System.out.println("Validade: " + cartao.getDataValidade());
            System.out.println("Limite de crédito: R$ " + cartao.getLimiteCredito());
        } catch (NumberFormatException e) {
            System.out.println("ID inválido! Digite um número válido.");
        } catch (Exception e) {
            System.out.println("Erro ao emitir cartão: " + e.getMessage());
        }
    }

    private void emitirCartaoDebito() {
        listarContas();
        System.out.print("Digite o ID da conta: ");
        try {
            Long contaId = Long.parseLong(scanner.nextLine());

            Conta conta = contaService.buscarPorId(contaId);
            if (!conta.isAtiva()) {
                System.out.println("\nNão é possível emitir cartão para uma conta inativa ou fechada.");
                return;
            }

            CartaoDebito cartao = cartaoService.emitirCartaoDebito(contaId);
            System.out.println("\nCartão de débito emitido com sucesso!");
            System.out.println("Número do cartão: " + cartao.getNumeroCartao());
            System.out.println("Titular: " + cartao.getNomeTitular());
            System.out.println("Conta: " + cartao.getConta().getNumeroConta());
            System.out.println("Cliente: " + cartao.getConta().getCliente().getNome());
            System.out.println("Validade: " + cartao.getDataValidade());
            System.out.println("Limite diário: R$ " + cartao.getLimiteDiario());
        } catch (NumberFormatException e) {
            System.out.println("ID inválido! Digite um número válido.");
        } catch (Exception e) {
            System.out.println("Erro ao emitir cartão: " + e.getMessage());
        }
    }

    private void alterarSenhaCartao() {
        System.out.print("Digite o ID do cartão: ");
        try {
            Long id = Long.parseLong(scanner.nextLine());
            System.out.print("Digite a nova senha: ");
            String novaSenha = scanner.nextLine();
            cartaoService.alterarSenha(id, novaSenha);
            System.out.println("Senha alterada com sucesso!");
        } catch (NumberFormatException e) {
            System.out.println("ID inválido! Digite um número válido.");
        } catch (Exception e) {
            System.out.println("Erro ao alterar senha: " + e.getMessage());
        }
    }

    private void alterarStatusCartao() {
        System.out.print("Digite o ID do cartão: ");
        try {
            Long id = Long.parseLong(scanner.nextLine());
            System.out.print("Digite o novo status (true para ativo, false para inativo): ");
            boolean ativo = Boolean.parseBoolean(scanner.nextLine());
            cartaoService.alterarStatus(id, ativo);
            System.out.println("Status do cartão alterado com sucesso!");
        } catch (NumberFormatException e) {
            System.out.println("ID inválido! Digite um número válido.");
        } catch (Exception e) {
            System.out.println("Erro ao alterar status: " + e.getMessage());
        }
    }

    private void realizarPagamentoComCartao() {
        System.out.print("Digite o ID do cartão: ");
        try {
            Long id = Long.parseLong(scanner.nextLine());
            System.out.print("Digite o valor do pagamento: ");
            BigDecimal valor = new BigDecimal(scanner.nextLine());

            boolean sucesso = cartaoService.realizarPagamento(id, valor);

            if (sucesso) {
                System.out.println("Pagamento realizado com sucesso!");
            } else {
                System.out.println("Pagamento falhou. Verifique o limite ou status do cartão.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Valor inválido! Digite um número válido.");
        } catch (Exception e) {
            System.out.println("Erro ao realizar pagamento: " + e.getMessage());
        }
    }

    private void alterarLimiteDiarioDebito() {
        System.out.print("Digite o ID do cartão de débito: ");
        try {
            Long id = Long.parseLong(scanner.nextLine());
            System.out.print("Digite o novo limite diário: ");
            BigDecimal novoLimite = new BigDecimal(scanner.nextLine());

            cartaoService.alterarLimiteDiarioDebito(id, novoLimite);
            System.out.println("Limite diário alterado com sucesso!");
        } catch (NumberFormatException e) {
            System.out.println("Valor inválido! Digite um número válido.");
        } catch (Exception e) {
            System.out.println("Erro ao alterar limite diário: " + e.getMessage());
        }
    }

    private void aplicarTaxaTodosCartoes() {
        cartaoService.aplicarTaxaTodasCartoes();
        System.out.println("Taxa aplicada a todos os cartões com sucesso!");
    }

    private void desbloquearCartao() {
        System.out.print("Digite o ID do cartão: ");
        try {
            Long id = Long.parseLong(scanner.nextLine());
            cartaoService.desbloquearCartao(id);
            System.out.println("Cartão desbloqueado com sucesso!");
        } catch (NumberFormatException e) {
            System.out.println("ID inválido! Digite um número válido.");
        } catch (Exception e) {
            System.out.println("Erro ao desbloquear cartão: " + e.getMessage());
        }
    }

    private void bloquearCartao() {
        System.out.print("Digite o ID do cartão: ");
        try {
            Long id = Long.parseLong(scanner.nextLine());
            cartaoService.bloquearCartao(id);
            System.out.println("Cartão bloqueado com sucesso!");
        } catch (NumberFormatException e) {
            System.out.println("ID inválido! Digite um número válido.");
        } catch (Exception e) {
            System.out.println("Erro ao bloquear cartão: " + e.getMessage());
        }
    }
}
