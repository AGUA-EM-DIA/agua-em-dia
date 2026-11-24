package br.com.aguaemdia.agua_em_dia_api.service;

import br.com.aguaemdia.agua_em_dia_api.exception.NegocioException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * Valida e guarda as fotos dos vazamentos em uma pasta do servidor (app.uploads.dir).
 * No banco fica apenas o endereço da foto (/uploads/arquivo.jpg). O nome do arquivo é gerado aqui,
 * nunca é o nome que veio do cidadão.
 */
@Service
public class ArmazenamentoImagemService {

    public static final long TAMANHO_MAXIMO_BYTES = 5L * 1024 * 1024;
    private static final String PREFIXO_URL = "/uploads/";

    /** Imagem já conferida: os bytes e o tipo real, descoberto pelo conteúdo. */
    public record Imagem(byte[] bytes, TipoImagem tipo) {
    }

    private final Path pasta;

    public ArmazenamentoImagemService(@Value("${app.uploads.dir:uploads}") String diretorio) {
        this.pasta = Paths.get(diretorio).toAbsolutePath().normalize();
        try {
            Files.createDirectories(pasta);
        } catch (IOException e) {
            throw new UncheckedIOException("Não consegui criar a pasta de fotos: " + pasta, e);
        }
    }

    public Path getPasta() {
        return pasta;
    }

    /** Confere tamanho e tipo (JPEG, PNG ou WebP). Lança erro 422 se a foto não for aceita. */
    public Imagem validar(MultipartFile arquivo) {
        if (arquivo == null || arquivo.isEmpty()) {
            throw NegocioException.naoProcessavel("A foto enviada está vazia.");
        }
        if (arquivo.getSize() > TAMANHO_MAXIMO_BYTES) {
            throw NegocioException.naoProcessavel("A foto é grande demais. O limite é de 5 MB.");
        }
        byte[] bytes;
        try {
            bytes = arquivo.getBytes();
        } catch (IOException e) {
            throw new UncheckedIOException("Não consegui ler a foto enviada.", e);
        }
        TipoImagem tipo = TipoImagem.detectar(bytes)
                .orElseThrow(() -> NegocioException.naoProcessavel("Envie uma foto nos formatos JPEG, PNG ou WebP."));
        return new Imagem(bytes, tipo);
    }

    /** Grava a imagem e devolve o endereço público (ex.: /uploads/3f2a....jpg). */
    public String salvar(Imagem imagem) {
        String nome = UUID.randomUUID() + "." + imagem.tipo().getExtensao();
        try {
            Files.write(pasta.resolve(nome), imagem.bytes());
        } catch (IOException e) {
            throw new UncheckedIOException("Não consegui gravar a foto.", e);
        }
        return PREFIXO_URL + nome;
    }

    /** Apaga uma foto já gravada (usado quando o registro do vazamento falha depois do upload). */
    public void remover(String url) {
        if (url == null || !url.startsWith(PREFIXO_URL)) {
            return;
        }
        Path arquivo = pasta.resolve(url.substring(PREFIXO_URL.length())).normalize();
        if (!arquivo.startsWith(pasta)) {
            return;
        }
        try {
            Files.deleteIfExists(arquivo);
        } catch (IOException e) {
            // Sobrar um arquivo solto não é grave; não vale derrubar a requisição por isso.
        }
    }
}
