package br.com.cdb.bancodigital.dto;

import java.time.LocalDate;
import br.com.cdb.bancodigital.model.Cliente.CategoriaCliente;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClienteDtoResponse {
    private Long id;
    private String cpf;
    private String nome;
    private LocalDate dataNascimento;
    private String rua;
    private String numero;
    private String complemento;
    private String cidade;
    private String estado;
    private String cep;
    private CategoriaCliente categoria;

    @Override
    public String toString() {
        return "Cliente{" +
                "id=" + id +
                ", cpf='" + cpf + '\'' +
                ", nome='" + nome + '\'' +
                ", cidade='" + cidade + '\'' +
                ", estado='" + estado + '\'' +
                ", categoria=" + categoria +
                '}';
    }
}
