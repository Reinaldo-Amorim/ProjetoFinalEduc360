package br.com.cdb.bancodigital.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("DEBITO")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class CartaoDebito extends Cartao {

    @NotNull
    @PositiveOrZero
    private BigDecimal limiteDiario = new BigDecimal("1000.00");

    @NotNull
    @PositiveOrZero
    private BigDecimal totalGastoDia = BigDecimal.ZERO;

    @NotNull
    private LocalDate diaAtual = LocalDate.now();

    @Override
    public boolean realizarPagamento(BigDecimal valor) {

        if (!isAtivo()) {
            return false;
        }

        LocalDate hoje = LocalDate.now();
        if (!this.diaAtual.equals(hoje)) {
            this.diaAtual = hoje;
            this.totalGastoDia = BigDecimal.ZERO;
        }

        if (this.totalGastoDia.add(valor).compareTo(this.limiteDiario) > 0) {
            return false;
        }

        Conta conta = getConta();
        if (conta.getSaldo().compareTo(valor) < 0) {
            return false;
        }

        conta.setSaldo(conta.getSaldo().subtract(valor));
        this.totalGastoDia = this.totalGastoDia.add(valor);

        return true;
    }

    @Override
    public void aplicarTaxa() {
    }

    public void alterarLimiteDiario(BigDecimal novoLimite) {
        if (novoLimite.compareTo(BigDecimal.ZERO) > 0) {
            this.limiteDiario = novoLimite;
        }
    }

    @Override
    public void realizarCompra(BigDecimal valor) {

        if (!isAtivo()) {
            throw new RuntimeException("Cartão de débito inativo");
        }

        LocalDate hoje = LocalDate.now();
        if (!this.diaAtual.equals(hoje)) {
            this.diaAtual = hoje;
            this.totalGastoDia = BigDecimal.ZERO;
        }

        if (this.totalGastoDia.add(valor).compareTo(this.limiteDiario) > 0) {
            throw new RuntimeException("Limite diário excedido");
        }

        Conta conta = getConta();
        if (conta.getSaldo().compareTo(valor) < 0) {
            throw new RuntimeException("Saldo insuficiente");
        }

        conta.setSaldo(conta.getSaldo().subtract(valor));
        this.totalGastoDia = this.totalGastoDia.add(valor);
    }
}
