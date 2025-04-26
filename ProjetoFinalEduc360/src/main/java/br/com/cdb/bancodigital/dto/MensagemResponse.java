package br.com.cdb.bancodigital.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MensagemResponse {
    private String titulo;
    private String mensagem;
}