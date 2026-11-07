-- V1__identidade_e_acesso
-- Gerado a partir da especificação única de tabelas; revisado manualmente nos índices e regras.

CREATE TABLE Usuario (
    id BIGINT IDENTITY(1,1) NOT NULL,
    nome NVARCHAR(150) NOT NULL,
    email NVARCHAR(254) NOT NULL,
    telefone NVARCHAR(30) NULL,
    senha_hash NVARCHAR(100) NOT NULL,
    situacao NVARCHAR(30) NOT NULL CONSTRAINT DF_Usuario_situacao DEFAULT ('PENDENTE_VERIFICACAO'),
    email_verificado_em DATETIMEOFFSET(3) NULL,
    criado_em DATETIMEOFFSET(3) NOT NULL CONSTRAINT DF_Usuario_criado_em DEFAULT (CAST(SYSUTCDATETIME() AS DATETIMEOFFSET(3))),
    anonimizado_em DATETIMEOFFSET(3) NULL,
    CONSTRAINT PK_Usuario PRIMARY KEY (id),
    CONSTRAINT CK_Usuario_situacao CHECK (situacao IN ('PENDENTE_VERIFICACAO','ATIVO','DESATIVADO','ANONIMIZADO'))
);


CREATE TABLE Papel (
    id BIGINT IDENTITY(1,1) NOT NULL,
    nome NVARCHAR(30) NOT NULL,
    descricao NVARCHAR(200) NULL,
    ativo BIT NOT NULL CONSTRAINT DF_Papel_ativo DEFAULT (1),
    CONSTRAINT PK_Papel PRIMARY KEY (id)
);


CREATE TABLE UsuarioPapel (
    id BIGINT IDENTITY(1,1) NOT NULL,
    usuario_id BIGINT NOT NULL,
    papel_id BIGINT NOT NULL,
    atribuido_em DATETIMEOFFSET(3) NOT NULL CONSTRAINT DF_UsuarioPapel_atribuido_em DEFAULT (CAST(SYSUTCDATETIME() AS DATETIMEOFFSET(3))),
    atribuido_por BIGINT NULL,
    revogado_em DATETIMEOFFSET(3) NULL,
    CONSTRAINT PK_UsuarioPapel PRIMARY KEY (id),
    CONSTRAINT FK_UsuarioPapel_usuario_id FOREIGN KEY (usuario_id) REFERENCES Usuario (id),
    CONSTRAINT FK_UsuarioPapel_papel_id FOREIGN KEY (papel_id) REFERENCES Papel (id),
    CONSTRAINT FK_UsuarioPapel_atribuido_por FOREIGN KEY (atribuido_por) REFERENCES Usuario (id)
);
CREATE INDEX IX_UsuarioPapel_usuario_id ON UsuarioPapel (usuario_id);
CREATE INDEX IX_UsuarioPapel_papel_id ON UsuarioPapel (papel_id);
CREATE INDEX IX_UsuarioPapel_atribuido_por ON UsuarioPapel (atribuido_por);

CREATE TABLE TokenVerificacao (
    id BIGINT IDENTITY(1,1) NOT NULL,
    usuario_id BIGINT NOT NULL,
    token_hash NVARCHAR(64) NOT NULL,
    expira_em DATETIMEOFFSET(3) NOT NULL,
    utilizado_em DATETIMEOFFSET(3) NULL,
    criado_em DATETIMEOFFSET(3) NOT NULL CONSTRAINT DF_TokenVerificacao_criado_em DEFAULT (CAST(SYSUTCDATETIME() AS DATETIMEOFFSET(3))),
    CONSTRAINT PK_TokenVerificacao PRIMARY KEY (id),
    CONSTRAINT FK_TokenVerificacao_usuario_id FOREIGN KEY (usuario_id) REFERENCES Usuario (id)
);
CREATE INDEX IX_TokenVerificacao_usuario_id ON TokenVerificacao (usuario_id);

CREATE TABLE TokenRecuperacao (
    id BIGINT IDENTITY(1,1) NOT NULL,
    usuario_id BIGINT NOT NULL,
    token_hash NVARCHAR(64) NOT NULL,
    expira_em DATETIMEOFFSET(3) NOT NULL,
    utilizado_em DATETIMEOFFSET(3) NULL,
    criado_em DATETIMEOFFSET(3) NOT NULL CONSTRAINT DF_TokenRecuperacao_criado_em DEFAULT (CAST(SYSUTCDATETIME() AS DATETIMEOFFSET(3))),
    CONSTRAINT PK_TokenRecuperacao PRIMARY KEY (id),
    CONSTRAINT FK_TokenRecuperacao_usuario_id FOREIGN KEY (usuario_id) REFERENCES Usuario (id)
);
CREATE INDEX IX_TokenRecuperacao_usuario_id ON TokenRecuperacao (usuario_id);


CREATE UNIQUE INDEX UX_Usuario_email ON Usuario (email);
CREATE UNIQUE INDEX UX_Papel_nome ON Papel (nome);
-- Um papel vigente por usuário (permite reconceder depois de revogar).
CREATE UNIQUE INDEX UX_UsuarioPapel_vigente ON UsuarioPapel (usuario_id, papel_id) WHERE revogado_em IS NULL;
CREATE UNIQUE INDEX UX_TokenVerificacao_hash ON TokenVerificacao (token_hash);
CREATE UNIQUE INDEX UX_TokenRecuperacao_hash ON TokenRecuperacao (token_hash);

INSERT INTO Papel (nome, descricao, ativo) VALUES
 ('CIDADAO',       N'Cidadão que registra relatos', 1),
 ('EQUIPE',        N'Integrante de equipe de execução', 1),
 ('OPERADOR',      N'Operador da central', 1),
 ('GESTOR',        N'Gestor operacional', 1),
 ('ADMINISTRADOR', N'Administrador do sistema', 1);
