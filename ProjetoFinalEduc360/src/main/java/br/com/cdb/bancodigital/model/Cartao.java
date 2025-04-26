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
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "cartoes")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_cartao")
@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class Cartao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String numeroCartao;

    @NotNull
    private String nomeTitular;

    @NotNull
    private LocalDate dataValidade;

    @NotNull
    private String senha;

    @NotNull
    private boolean ativo = false;

    @ManyToOne
    @JoinColumn(name = "conta_id")
    @JsonBackReference
    private Conta conta;

    @Column
    private LocalDate validade;

    @Column
    private String cvv;

    public abstract boolean realizarPagamento(BigDecimal valor);

    public abstract void realizarCompra(BigDecimal valor);

    public void alterarSenha(String novaSenha) {
        this.senha = novaSenha;
    }

    public void alterarStatus(boolean ativo) {
        this.ativo = ativo;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public abstract void aplicarTaxa();

    public void setValidade(LocalDate validade) {
        this.validade = validade;
        this.dataValidade = validade;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    public void desbloquearCartao() {
        this.ativo = true;
    }

    public void bloquearCartao() {
        this.ativo = false;
    }
}
