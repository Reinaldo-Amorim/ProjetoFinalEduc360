package br.com.cdb.bancodigital;

import br.com.cdb.bancodigital.menu.ClienteMenu;
import br.com.cdb.bancodigital.menu.ContaMenu;
import br.com.cdb.bancodigital.menu.CartaoMenu;
import br.com.cdb.bancodigital.menu.SeguroMenu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
public class MainMenu implements CommandLineRunner {
    private final Scanner scanner;
    private final ClienteMenu clienteMenu;
    private final ContaMenu contaMenu;
    private final CartaoMenu cartaoMenu;
    private final SeguroMenu seguroMenu;

    @Autowired
    public MainMenu(ClienteMenu clienteMenu, ContaMenu contaMenu, CartaoMenu cartaoMenu, SeguroMenu seguroMenu) {
        this.scanner = new Scanner(System.in);
        this.clienteMenu = clienteMenu;
        this.contaMenu = contaMenu;
        this.cartaoMenu = cartaoMenu;
        this.seguroMenu = seguroMenu;
    }

    @Override
    public void run(String... args) {
        exibirMenuPrincipal();
    }

    public void exibirMenuPrincipal() {
        int opcao;
        do {
            System.out.println("\n======== BANCO DIGITAL ========");
            System.out.println("1 - Clientes");
            System.out.println("2 - Contas");
            System.out.println("3 - Cartões");
            System.out.println("4 - Seguros");
            System.out.println("0 - Sair");
            System.out.print("Escolha uma opção: ");

            opcao = lerOpcao();

            switch (opcao) {
                case 1:
                    clienteMenu.mostrarMenu();
                    break;
                case 2:
                    contaMenu.mostrarMenu();
                    break;
                case 3:
                    cartaoMenu.mostrarMenu();
                    break;
                case 4:
                    seguroMenu.mostrarMenu();
                    break;
                case 0:
                    System.out.println("Saindo do sistema. Até logo!");
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
            return -1;
        }
    }
}
