package br.com.aguaemdia.service;

import br.com.aguaemdia.common.ApiException;
import br.com.aguaemdia.config.AppProperties;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import org.springframework.stereotype.Service;

/**
 * Armazenamento em disco local. Troque por S3/Azure Blob criando outra implementação com os mesmos três métodos:
 * o resto do sistema só enxerga "caminho relativo".
 */
@Service
public class ArmazenamentoService {

    private final Path raiz;

    public ArmazenamentoService(AppProperties props) {
        this.raiz = Path.of(props.armazenamento().diretorio()).toAbsolutePath().normalize();
    }

    /** Grava com nome aleatório (nunca usa o nome enviado pelo usuário) e devolve o caminho relativo. */
    public String salvar(String subpasta, byte[] conteudo, String extensao) {
        try {
            Path dir = raiz.resolve(subpasta).normalize();
            if (!dir.startsWith(raiz)) throw ApiException.invalido("Caminho inválido.");
            Files.createDirectories(dir);
            String nome = UUID.randomUUID() + "." + extensao;
            Path destino = dir.resolve(nome);
            Path temp = Files.createTempFile(dir, "up-", ".tmp");
            Files.write(temp, conteudo);
            Files.move(temp, destino, StandardCopyOption.ATOMIC_MOVE);
            return raiz.relativize(destino).toString().replace('\\', '/');
        } catch (IOException e) {
            throw new IllegalStateException("Falha ao gravar arquivo", e);
        }
    }

    public byte[] ler(String caminhoRelativo) {
        try {
            Path p = raiz.resolve(caminhoRelativo).normalize();
            if (!p.startsWith(raiz)) throw ApiException.invalido("Caminho inválido.");
            return Files.readAllBytes(p);
        } catch (IOException e) {
            throw ApiException.naoEncontrado("Arquivo não encontrado.");
        }
    }

    public void apagar(String caminhoRelativo) {
        try {
            Path p = raiz.resolve(caminhoRelativo).normalize();
            if (p.startsWith(raiz)) Files.deleteIfExists(p);
        } catch (IOException ignorado) {
            // limpeza best-effort
        }
    }
}
