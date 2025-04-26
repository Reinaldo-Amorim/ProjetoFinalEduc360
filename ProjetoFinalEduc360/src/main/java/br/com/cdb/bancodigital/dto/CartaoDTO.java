package br.com.cdb.bancodigital.dto;

import java.time.LocalDate;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CartaoDTO {
    private Long id;

    @NotBlank(message = "Número do cartão é obrigatório")
    private String numeroCartao;

    @NotBlank(message = "Nome do titular é obrigatório")
    private String nomeTitular;

    @NotNull(message = "Data de validade é obrigatória")
    private LocalDate dataValidade;

    @NotBlank(message = "Senha é obrigatória")
    private String senha;

    @NotBlank(message = "CVV é obrigatório")
    private String cvv;

    private boolean ativo = true;
    private Long contaId;
}
