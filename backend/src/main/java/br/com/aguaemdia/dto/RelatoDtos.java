package br.com.aguaemdia.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

public final class RelatoDtos {
    private RelatoDtos() {}

    public record RelatoRequest(
            @NotNull Long categoriaId,
            @NotNull Long porteId,
            Long infraestruturaId,
            @NotNull @DecimalMin("-90.0") @DecimalMax("90.0") BigDecimal latitude,
            @NotNull @DecimalMin("-180.0") @DecimalMax("180.0") BigDecimal longitude,
            @NotBlank String origemLocalizacao,
            @Size(max = 300) String endereco,
            @Size(max = 120) String bairro,
            @Size(max = 2000) String descricao,
            Long rascunhoId) {}

    public record EditarDescricaoRequest(@Size(max = 2000) String descricao, @Size(max = 500) String motivo) {}

    public record RascunhoRequest(@NotNull Map<String, Object> dados) {}

    public record RascunhoResponse(Long id, Map<String, Object> dados, OffsetDateTime criadoEm, OffsetDateTime atualizadoEm) {}

    public record RelatoResumo(Long id, String protocolo, OffsetDateTime enviadoEm, String statusOcorrencia,
            String categoria, String porte, String bairro, String endereco, BigDecimal volumeEstimadoLitros,
            boolean volumeIncerto) {}

    public record AtendimentoResumo(Long id, String status, OffsetDateTime criadoEm, OffsetDateTime encerradoEm,
            boolean podeAvaliar, boolean jaAvaliado) {}

    public record EventoLinhaDoTempo(String status, OffsetDateTime em) {}

    public record RelatoDetalhe(Long id, String protocolo, OffsetDateTime enviadoEm, Long ocorrenciaId,
            String statusOcorrencia, String categoria, String porte, String infraestrutura, String descricao,
            BigDecimal latitude, BigDecimal longitude, String endereco, String bairro,
            BigDecimal volumeEstimadoLitros, boolean volumeIncerto, boolean estimativa,
            List<Long> imagens, List<AtendimentoResumo> atendimentos, List<EventoLinhaDoTempo> linhaDoTempo) {}

    public record CompartilhamentoResponse(String texto, String urlWhatsapp) {}
}
