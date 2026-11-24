package br.com.aguaemdia.agua_em_dia_api.service;

import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Aquari, o assistente do app. Nesta versão ele responde perguntas frequentes por palavras-chave.
 * É o ponto onde uma IA pode entrar depois: bastaria chamar o modelo aqui quando nenhuma resposta fixa servir.
 * Áudio e foto ainda não são recebidos; a identificação por foto já existe em /api/ia/identificar.
 */
@Service
public class AquariService {

    private static final String NAO_ENTENDI = "Ainda não sei responder isso. Posso ajudar a registrar um vazamento, "
            + "explicar os tamanhos, a estimativa de perda, a avaliação ou como acompanhar seus registros.";

    /** Cada resposta tem as palavras-chave que a ativam (sem acento, em minúsculas). */
    private final Map<String[], String> respostas = new LinkedHashMap<>();

    public AquariService() {
        respostas.put(new String[]{"registrar", "informar", "avisar", "denunciar", "reportar", "como faco"},
                "Para registrar: toque em Registrar, tire ou escolha uma foto (se quiser), confirme o tipo e o tamanho, "
                        + "marque o local no mapa e envie. Você recebe um protocolo para acompanhar.");
        respostas.put(new String[]{"tamanho", "porte", "jato", "gotejamento"},
                "Os tamanhos vão de gotejamento (cerca de 3 litros por hora) a jorro (cerca de 9.000 litros por hora). "
                        + "Escolha o que mais se parece com o que você está vendo.");
        respostas.put(new String[]{"litros", "perda", "desperdicio", "estimativa", "quanto"},
                "A perda estimada é a vazão do tamanho escolhido, ajustada pelo tipo de vazamento, vezes o tempo em que ele "
                        + "ficou aberto. É uma estimativa, não uma medição.");
        respostas.put(new String[]{"avaliar", "avaliacao", "nota"},
                "Quando o reparo é concluído, o registro libera a avaliação: uma nota de 1 a 5 e um comentário opcional. "
                        + "Cada registro recebe uma avaliação.");
        respostas.put(new String[]{"status", "situacao", "acompanhar", "protocolo", "andamento"},
                "Cada registro passa por três situações: aberto, em reparo e resolvido. Você vê o andamento em Meus registros, "
                        + "e o app avisa quando a situação muda.");
        respostas.put(new String[]{"editar", "corrigir", "mudar descricao"},
                "Você pode editar a descrição dos seus próprios registros. As outras informações não mudam depois do envio.");
        respostas.put(new String[]{"dados", "privacidade", "lgpd", "cpf"},
                "Os outros cidadãos veem só o seu primeiro nome. O CPF é usado apenas no cadastro e nunca aparece para ninguém.");
        respostas.put(new String[]{"senha", "esqueci", "recuperar"},
                "Na tela de entrada, toque em Esqueci a senha e informe seu e-mail. Enviamos um código de 6 dígitos para criar uma nova senha.");
    }

    public String responder(String texto) {
        String pergunta = semAcento(texto);
        for (Map.Entry<String[], String> item : respostas.entrySet()) {
            for (String palavra : item.getKey()) {
                if (pergunta.contains(palavra)) {
                    return item.getValue();
                }
            }
        }
        return NAO_ENTENDI;
    }

    private static String semAcento(String texto) {
        String base = Normalizer.normalize(texto == null ? "" : texto, Normalizer.Form.NFD);
        return base.replaceAll("\\p{M}", "").toLowerCase();
    }
}
