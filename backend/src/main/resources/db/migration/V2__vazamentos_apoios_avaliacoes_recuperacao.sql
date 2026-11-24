-- V2: vazamentos, reforços ("eu também vi"), avaliações e recuperação de senha.
-- Datas em UTC. Identificadores BIGINT com AUTO_INCREMENT. Nada é apagado nem recriado.

-- Novas informações do cidadão: data do cadastro e registro do aceite dos termos (LGPD)
ALTER TABLE usuario
    ADD COLUMN data_cadastro DATETIME(6) NOT NULL DEFAULT (UTC_TIMESTAMP(6)),
    ADD COLUMN termos_aceitos_em DATETIME(6) NULL;

CREATE TABLE vazamento (
    id                 BIGINT        NOT NULL AUTO_INCREMENT,
    usuario_id         BIGINT        NOT NULL,
    tipo               VARCHAR(20)   NOT NULL,
    porte              VARCHAR(20)   NOT NULL,
    situacao           VARCHAR(20)   NOT NULL DEFAULT 'ABERTO',
    descricao          VARCHAR(1000) NULL,
    endereco           VARCHAR(255)  NULL,
    latitude           DECIMAL(9, 6) NOT NULL,
    longitude          DECIMAL(9, 6) NOT NULL,
    foto_url           VARCHAR(255)  NULL,
    criado_em          DATETIME(6)   NOT NULL,
    despachado_em      DATETIME(6)   NULL,
    concluido_em       DATETIME(6)   NULL,
    novidade_nao_vista TINYINT(1)    NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    KEY idx_vazamento_usuario (usuario_id),
    KEY idx_vazamento_situacao (situacao),
    KEY idx_vazamento_criado_em (criado_em),
    CONSTRAINT fk_vazamento_usuario FOREIGN KEY (usuario_id) REFERENCES usuario (id),
    CONSTRAINT ck_vazamento_tipo CHECK (tipo IN ('ADUTORA', 'REDE', 'RAMAL', 'CLANDESTINA', 'DESPERDICIO')),
    CONSTRAINT ck_vazamento_porte CHECK (porte IN ('GOTEJAMENTO', 'FIO', 'MEDIO', 'FORTE', 'JORRO')),
    CONSTRAINT ck_vazamento_situacao CHECK (situacao IN ('ABERTO', 'REPARO', 'RESOLVIDO')),
    CONSTRAINT ck_vazamento_latitude CHECK (latitude BETWEEN -90 AND 90),
    CONSTRAINT ck_vazamento_longitude CHECK (longitude BETWEEN -180 AND 180)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE apoio (
    id          BIGINT      NOT NULL AUTO_INCREMENT,
    usuario_id  BIGINT      NOT NULL,
    vazamento_id BIGINT     NOT NULL,
    criado_em   DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    -- cada cidadão reforça um vazamento uma única vez
    UNIQUE KEY uk_apoio_usuario_vazamento (usuario_id, vazamento_id),
    KEY idx_apoio_vazamento (vazamento_id),
    CONSTRAINT fk_apoio_usuario FOREIGN KEY (usuario_id) REFERENCES usuario (id),
    CONSTRAINT fk_apoio_vazamento FOREIGN KEY (vazamento_id) REFERENCES vazamento (id) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE avaliacao (
    id           BIGINT       NOT NULL AUTO_INCREMENT,
    vazamento_id BIGINT       NOT NULL,
    nota         INT          NOT NULL,
    comentario   VARCHAR(500) NULL,
    criado_em    DATETIME(6)  NOT NULL,
    PRIMARY KEY (id),
    -- uma avaliação por vazamento
    UNIQUE KEY uk_avaliacao_vazamento (vazamento_id),
    CONSTRAINT fk_avaliacao_vazamento FOREIGN KEY (vazamento_id) REFERENCES vazamento (id) ON DELETE CASCADE,
    CONSTRAINT ck_avaliacao_nota CHECK (nota BETWEEN 1 AND 5)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE recuperacao_senha (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    usuario_id  BIGINT       NOT NULL,
    codigo_hash VARCHAR(100) NOT NULL,
    expira_em   DATETIME(6)  NOT NULL,
    tentativas  INT          NOT NULL DEFAULT 0,
    usado_em    DATETIME(6)  NULL,
    criado_em   DATETIME(6)  NOT NULL,
    PRIMARY KEY (id),
    KEY idx_recuperacao_usuario (usuario_id, criado_em),
    CONSTRAINT fk_recuperacao_usuario FOREIGN KEY (usuario_id) REFERENCES usuario (id) ON DELETE CASCADE,
    CONSTRAINT ck_recuperacao_tentativas CHECK (tentativas >= 0)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;
