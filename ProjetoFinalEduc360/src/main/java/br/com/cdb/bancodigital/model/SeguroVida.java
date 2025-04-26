package br.com.cdb.bancodigital.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("VIDA")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class SeguroVida extends Seguro {

    private static final BigDecimal VALOR_PREMIO_MENSAL = new BigDecimal("100.00");
    private static final BigDecimal VALOR_APOLICE_BASE = new BigDecimal("200000.00");
    private static final String DESCRICAO_COBERTURA = "Cobertura para morte natural ou acidental, invalidez permanente total ou parcial "
            +
            "por acidente, e assistência funeral familiar. Benefícios adicionais incluem " +
            "antecipação em caso de doenças terminais.";

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    public SeguroVida(Cliente cliente) {
        super();
        this.cliente = cliente;
        setDataContratacao(LocalDate.now());
        setDataInicio(LocalDate.now().plusDays(30));
        setDataFim(LocalDate.now().plusYears(1));
        setAtivo(true);
        setValorApolice(VALOR_APOLICE_BASE);

        if (cliente.getCategoria() == Cliente.CategoriaCliente.PREMIUM) {
            setValorPremio(VALOR_PREMIO_MENSAL.multiply(new BigDecimal("0.8")));
        } else {
            setValorPremio(VALOR_PREMIO_MENSAL);
        }

        setDescricaoCobertura(DESCRICAO_COBERTURA);
        gerarNumeroApolice();
    }

    @Override
    public void gerarNumeroApolice() {
        if (this.cliente != null && this.cliente.getId() != null) {
            setNumeroApolice(String.format("SV%d%02d%02d%d%d",
                    LocalDate.now().getYear(),
                    LocalDate.now().getMonthValue(),
                    LocalDate.now().getDayOfMonth(),
                    this.cliente.getId(),
                    System.currentTimeMillis() % 10000));
        } else {
            setNumeroApolice("SV-TEMP-" + System.currentTimeMillis());
        }
    }

    public Cliente getCliente() {
        return this.cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
        if (cliente != null && cliente.getId() != null) {
            gerarNumeroApolice();
        }
    }
}