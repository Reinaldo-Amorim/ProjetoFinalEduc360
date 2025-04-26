package br.com.cdb.bancodigital.model;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.CascadeType;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("CREDITO")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class CartaoCredito extends Cartao {

    @NotNull
    @PositiveOrZero
    private BigDecimal limiteCredito;

    @NotNull
    @PositiveOrZero
    private BigDecimal limiteDisponivel;

    @NotNull
    private YearMonth mesAtual = YearMonth.now();

    @NotNull
    @PositiveOrZero
    private BigDecimal totalGastoMesAtual = BigDecimal.ZERO;

    @OneToMany(mappedBy = "cartaoCredito", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Seguro> seguros = new ArrayList<>();

    @OneToOne(mappedBy = "cartaoCredito", cascade = CascadeType.ALL, orphanRemoval = true)
    private SeguroFraude seguroFraude;

    @NotNull
    @PositiveOrZero
    private BigDecimal faturaAtual = BigDecimal.ZERO;

    public CartaoCredito(Conta conta) {
        super();
        setConta(conta);

        switch (conta.getCliente().getCategoria()) {
            case COMUM:
                this.limiteCredito = new BigDecimal("1000.00");
                break;
            case SUPER:
                this.limiteCredito = new BigDecimal("5000.00");
                break;
            case PREMIUM:
                this.limiteCredito = new BigDecimal("10000.00");
                break;
            default:
                this.limiteCredito = new BigDecimal("1000.00");
        }

        this.limiteDisponivel = this.limiteCredito;

        this.seguroFraude = null;
    }

    public BigDecimal getFaturaAtual() {
        return this.faturaAtual;
    }

    public void setFaturaAtual(BigDecimal faturaAtual) {
        this.faturaAtual = faturaAtual;
    }

    public BigDecimal getLimite() {
        return this.limiteCredito;
    }

    public void setLimite(BigDecimal novoLimite) {
        if (novoLimite.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("O limite não pode ser negativo");
        }

        this.limiteCredito = novoLimite;

        this.limiteDisponivel = novoLimite;
    }

    public void configurarSeguroFraude() {
        if (this.seguroFraude == null && this.getId() != null) {
            this.seguroFraude = new SeguroFraude(this);
        }
    }

    @Override
    public boolean realizarPagamento(BigDecimal valor) {
        if (!isAtivo()) {
            return false;
        }

        if (valor.compareTo(limiteDisponivel) > 0) {
            return false;
        }

        YearMonth mesAtual = YearMonth.now();
        if (!this.mesAtual.equals(mesAtual)) {
            this.mesAtual = mesAtual;
            this.totalGastoMesAtual = BigDecimal.ZERO;
            this.limiteDisponivel = this.limiteCredito;
        }

        this.limiteDisponivel = this.limiteDisponivel.subtract(valor);
        this.totalGastoMesAtual = this.totalGastoMesAtual.add(valor);

        return true;
    }

    @Override
    public void aplicarTaxa() {
        BigDecimal oitentaPorCentoLimite = this.limiteCredito.multiply(new BigDecimal("0.8"));

        if (this.totalGastoMesAtual.compareTo(oitentaPorCentoLimite) > 0) {
            BigDecimal taxa = this.totalGastoMesAtual.multiply(new BigDecimal("0.05"));

            Conta conta = getConta();
            if (conta.getSaldo().compareTo(taxa) >= 0) {
                conta.setSaldo(conta.getSaldo().subtract(taxa));
            }
        }
    }

    @Override
    public void realizarCompra(BigDecimal valor) {
        if (valor.compareTo(this.limiteDisponivel) > 0) {
            throw new RuntimeException("Limite de crédito insuficiente");
        }

        YearMonth mesAtualNow = YearMonth.now();
        if (!this.mesAtual.equals(mesAtualNow)) {
            this.mesAtual = mesAtualNow;
            this.totalGastoMesAtual = BigDecimal.ZERO;
            this.limiteDisponivel = this.limiteCredito;
        }

        this.limiteDisponivel = this.limiteDisponivel.subtract(valor);
        this.totalGastoMesAtual = this.totalGastoMesAtual.add(valor);
        this.faturaAtual = this.faturaAtual.add(valor);
    }

    public void adicionarSeguro(Seguro seguro) {
        seguros.add(seguro);
        seguro.setCartaoCredito(this);
    }

    public void removerSeguro(Seguro seguro) {
        seguros.remove(seguro);
        seguro.setCartaoCredito(null);
    }
}