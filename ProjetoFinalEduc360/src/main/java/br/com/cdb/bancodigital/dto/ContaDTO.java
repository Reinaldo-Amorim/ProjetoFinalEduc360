package br.com.cdb.bancodigital.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ContaDTO {
    private Long id;
    private String numeroConta;
    private BigDecimal saldo;

    @NotNull(message = "Cliente é obrigatório")
    private Long clienteId;

    private LocalDate dataAbertura;
    private boolean ativa = true;
}