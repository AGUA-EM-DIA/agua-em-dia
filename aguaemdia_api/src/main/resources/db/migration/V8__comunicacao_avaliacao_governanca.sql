-- V8__comunicacao_avaliacao_governanca
-- Gerado a partir da especificação única de tabelas; revisado manualmente nos índices e regras.

CREATE TABLE Avaliacao (
    id BIGINT IDENTITY(1,1) NOT NULL,
    atendimento_id BIGINT NOT NULL,
    usuario_id BIGINT NOT NULL,
    nota SMALLINT NOT NULL,
    comentario NVARCHAR(1000) NULL,
    criado_em DATETIMEOFFSET(3) NOT NULL CONSTRAINT DF_Avaliacao_criado_em DEFAULT (CAST(SYSUTCDATETIME() AS DATETIMEOFFSET(3))),
    atualizado_em DATETIMEOFFSET(3) NULL,
    CONSTRAINT PK_Avaliacao PRIMARY KEY (id),
    CONSTRAINT FK_Avaliacao_atendimento_id FOREIGN KEY (atendimento_id) REFERENCES Atendimento (id),
    CONSTRAINT FK_Avaliacao_usuario_id FOREIGN KEY (usuario_id) REFERENCES Usuario (id),
    CONSTRAINT CK_Avaliacao_nota CHECK (nota BETWEEN 1 AND 5)
);
CREATE INDEX IX_Avaliacao_atendimento_id ON Avaliacao (atendimento_id);
CREATE INDEX IX_Avaliacao_usuario_id ON Avaliacao (usuario_id);

CREATE TABLE ContatoRetorno (
    id BIGINT IDENTITY(1,1) NOT NULL,
    avaliacao_id BIGINT NOT NULL,
    responsavel_id BIGINT NULL,
    situacao NVARCHAR(20) NOT NULL CONSTRAINT DF_ContatoRetorno_situacao DEFAULT ('ABERTO'),
    observacao NVARCHAR(2000) NULL,
    criado_em DATETIMEOFFSET(3) NOT NULL CONSTRAINT DF_ContatoRetorno_criado_em DEFAULT (CAST(SYSUTCDATETIME() AS DATETIMEOFFSET(3))),
    encerrado_em DATETIMEOFFSET(3) NULL,
    CONSTRAINT PK_ContatoRetorno PRIMARY KEY (id),
    CONSTRAINT FK_ContatoRetorno_avaliacao_id FOREIGN KEY (avaliacao_id) REFERENCES Avaliacao (id),
    CONSTRAINT FK_ContatoRetorno_responsavel_id FOREIGN KEY (responsavel_id) REFERENCES Usuario (id),
    CONSTRAINT CK_ContatoRetorno_situacao CHECK (situacao IN ('ABERTO','EM_CONTATO','CONCLUIDO'))
);
CREATE INDEX IX_ContatoRetorno_avaliacao_id ON ContatoRetorno (avaliacao_id);
CREATE INDEX IX_ContatoRetorno_responsavel_id ON ContatoRetorno (responsavel_id);

CREATE TABLE Notificacao (
    id BIGINT IDENTITY(1,1) NOT NULL,
    usuario_id BIGINT NOT NULL,
    tipo NVARCHAR(40) NOT NULL,
    titulo NVARCHAR(150) NOT NULL,
    mensagem NVARCHAR(1000) NOT NULL,
    vazamento_id BIGINT NULL,
    criado_em DATETIMEOFFSET(3) NOT NULL CONSTRAINT DF_Notificacao_criado_em DEFAULT (CAST(SYSUTCDATETIME() AS DATETIMEOFFSET(3))),
    lida_em DATETIMEOFFSET(3) NULL,
    CONSTRAINT PK_Notificacao PRIMARY KEY (id),
    CONSTRAINT FK_Notificacao_usuario_id FOREIGN KEY (usuario_id) REFERENCES Usuario (id),
    CONSTRAINT FK_Notificacao_vazamento_id FOREIGN KEY (vazamento_id) REFERENCES Vazamento (id)
);
CREATE INDEX IX_Notificacao_usuario_id ON Notificacao (usuario_id);
CREATE INDEX IX_Notificacao_vazamento_id ON Notificacao (vazamento_id);

