package br.com.aguaemdia.service;

import br.com.aguaemdia.common.ApiException;
import br.com.aguaemdia.domain.ParametroSistema;
import br.com.aguaemdia.repository.ParametroSistemaRepository;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Parâmetros ajustáveis em runtime (RF028), com cache curto para não consultar o banco a cada cálculo. */
@Service
public class ParametroService {

    private static final long TTL_MS = 30_000;

    private record Item(String valor, long expiraEm) {}

    private final ParametroSistemaRepository repo;
    private final AuditoriaService auditoria;
    private final Clock clock;
    private final Map<String, Item> cache = new ConcurrentHashMap<>();

    public ParametroService(ParametroSistemaRepository repo, AuditoriaService auditoria, Clock clock) {
        this.repo = repo;
        this.auditoria = auditoria;
        this.clock = clock;
    }

    public String texto(String chave) {
        Item item = cache.get(chave);
        long agora = System.currentTimeMillis();
        if (item != null && item.expiraEm() > agora) return item.valor();
        String valor = repo.findByChave(chave)
                .orElseThrow(() -> new IllegalStateException("Parâmetro ausente: " + chave)).getValor();
        cache.put(chave, new Item(valor, agora + TTL_MS));
        return valor;
    }

    public int inteiro(String chave) { return Integer.parseInt(texto(chave).trim()); }
    public long longo(String chave) { return Long.parseLong(texto(chave).trim()); }
    public double duplo(String chave) { return Double.parseDouble(texto(chave).trim()); }
    public BigDecimal decimal(String chave) { return new BigDecimal(texto(chave).trim()); }

    @Transactional(readOnly = true)
    public List<ParametroSistema> listar() { return repo.findAllByOrderByChave(); }

    @Transactional
    public ParametroSistema atualizar(String chave, String valor, Long ator) {
        ParametroSistema p = repo.findByChave(chave)
                .orElseThrow(() -> ApiException.naoEncontrado("Parâmetro não encontrado: " + chave));
        String novo = valor == null ? "" : valor.trim();
        if (novo.isEmpty()) throw ApiException.invalido("O valor do parâmetro não pode ser vazio.");
        if (pareceNumero(p.getValor()) && !pareceNumero(novo)) {
            throw ApiException.invalido("O parâmetro " + chave + " deve ser numérico.");
        }
        String anterior = p.getValor();
        p.setValor(novo);
        p.setAtualizadoPor(ator);
        p.setAtualizadoEm(OffsetDateTime.now(clock));
        repo.save(p);
        cache.remove(chave);
        auditoria.registrar(ator, "PARAMETRO_ALTERADO", "ParametroSistema", p.getId(),
                Map.of("chave", chave, "valor", anterior), Map.of("chave", chave, "valor", novo));
        return p;
    }

    private static boolean pareceNumero(String s) {
        try { new BigDecimal(s.trim()); return true; } catch (RuntimeException e) { return false; }
    }
}
