package br.com.aguaemdia.dto;

import java.util.List;
import org.springframework.data.domain.Page;

public record Pagina<T>(List<T> itens, int pagina, int tamanho, long total, int totalPaginas) {
    public static <T> Pagina<T> de(Page<T> p) {
        return new Pagina<>(p.getContent(), p.getNumber(), p.getSize(), p.getTotalElements(), p.getTotalPages());
    }
}
