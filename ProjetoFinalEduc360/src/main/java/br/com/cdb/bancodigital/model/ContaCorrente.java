package br.com.cdb.bancodigital.model;

import java.math.BigDecimal;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("CORRENTE")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class ContaCorrente extends Conta {

    private static final BigDecimal TAXA_MANUTENCAO_COMUM = new BigDecimal("12.00");
    private static final BigDecimal TAXA_MANUTENCAO_SUPER = new BigDecimal("8.00");
    private static final BigDecimal TAXA_MANUTENCAO_PREMIUM = BigDecimal.ZERO;

    @Override
    public void aplicarTaxaMensal() {
        BigDecimal taxaManutencao;

        switch (getCliente().getCategoria()) {
            case COMUM:
                taxaManutencao = TAXA_MANUTENCAO_COMUM;
                break;
            case SUPER:
                taxaManutencao = TAXA_MANUTENCAO_SUPER;
                break;
            case PREMIUM:
                taxaManutencao = TAXA_MANUTENCAO_PREMIUM;
                break;
            default:
                taxaManutencao = TAXA_MANUTENCAO_COMUM;
        }

        if (getSaldo().compareTo(taxaManutencao) >= 0) {
            setSaldo(getSaldo().subtract(taxaManutencao));
        }
    }
}