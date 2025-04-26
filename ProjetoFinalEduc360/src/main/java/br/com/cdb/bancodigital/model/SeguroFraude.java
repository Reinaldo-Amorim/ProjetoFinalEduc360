
package br.com.cdb.bancodigital.model;

import java.math.BigDecimal;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("FRAUDE")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class SeguroFraude extends Seguro {

    private static final BigDecimal VALOR_APOLICE_BASE = new BigDecimal("5000.00");
    private static final String DESCRICAO_COBERTURA = "Cobertura para transações não autorizadas realizadas com o cartão em caso de "
            +
            "roubo, furto, perda ou fraude, desde que o titular do cartão tenha comunicado " +
            "o banco imediatamente após o ocorrido.";

    @OneToOne
    private CartaoCredito cartaoCredito;

    public SeguroFraude(CartaoCredito cartaoCredito) {
        super();
        this.cartaoCredito = cartaoCredito;
        setValorApolice(VALOR_APOLICE_BASE);
        setDescricaoCobertura(DESCRICAO_COBERTURA);

        if (cartaoCredito != null && cartaoCredito.getId() != null) {
            gerarNumeroApolice();
        }
    }

    @Override
    public void setCartaoCredito(CartaoCredito cartaoCredito) {
        this.cartaoCredito = cartaoCredito;

        if (cartaoCredito != null && cartaoCredito.getId() != null) {
            gerarNumeroApolice();
        }
    }

    @Override
    public void gerarNumeroApolice() {
        if (this.cartaoCredito != null && this.cartaoCredito.getId() != null) {
            this.setNumeroApolice(String.format("%d%02d%02d%d%d",
                    java.time.LocalDate.now().getYear(),
                    java.time.LocalDate.now().getMonthValue(),
                    java.time.LocalDate.now().getDayOfMonth(),
                    this.cartaoCredito.getId(),
                    System.currentTimeMillis() % 10000));
        } else {
            this.setNumeroApolice("TEMP-" + System.currentTimeMillis());
        }
    }
}