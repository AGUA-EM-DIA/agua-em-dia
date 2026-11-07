package br.com.aguaemdia.service;

import br.com.aguaemdia.common.Textos;
import br.com.aguaemdia.domain.Auditoria;
import br.com.aguaemdia.domain.HistoricoStatus;
import br.com.aguaemdia.repository.AuditoriaRepository;
import br.com.aguaemdia.repository.HistoricoStatusRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Trilha de auditoria (RF026). Nunca receba aqui senhas, hashes ou tokens (RNF010). */
@Service
public class AuditoriaService {

    private final AuditoriaRepository auditorias;
    private final HistoricoStatusRepository historicos;
    private final ObjectMapper mapper;

    public AuditoriaService(AuditoriaRepository auditorias, HistoricoStatusRepository historicos, ObjectMapper mapper) {
        this.auditorias = auditorias;
        this.historicos = historicos;
        this.mapper = mapper;
    }

    private String json(Object o) {
        if (o == null) return null;
        try {
            return mapper.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            return "{\"erro\":\"nao_serializavel\"}";
        }
    }

    @Transactional
    public void registrar(Long usuarioId, String acao, String entidadeTipo, Long entidadeId, Object antes, Object depois) {
        Auditoria a = new Auditoria();
        a.setUsuarioId(usuarioId);
        a.setAcao(acao);
        a.setEntidadeTipo(entidadeTipo);
        a.setEntidadeId(entidadeId);
        a.setDadosAnteriores(json(antes));
        a.setDadosNovos(json(depois));
        auditorias.save(a);
    }

    @Transactional
    public void statusAlterado(String entidadeTipo, Long entidadeId, String anterior, String novo, Long ator, String justificativa) {
        HistoricoStatus h = new HistoricoStatus();
        h.setEntidadeTipo(entidadeTipo);
        h.setEntidadeId(entidadeId);
        h.setStatusAnterior(anterior);
        h.setStatusNovo(novo);
        h.setAlteradoPor(ator);
        h.setJustificativa(Textos.truncar(justificativa, 1000));
        historicos.save(h);
    }

    @Transactional(readOnly = true)
    public Page<Auditoria> consultar(String tipo, Long entidadeId, Long usuarioId, Pageable pageable) {
        return auditorias.filtrar(tipo, entidadeId, usuarioId, pageable);
    }
}
