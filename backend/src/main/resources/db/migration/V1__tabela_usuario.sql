-- V1: tabela de usuários (cidadãos), exatamente como o Hibernate a criava antes do Flyway.
-- "IF NOT EXISTS": se o seu banco já tem a tabela, nada é alterado.
CREATE TABLE IF NOT EXISTS usuario (
    id           BIGINT       NOT NULL AUTO_INCREMENT,
    nome         VARCHAR(150) NOT NULL,
    nome_usuario VARCHAR(150) NOT NULL,
    cpf          VARCHAR(14)  NOT NULL,
    email        VARCHAR(150) NOT NULL,
    telefone     VARCHAR(20)  NOT NULL,
    senha        VARCHAR(255) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_usuario_nome_usuario (nome_usuario),
    UNIQUE KEY uk_usuario_cpf (cpf),
    UNIQUE KEY uk_usuario_email (email)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;
