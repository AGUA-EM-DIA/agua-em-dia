-- V4__relatos
-- Gerado a partir da especificação única de tabelas; revisado manualmente nos índices e regras.

CREATE TABLE RascunhoRelato (
    id BIGINT IDENTITY(1,1) NOT NULL,
    usuario_id BIGINT NOT NULL,
    dados_parciais NVARCHAR(MAX) NOT NULL,
    criado_em DATETIMEOFFSET(3) NOT NULL CONSTRAINT DF_RascunhoRelato_criado_em DEFAULT (CAST(SYSUTCDATETIME() AS DATETIMEOFFSET(3))),
    atualizado_em DATETIMEOFFSET(3) NOT NULL CONSTRAINT DF_RascunhoRelato_atualizado_em DEFAULT (CAST(SYSUTCDATETIME() AS DATETIMEOFFSET(3))),
    CONSTRAINT PK_RascunhoRelato PRIMARY KEY (id),
    CONSTRAINT FK_RascunhoRelato_usuario_id FOREIGN KEY (usuario_id) REFERENCES Usuario (id),
    CONSTRAINT CK_Rascunho_json CHECK (ISJSON(dados_parciais) = 1)
);
CREATE INDEX IX_RascunhoRelato_usuario_id ON RascunhoRelato (usuario_id);

CREATE TABLE Relato (
    id BIGINT IDENTITY(1,1) NOT NULL,
    usuario_id BIGINT NOT NULL,
    vazamento_id BIGINT NOT NULL,
    localizacao_original_id BIGINT NOT NULL,
    protocolo NVARCHAR(30) NOT NULL,
    categoria_informada_id BIGINT NOT NULL,
    porte_informado_id BIGINT NOT NULL,
    infraestrutura_informada_id BIGINT NULL,
    descricao NVARCHAR(2000) NULL,
    enviado_em DATETIMEOFFSET(3) NOT NULL CONSTRAINT DF_Relato_enviado_em DEFAULT (CAST(SYSUTCDATETIME() AS DATETIMEOFFSET(3))),
    CONSTRAINT PK_Relato PRIMARY KEY (id),
    CONSTRAINT FK_Relato_usuario_id FOREIGN KEY (usuario_id) REFERENCES Usuario (id),
    CONSTRAINT FK_Relato_vazamento_id FOREIGN KEY (vazamento_id) REFERENCES Vazamento (id),
    CONSTRAINT FK_Relato_localizacao_original_id FOREIGN KEY (localizacao_original_id) REFERENCES Localizacao (id),
    CONSTRAINT FK_Relato_categoria_informada_id FOREIGN KEY (categoria_informada_id) REFERENCES CategoriaOcorrencia (id),
    CONSTRAINT FK_Relato_porte_informado_id FOREIGN KEY (porte_informado_id) REFERENCES PorteVazamento (id),
    CONSTRAINT FK_Relato_infraestrutura_informada_id FOREIGN KEY (infraestrutura_informada_id) REFERENCES TipoInfraestrutura (id)
);
CREATE INDEX IX_Relato_usuario_id ON Relato (usuario_id);
CREATE INDEX IX_Relato_vazamento_id ON Relato (vazamento_id);
CREATE INDEX IX_Relato_localizacao_original_id ON Relato (localizacao_original_id);
CREATE INDEX IX_Relato_categoria_informada_id ON Relato (categoria_informada_id);
CREATE INDEX IX_Relato_porte_informado_id ON Relato (porte_informado_id);
CREATE INDEX IX_Relato_infraestrutura_informada_id ON Relato (infraestrutura_informada_id);

CREATE TABLE SolicitacaoCorrecao (
    id BIGINT IDENTITY(1,1) NOT NULL,
    relato_id BIGINT NOT NULL,
    solicitante_id BIGINT NOT NULL,
    campo NVARCHAR(40) NOT NULL,
    valor_solicitado NVARCHAR(2000) NOT NULL,
    justificativa NVARCHAR(1000) NOT NULL,
    situacao NVARCHAR(20) NOT NULL CONSTRAINT DF_SolicitacaoCorrecao_situacao DEFAULT ('PENDENTE'),
    analisado_por BIGINT NULL,
    criado_em DATETIMEOFFSET(3) NOT NULL CONSTRAINT DF_SolicitacaoCorrecao_criado_em DEFAULT (CAST(SYSUTCDATETIME() AS DATETIMEOFFSET(3))),
    analisado_em DATETIMEOFFSET(3) NULL,
    CONSTRAINT PK_SolicitacaoCorrecao PRIMARY KEY (id),
    CONSTRAINT FK_SolicitacaoCorrecao_relato_id FOREIGN KEY (relato_id) REFERENCES Relato (id),
    CONSTRAINT FK_SolicitacaoCorrecao_solicitante_id FOREIGN KEY (solicitante_id) REFERENCES Usuario (id),
    CONSTRAINT FK_SolicitacaoCorrecao_analisado_por FOREIGN KEY (analisado_por) REFERENCES Usuario (id),
    CONSTRAINT CK_SolCorrecao_situacao CHECK (situacao IN ('PENDENTE','APROVADA','REJEITADA'))
);
CREATE INDEX IX_SolicitacaoCorrecao_relato_id ON SolicitacaoCorrecao (relato_id);
CREATE INDEX IX_SolicitacaoCorrecao_solicitante_id ON SolicitacaoCorrecao (solicitante_id);
CREATE INDEX IX_SolicitacaoCorrecao_analisado_por ON SolicitacaoCorrecao (analisado_por);

CREATE TABLE HistoricoAlteracaoRelato (
    id BIGINT IDENTITY(1,1) NOT NULL,
    relato_id BIGINT NOT NULL,
    campo NVARCHAR(40) NOT NULL,
    valor_anterior NVARCHAR(2000) NULL,
    valor_novo NVARCHAR(2000) NULL,
    alterado_por BIGINT NOT NULL,
    alterado_em DATETIMEOFFSET(3) NOT NULL CONSTRAINT DF_HistoricoAlteracaoRelato_alterado_em DEFAULT (CAST(SYSUTCDATETIME() AS DATETIMEOFFSET(3))),
    motivo NVARCHAR(500) NULL,
    CONSTRAINT PK_HistoricoAlteracaoRelato PRIMARY KEY (id),
    CONSTRAINT FK_HistoricoAlteracaoRelato_relato_id FOREIGN KEY (relato_id) REFERENCES Relato (id),
    CONSTRAINT FK_HistoricoAlteracaoRelato_alterado_por FOREIGN KEY (alterado_por) REFERENCES Usuario (id)
);
CREATE INDEX IX_HistoricoAlteracaoRelato_relato_id ON HistoricoAlteracaoRelato (relato_id);
CREATE INDEX IX_HistoricoAlteracaoRelato_alterado_por ON HistoricoAlteracaoRelato (alterado_por);


CREATE UNIQUE INDEX UX_Relato_protocolo ON Relato (protocolo);
CREATE INDEX IX_Relato_usuario_enviado ON Relato (usuario_id, enviado_em DESC);
CREATE INDEX IX_Relato_enviado_em ON Relato (enviado_em);
CREATE INDEX IX_Rascunho_usuario_atualizado ON RascunhoRelato (usuario_id, atualizado_em DESC);
