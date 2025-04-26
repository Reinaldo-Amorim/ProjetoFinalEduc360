package br.com.cdb.bancodigital.model;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("POUPANCA")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class ContaPoupanca extends Conta {

    private static final BigDecimal TAXA_RENDIMENTO_COMUM = new BigDecimal("0.005"); // 0.5% ao ano
    private static final BigDecimal TAXA_RENDIMENTO_SUPER = new BigDecimal("0.007"); // 0.7% ao ano
    private static final BigDecimal TAXA_RENDIMENTO_PREMIUM = new BigDecimal("0.009"); // 0.9% ao ano

    @Override
    public void aplicarTaxaMensal() {
        BigDecimal taxaRendimentoAnual;

        switch (getCliente().getCategoria()) {
            case COMUM:
                taxaRendimentoAnual = TAXA_RENDIMENTO_COMUM;
                break;
            case SUPER:
                taxaRendimentoAnual = TAXA_RENDIMENTO_SUPER;
                break;
            case PREMIUM:
                taxaRendimentoAnual = TAXA_RENDIMENTO_PREMIUM;
                break;
            default:
                taxaRendimentoAnual = TAXA_RENDIMENTO_COMUM;
        }

        BigDecimal taxaMensal = BigDecimal.ONE.add(taxaRendimentoAnual)
                .pow(1, new MathContext(10))
                .multiply(new BigDecimal("0.0833333"))
                .subtract(BigDecimal.ONE)
                .setScale(6, RoundingMode.HALF_UP);

        BigDecimal rendimento = getSaldo().multiply(taxaMensal).setScale(2, RoundingMode.HALF_UP);
        setSaldo(getSaldo().add(rendimento));
    }

    public void aplicarRendimento() {
        BigDecimal taxaRendimentoAnual;

        switch (getCliente().getCategoria()) {
            case COMUM:
                taxaRendimentoAnual = TAXA_RENDIMENTO_COMUM;
                break;
            case SUPER:
                taxaRendimentoAnual = TAXA_RENDIMENTO_SUPER;
                break;
            case PREMIUM:
                taxaRendimentoAnual = TAXA_RENDIMENTO_PREMIUM;
                break;
            default:
                taxaRendimentoAnual = TAXA_RENDIMENTO_COMUM;
        }

        BigDecimal taxaMensal = BigDecimal.ONE.add(taxaRendimentoAnual)
                .pow(1, new MathContext(10))
                .multiply(new BigDecimal("0.0833333"))
                .subtract(BigDecimal.ONE)
                .setScale(6, RoundingMode.HALF_UP);

        BigDecimal rendimento = getSaldo().multiply(taxaMensal).setScale(2, RoundingMode.HALF_UP);
        setSaldo(getSaldo().add(rendimento));
    }
}