CREATE TABLE EnvioNotificacao (
    id BIGINT IDENTITY(1,1) NOT NULL,
    notificacao_id BIGINT NOT NULL,
    canal NVARCHAR(10) NOT NULL,
    situacao NVARCHAR(20) NOT NULL CONSTRAINT DF_EnvioNotificacao_situacao DEFAULT ('PENDENTE'),
    tentativas INT NOT NULL CONSTRAINT DF_EnvioNotificacao_tentativas DEFAULT (0),
    enviado_em DATETIMEOFFSET(3) NULL,
    erro NVARCHAR(500) NULL,
    proxima_tentativa_em DATETIMEOFFSET(3) NULL,
    CONSTRAINT PK_EnvioNotificacao PRIMARY KEY (id),
    CONSTRAINT FK_EnvioNotificacao_notificacao_id FOREIGN KEY (notificacao_id) REFERENCES Notificacao (id),
    CONSTRAINT CK_Envio_canal CHECK (canal IN ('APP','EMAIL')),
    CONSTRAINT CK_Envio_situacao CHECK (situacao IN ('PENDENTE','ENVIADO','FALHA'))
);
CREATE INDEX IX_EnvioNotificacao_notificacao_id ON EnvioNotificacao (notificacao_id);

CREATE TABLE HistoricoStatus (
    id BIGINT IDENTITY(1,1) NOT NULL,
    entidade_tipo NVARCHAR(40) NOT NULL,
    entidade_id BIGINT NOT NULL,
    status_anterior NVARCHAR(30) NULL,
    status_novo NVARCHAR(30) NOT NULL,
    alterado_por BIGINT NULL,
    alterado_em DATETIMEOFFSET(3) NOT NULL CONSTRAINT DF_HistoricoStatus_alterado_em DEFAULT (CAST(SYSUTCDATETIME() AS DATETIMEOFFSET(3))),
    justificativa NVARCHAR(1000) NULL,
    CONSTRAINT PK_HistoricoStatus PRIMARY KEY (id),
    CONSTRAINT FK_HistoricoStatus_alterado_por FOREIGN KEY (alterado_por) REFERENCES Usuario (id)
);
CREATE INDEX IX_HistoricoStatus_alterado_por ON HistoricoStatus (alterado_por);

CREATE TABLE Auditoria (
    id BIGINT IDENTITY(1,1) NOT NULL,
    usuario_id BIGINT NULL,
    acao NVARCHAR(60) NOT NULL,
    entidade_tipo NVARCHAR(40) NOT NULL,
    entidade_id BIGINT NULL,
    dados_anteriores NVARCHAR(MAX) NULL,
    dados_novos NVARCHAR(MAX) NULL,
    criado_em DATETIMEOFFSET(3) NOT NULL CONSTRAINT DF_Auditoria_criado_em DEFAULT (CAST(SYSUTCDATETIME() AS DATETIMEOFFSET(3))),
    CONSTRAINT PK_Auditoria PRIMARY KEY (id),
    CONSTRAINT FK_Auditoria_usuario_id FOREIGN KEY (usuario_id) REFERENCES Usuario (id)
);
CREATE INDEX IX_Auditoria_usuario_id ON Auditoria (usuario_id);

CREATE TABLE ParametroSistema (
    id BIGINT IDENTITY(1,1) NOT NULL,
    chave NVARCHAR(100) NOT NULL,
    valor NVARCHAR(500) NOT NULL,
    descricao NVARCHAR(300) NULL,
    atualizado_por BIGINT NULL,
    atualizado_em DATETIMEOFFSET(3) NOT NULL CONSTRAINT DF_ParametroSistema_atualizado_em DEFAULT (CAST(SYSUTCDATETIME() AS DATETIMEOFFSET(3))),
    CONSTRAINT PK_ParametroSistema PRIMARY KEY (id),
    CONSTRAINT FK_ParametroSistema_atualizado_por FOREIGN KEY (atualizado_por) REFERENCES Usuario (id)
);
CREATE INDEX IX_ParametroSistema_atualizado_por ON ParametroSistema (atualizado_por);


CREATE UNIQUE INDEX UX_Avaliacao_atendimento_usuario ON Avaliacao (atendimento_id, usuario_id);
CREATE INDEX IX_Notificacao_usuario_criado ON Notificacao (usuario_id, criado_em DESC);
CREATE INDEX IX_Envio_pendentes ON EnvioNotificacao (situacao, proxima_tentativa_em);
CREATE INDEX IX_HistoricoStatus_entidade ON HistoricoStatus (entidade_tipo, entidade_id);
CREATE INDEX IX_Auditoria_entidade ON Auditoria (entidade_tipo, entidade_id);
CREATE INDEX IX_Auditoria_criado_em ON Auditoria (criado_em);
CREATE UNIQUE INDEX UX_Parametro_chave ON ParametroSistema (chave);
