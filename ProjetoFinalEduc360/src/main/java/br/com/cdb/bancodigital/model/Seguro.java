package br.com.cdb.bancodigital.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "seguros")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_seguro")
@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class Seguro {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true)
    private String numeroApolice;
    
    @NotNull
    private LocalDate dataContratacao = LocalDate.now();
    
    @NotNull
    private LocalDate dataInicio = LocalDate.now();
    
    @NotNull
    private LocalDate dataFim = LocalDate.now().plusYears(1);
    
    @NotNull
    @PositiveOrZero
    private BigDecimal valorApolice;
    
    @NotNull
    @PositiveOrZero
    private BigDecimal valorPremio = BigDecimal.ZERO;
    
    @NotBlank
    private String descricaoCobertura;
    
    @ManyToOne
    @JoinColumn(name = "cartao_credito_id")
    @JsonBackReference
    private CartaoCredito cartaoCredito;
    
    private boolean ativo = true;
    
    public String getTipoSeguro() {
        return this.getClass().getSimpleName();
    }
    
    public Cartao getCartao() {
        return this.cartaoCredito;
    }

    public abstract void gerarNumeroApolice();
}
