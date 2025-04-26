package br.com.cdb.bancodigital.menu;

import br.com.cdb.bancodigital.model.Cartao;
import br.com.cdb.bancodigital.model.CartaoCredito;
import br.com.cdb.bancodigital.model.Seguro;
import br.com.cdb.bancodigital.model.SeguroFraude;
import br.com.cdb.bancodigital.model.SeguroViagem;
import br.com.cdb.bancodigital.service.CartaoService;
import br.com.cdb.bancodigital.service.SeguroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

@Component
public class SeguroMenu {
    private final Scanner scanner;
    private final SeguroService seguroService;
    private final CartaoService cartaoService;

    @Autowired
    public SeguroMenu(SeguroService seguroService, CartaoService cartaoService) {
        this.scanner = new Scanner(System.in);
        this.seguroService = seguroService;
        this.cartaoService = cartaoService;
    }

    public void mostrarMenu() {
        int opcao;
        do {
            System.out.println("\n---- Menu Seguros ----");
            System.out.println("1 - Contratar seguro viagem");
            System.out.println("2 - Contratar seguro fraude");
            System.out.println("3 - Listar seguros");
            System.out.println("4 - Cancelar seguro");
            System.out.println("5 - Voltar");
            System.out.print("Escolha uma opção: ");

            opcao = lerOpcao();

            switch (opcao) {
                case 1:
                    contratarSeguroViagem();
                    break;
                case 2:
                    contratarSeguroFraude();
                    break;
                case 3:
                    listarSeguros();
                    break;
                case 4:
                    cancelarSeguro();
                    break;
                case 5:
                    System.out.println("Voltando ao menu principal...");
                    break;
                default:
                    System.out.println("Opção inválida! Tente novamente.");
            }
        } while (opcao != 5);
    }

    private int lerOpcao() {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private void listarCartoesCredito() {
        List<Cartao> cartoes = cartaoService.listarTodos();
        if (cartoes.isEmpty()) {
            System.out.println("Nenhum cartão cadastrado.");
            return;
        }

        System.out.println("\n===== Lista de Cartões de Crédito =====");
        cartoes.stream()
                .filter(c -> c instanceof CartaoCredito)
                .forEach(cartao -> {
                    System.out.println("ID: " + cartao.getId());
                    System.out.println("Número: " + cartao.getNumeroCartao());
                    System.out.println("Titular: " + cartao.getNomeTitular());
                    System.out.println("Validade: " + cartao.getDataValidade());
                    System.out.println("Status: " + (cartao.isAtivo() ? "Ativo" : "Bloqueado"));
                    System.out.println("-------------------------");
                });
    }

    private void listarSeguros() {
        List<Seguro> seguros = seguroService.listarTodos();
        if (seguros.isEmpty()) {
            System.out.println("Nenhum seguro contratado.");
            return;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        System.out.println("\n===== Lista de Seguros =====");
        seguros.forEach(seguro -> {
            System.out.println("ID: " + seguro.getId());
            System.out.println("Tipo: " + seguro.getTipoSeguro());

            if (seguro.getCartao() != null) {
                System.out.println("Cliente: " + seguro.getCartao().getNomeTitular());
            } else {
                System.out.println("Cliente: Não disponível");
            }

            System.out.println("Data Contratação: " + seguro.getDataContratacao().format(formatter));
            System.out.println("Vigência: " + seguro.getDataInicio().format(formatter) +
                    " a " + seguro.getDataFim().format(formatter));
            System.out.println("Status: " + (seguro.isAtivo() ? "Ativo" : "Cancelado"));
            System.out.println("Valor Prêmio: R$ " + seguro.getValorPremio());
            System.out.println("-------------------------");
        });
    }

    private void contratarSeguroViagem() {
        listarCartoesCredito();
        System.out.print("Digite o ID do cartão de crédito: ");
        try {
            Long cartaoId = Long.parseLong(scanner.nextLine());
            SeguroViagem seguro = seguroService.contratarSeguroViagem(cartaoId);

            System.out.println("Seguro viagem contratado com sucesso!");
            System.out.println("ID do Seguro: " + seguro.getId());
            System.out.println("Período de vigência: " + seguro.getDataInicio() + " a " + seguro.getDataFim());
            System.out.println("Valor do prêmio: R$ " + seguro.getValorPremio());
        } catch (NumberFormatException e) {
            System.out.println("ID inválido! Digite um número válido.");
        } catch (Exception e) {
            System.out.println("Erro ao contratar seguro: " + e.getMessage());
        }
    }

    private void contratarSeguroFraude() {
        listarCartoesCredito();
        System.out.print("Digite o ID do cartão de crédito: ");
        try {
            Long cartaoId = Long.parseLong(scanner.nextLine());
            SeguroFraude seguro = seguroService.contratarSeguroFraude(cartaoId);

            System.out.println("Seguro fraude contratado com sucesso!");
            System.out.println("ID do Seguro: " + seguro.getId());
            System.out.println("Número da Apólice: " + seguro.getNumeroApolice());
            System.out.println("Período de vigência: " + seguro.getDataInicio() + " a " + seguro.getDataFim());
            System.out.println("Valor da Apólice: R$ " + seguro.getValorApolice());
            System.out.println("Valor do prêmio: R$ " + seguro.getValorPremio());
        } catch (NumberFormatException e) {
            System.out.println("ID inválido! Digite um número válido.");
        } catch (Exception e) {
            System.out.println("Erro ao contratar seguro: " + e.getMessage());
        }
    }

    private void cancelarSeguro() {
        listarSeguros();
        System.out.print("Digite o ID do seguro que deseja cancelar: ");
        try {
            Long seguroId = Long.parseLong(scanner.nextLine());

            System.out.print("Confirma o cancelamento? (S/N): ");
            String confirmacao = scanner.nextLine();

            if (confirmacao.equalsIgnoreCase("S")) {
                seguroService.cancelarSeguro(seguroId);
                System.out.println("Seguro cancelado com sucesso!");
            } else {
                System.out.println("Operação cancelada.");
            }
        } catch (NumberFormatException e) {
            System.out.println("ID inválido! Digite um número válido.");
        } catch (Exception e) {
            System.out.println("Erro ao cancelar seguro: " + e.getMessage());
        }
    }
}
