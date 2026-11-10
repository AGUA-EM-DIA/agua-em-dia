package br.com.aguaemdia.service;

import br.com.aguaemdia.common.ApiException;
import br.com.aguaemdia.common.Textos;
import br.com.aguaemdia.domain.Bairro;
import br.com.aguaemdia.domain.Localizacao;
import br.com.aguaemdia.repository.BairroRepository;
import br.com.aguaemdia.repository.LocalizacaoRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Set;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LocalizacaoService {

    private static final Set<String> ORIGENS = Set.of("GPS", "ENDERECO", "MAPA");

    private final LocalizacaoRepository localizacoes;
    private final BairroRepository bairros;
    private final ParametroService parametros;
    private final NamedParameterJdbcTemplate jdbc;

    public LocalizacaoService(LocalizacaoRepository localizacoes, BairroRepository bairros,
            ParametroService parametros, NamedParameterJdbcTemplate jdbc) {
        this.localizacoes = localizacoes;
        this.bairros = bairros;
        this.parametros = parametros;
        this.jdbc = jdbc;
    }

    /** RN-AD02: coordenadas obrigatórias e dentro da caixa geográfica configurada. */
    public void validarCoordenadas(BigDecimal lat, BigDecimal lon) {
        if (lat == null || lon == null) throw ApiException.negocio("Latitude e longitude são obrigatórias.");
        if (lat.compareTo(parametros.decimal("geo.latitude_minima")) < 0 || lat.compareTo(parametros.decimal("geo.latitude_maxima")) > 0
                || lon.compareTo(parametros.decimal("geo.longitude_minima")) < 0 || lon.compareTo(parametros.decimal("geo.longitude_maxima")) > 0) {
            throw ApiException.negocio("As coordenadas estão fora da área atendida.");
        }
    }

    @Transactional
    public Localizacao criar(BigDecimal latitude, BigDecimal longitude, String endereco, String bairroNome, String origem) {
        validarCoordenadas(latitude, longitude);
        if (origem == null || !ORIGENS.contains(origem)) {
            throw ApiException.invalido("Origem da localização inválida (use GPS, ENDERECO ou MAPA).");
        }
        BigDecimal lat = latitude.setScale(6, RoundingMode.HALF_UP);
        BigDecimal lon = longitude.setScale(6, RoundingMode.HALF_UP);

        Localizacao l = new Localizacao();
        l.setLatitude(lat);
        l.setLongitude(lon);
        l.setEndereco(Textos.truncar(Textos.limpar(endereco), 300));
        l.setOrigemLocalizacao(origem);
        String nomeBairro = Textos.limpar(bairroNome);
        if (nomeBairro != null) l.setBairroId(bairroOuCria(nomeBairro).getId());
        l = localizacoes.saveAndFlush(l);

        // Coluna espacial: preenchida via SQL porque o JPA não mapeia GEOGRAPHY. O "::" é tratado pelo Spring JDBC.
        jdbc.update("UPDATE Localizacao SET geo = geography::Point(:lat, :lon, 4326) WHERE id = :id",
                new MapSqlParameterSource().addValue("lat", lat.doubleValue()).addValue("lon", lon.doubleValue())
                        .addValue("id", l.getId()));
        return l;
    }

    private Bairro bairroOuCria(String nome) {
        String normalizado = Textos.normalizar(nome);
        return bairros.findByNomeNormalizado(normalizado).orElseGet(() -> {
            Bairro b = new Bairro();
            b.setNome(Textos.truncar(nome, 120));
            b.setNomeNormalizado(Textos.truncar(normalizado, 120));
            b.setAtivo(Boolean.TRUE);
            return bairros.save(b);
        });
    }
}
