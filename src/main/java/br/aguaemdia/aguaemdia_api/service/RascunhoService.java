package br.com.aguaemdia.service;

import br.com.aguaemdia.common.ApiException;
import br.com.aguaemdia.domain.RascunhoRelato;
import br.com.aguaemdia.dto.RelatoDtos.RascunhoResponse;
import br.com.aguaemdia.repository.RascunhoRelatoRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** RF007: rascunho é só dados parciais do formulário — não gera ocorrência, protocolo nem estimativa. */
@Service
public class RascunhoService {

    private static final int LIMITE_JSON = 20_000;

    private final RascunhoRelatoRepository rascunhos;
    private final ObjectMapper mapper;
    private final Clock clock;

    public RascunhoService(RascunhoRelatoRepository rascunhos, ObjectMapper mapper, Clock clock) {
        this.rascunhos = rascunhos;
        this.mapper = mapper;
        this.clock = clock;
    }

    @Transactional
    public RascunhoResponse salvar(Long usuarioId, Long id, Map<String, Object> dados) {
        String json;
        try {
            json = mapper.writeValueAsString(dados);
        } catch (JsonProcessingException e) {
            throw ApiException.invalido("Dados do rascunho inválidos.");
        }
        if (json.length() > LIMITE_JSON) throw ApiException.invalido("Rascunho grande demais.");
        RascunhoRelato r = id == null ? new RascunhoRelato()
                : rascunhos.findByIdAndUsuarioId(id, usuarioId).orElseThrow(() -> ApiException.naoEncontrado("Rascunho não encontrado."));
        r.setUsuarioId(usuarioId);
        r.setDadosParciais(json);
        r.setAtualizadoEm(OffsetDateTime.now(clock));
        return paraResposta(rascunhos.save(r));
    }

    @Transactional(readOnly = true)
    public List<RascunhoResponse> listar(Long usuarioId) {
        return rascunhos.findByUsuarioIdOrderByAtualizadoEmDesc(usuarioId).stream().map(this::paraResposta).toList();
    }

    @Transactional(readOnly = true)
    public RascunhoResponse obter(Long usuarioId, Long id) {
        return paraResposta(rascunhos.findByIdAndUsuarioId(id, usuarioId)
                .orElseThrow(() -> ApiException.naoEncontrado("Rascunho não encontrado.")));
    }

    @Transactional
    public void excluir(Long usuarioId, Long id) {
        rascunhos.delete(rascunhos.findByIdAndUsuarioId(id, usuarioId)
                .orElseThrow(() -> ApiException.naoEncontrado("Rascunho não encontrado.")));
    }

    private RascunhoResponse paraResposta(RascunhoRelato r) {
        Map<String, Object> dados;
        try {
            dados = mapper.readValue(r.getDadosParciais(), new TypeReference<Map<String, Object>>() {});
        } catch (JsonProcessingException e) {
            dados = Map.of();
        }
        return new RascunhoResponse(r.getId(), dados, r.getCriadoEm(), r.getAtualizadoEm());
    }
}
