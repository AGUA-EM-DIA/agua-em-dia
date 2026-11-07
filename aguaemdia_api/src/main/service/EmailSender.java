package br.com.aguaemdia.service;

public interface EmailSender {
    void enviar(String para, String assunto, String corpo);
}
