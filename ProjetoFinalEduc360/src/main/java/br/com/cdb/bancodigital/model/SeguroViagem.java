package br.com.cdb.bancodigital.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("VIAGEM")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class SeguroViagem extends Seguro {

    private static final BigDecimal VALOR_PREMIO_MENSAL = new BigDecimal("50.00");
    private static final BigDecimal VALOR_APOLICE_BASE = new BigDecimal("100000.00");
    private static final String DESCRICAO_COBERTURA = "Cobertura para despesas médicas, hospitalares e odontológicas em viagem, "
            +
            "extravio de bagagem, cancelamento de viagem, invalidez permanente total ou parcial " +
            "por acidente em viagem, e morte acidental em viagem.";

    public SeguroViagem(CartaoCredito cartaoCredito) {
        super();
        setCartaoCredito(cartaoCredito);
        setDataContratacao(LocalDate.now());
        setDataInicio(LocalDate.now().plusDays(7));
        setDataFim(LocalDate.now().plusDays(37));
        setAtivo(true);
        setValorApolice(VALOR_APOLICE_BASE);

        if (cartaoCredito.getConta().getCliente().getCategoria() == Cliente.CategoriaCliente.PREMIUM) {
            setValorPremio(BigDecimal.ZERO);
        } else {
            setValorPremio(VALOR_PREMIO_MENSAL);
        }

        setDescricaoCobertura(DESCRICAO_COBERTURA);
        gerarNumeroApolice();
    }

    @Override
    public void gerarNumeroApolice() {
        CartaoCredito cartaoCredito = (CartaoCredito) getCartao();
        if (cartaoCredito != null && cartaoCredito.getId() != null) {
            setNumeroApolice(String.format("SV%d%02d%02d%d%d",
                    LocalDate.now().getYear(),
                    LocalDate.now().getMonthValue(),
                    LocalDate.now().getDayOfMonth(),
                    cartaoCredito.getId(),
                    System.currentTimeMillis() % 10000));
        } else {
            setNumeroApolice("SV-TEMP-" + System.currentTimeMillis());
        }
    }
}
