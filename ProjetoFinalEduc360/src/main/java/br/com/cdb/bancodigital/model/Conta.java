package br.com.cdb.bancodigital.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "contas")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_conta")
@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class Conta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String numeroConta;

    @NotNull
    @PositiveOrZero
    private BigDecimal saldo = BigDecimal.ZERO;

    @NotNull
    private LocalDateTime dataCriacao = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    @JsonBackReference
    private Cliente cliente;

    @OneToMany(mappedBy = "conta", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Cartao> cartoes = new ArrayList<>();

    private LocalDate dataAbertura;
    private boolean ativa = true;

    public void setDataAbertura(LocalDate dataAbertura) {
        this.dataAbertura = dataAbertura;
    }

    public void setAtiva(boolean ativa) {
        this.ativa = ativa;
    }

    public boolean isAtiva() {
        return ativa;
    }

    public abstract void aplicarTaxaMensal();

    public BigDecimal exibirSaldo() {
        return this.saldo;
    }

    public boolean transferirPix(Conta contaDestino, BigDecimal valor) {
        if (valor.compareTo(BigDecimal.ZERO) <= 0 || valor.compareTo(this.saldo) > 0) {
            return false;
        }

        this.saldo = this.saldo.subtract(valor);
        contaDestino.saldo = contaDestino.saldo.add(valor);
        return true;
    }

    public void adicionarCartao(Cartao cartao) {
        cartoes.add(cartao);
        cartao.setConta(this);
    }

    public void removerCartao(Cartao cartao) {
        cartoes.remove(cartao);
        cartao.setConta(null);
    }
}
