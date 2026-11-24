package br.com.aguaemdia.agua_em_dia_api.dto;

import br.com.aguaemdia.agua_em_dia_api.entity.PorteVazamento;
import br.com.aguaemdia.agua_em_dia_api.entity.TipoVazamento;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/**
 * Dados enviados pelo cidadão para registrar um vazamento. O dono é sempre o usuário autenticado:
 * o frontend não envia (e não pode escolher) o id do autor.
 */
public record VazamentoCadastroDTO(
        @NotNull(message = "Escolha o tipo do vazamento.") TipoVazamento tipo,
        @NotNull(message = "Escolha o tamanho do vazamento.") PorteVazamento porte,
        @Size(max = 1000, message = "A descrição pode ter até 1000 caracteres.") String descricao,
        @Size(max = 255, message = "O endereço pode ter até 255 caracteres.") String endereco,
        @NotNull(message = "Informe a latitude.")
        @DecimalMin(value = "-90.0", message = "Latitude inválida.")
        @DecimalMax(value = "90.0", message = "Latitude inválida.") BigDecimal latitude,
        @NotNull(message = "Informe a longitude.")
        @DecimalMin(value = "-180.0", message = "Longitude inválida.")
        @DecimalMax(value = "180.0", message = "Longitude inválida.") BigDecimal longitude) {
}
