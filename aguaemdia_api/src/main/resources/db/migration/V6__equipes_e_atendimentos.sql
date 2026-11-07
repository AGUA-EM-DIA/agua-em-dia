-- V6__equipes_e_atendimentos
-- Gerado a partir da especificação única de tabelas; revisado manualmente nos índices e regras.

CREATE TABLE Equipe (
    id BIGINT IDENTITY(1,1) NOT NULL,
    nome NVARCHAR(100) NOT NULL,
    situacao NVARCHAR(20) NOT NULL CONSTRAINT DF_Equipe_situacao DEFAULT ('ATIVA'),
    criado_em DATETIMEOFFSET(3) NOT NULL CONSTRAINT DF_Equipe_criado_em DEFAULT (CAST(SYSUTCDATETIME() AS DATETIMEOFFSET(3))),
    CONSTRAINT PK_Equipe PRIMARY KEY (id),
    CONSTRAINT CK_Equipe_situacao CHECK (situacao IN ('ATIVA','INATIVA'))
);


CREATE TABLE MembroEquipe (
    id BIGINT IDENTITY(1,1) NOT NULL,
    equipe_id BIGINT NOT NULL,
    usuario_id BIGINT NOT NULL,
    inicio_vinculo DATETIMEOFFSET(3) NOT NULL CONSTRAINT DF_MembroEquipe_inicio_vinculo DEFAULT (CAST(SYSUTCDATETIME() AS DATETIMEOFFSET(3))),
    fim_vinculo DATETIMEOFFSET(3) NULL,
    CONSTRAINT PK_MembroEquipe PRIMARY KEY (id),
    CONSTRAINT FK_MembroEquipe_equipe_id FOREIGN KEY (equipe_id) REFERENCES Equipe (id),
    CONSTRAINT FK_MembroEquipe_usuario_id FOREIGN KEY (usuario_id) REFERENCES Usuario (id)
);
CREATE INDEX IX_MembroEquipe_equipe_id ON MembroEquipe (equipe_id);
CREATE INDEX IX_MembroEquipe_usuario_id ON MembroEquipe (usuario_id);

CREATE TABLE Atendimento (
    id BIGINT IDENTITY(1,1) NOT NULL,
    vazamento_id BIGINT NOT NULL,
    status NVARCHAR(30) NOT NULL CONSTRAINT DF_Atendimento_status DEFAULT ('AGUARDANDO_EQUIPE'),
    criado_em DATETIMEOFFSET(3) NOT NULL CONSTRAINT DF_Atendimento_criado_em DEFAULT (CAST(SYSUTCDATETIME() AS DATETIMEOFFSET(3))),
    iniciado_em DATETIMEOFFSET(3) NULL,
    encerrado_em DATETIMEOFFSET(3) NULL,
    versao BIGINT NOT NULL CONSTRAINT DF_Atendimento_versao DEFAULT (0),
    CONSTRAINT PK_Atendimento PRIMARY KEY (id),
    CONSTRAINT FK_Atendimento_vazamento_id FOREIGN KEY (vazamento_id) REFERENCES Vazamento (id),
    CONSTRAINT CK_Atendimento_status CHECK (status IN ('AGUARDANDO_EQUIPE','ATRIBUIDO','EM_EXECUCAO','AGUARDANDO_VALIDACAO','CONCLUIDO','CANCELADO'))
);
CREATE INDEX IX_Atendimento_vazamento_id ON Atendimento (vazamento_id);

CREATE TABLE AtribuicaoEquipe (
    id BIGINT IDENTITY(1,1) NOT NULL,
    atendimento_id BIGINT NOT NULL,
    equipe_id BIGINT NOT NULL,
    atribuido_por BIGINT NOT NULL,
    atribuido_em DATETIMEOFFSET(3) NOT NULL CONSTRAINT DF_AtribuicaoEquipe_atribuido_em DEFAULT (CAST(SYSUTCDATETIME() AS DATETIMEOFFSET(3))),
    encerrado_em DATETIMEOFFSET(3) NULL,
    motivo_substituicao NVARCHAR(500) NULL,
    CONSTRAINT PK_AtribuicaoEquipe PRIMARY KEY (id),
    CONSTRAINT FK_AtribuicaoEquipe_atendimento_id FOREIGN KEY (atendimento_id) REFERENCES Atendimento (id),
    CONSTRAINT FK_AtribuicaoEquipe_equipe_id FOREIGN KEY (equipe_id) REFERENCES Equipe (id),
    CONSTRAINT FK_AtribuicaoEquipe_atribuido_por FOREIGN KEY (atribuido_por) REFERENCES Usuario (id)
);
CREATE INDEX IX_AtribuicaoEquipe_atendimento_id ON AtribuicaoEquipe (atendimento_id);
CREATE INDEX IX_AtribuicaoEquipe_equipe_id ON AtribuicaoEquipe (equipe_id);
CREATE INDEX IX_AtribuicaoEquipe_atribuido_por ON AtribuicaoEquipe (atribuido_por);

CREATE TABLE RegistroExecucao (
    id BIGINT IDENTITY(1,1) NOT NULL,
    atendimento_id BIGINT NOT NULL,
    usuario_id BIGINT NOT NULL,
    tipo_evento NVARCHAR(30) NOT NULL,
    registrado_em DATETIMEOFFSET(3) NOT NULL CONSTRAINT DF_RegistroExecucao_registrado_em DEFAULT (CAST(SYSUTCDATETIME() AS DATETIMEOFFSET(3))),
    observacao NVARCHAR(2000) NULL,
    CONSTRAINT PK_RegistroExecucao PRIMARY KEY (id),
    CONSTRAINT FK_RegistroExecucao_atendimento_id FOREIGN KEY (atendimento_id) REFERENCES Atendimento (id),
    CONSTRAINT FK_RegistroExecucao_usuario_id FOREIGN KEY (usuario_id) REFERENCES Usuario (id),
    CONSTRAINT CK_RegExec_tipo CHECK (tipo_evento IN ('INICIO','ANDAMENTO','PAUSA','RETOMADA','OBSERVACAO'))
);
CREATE INDEX IX_RegistroExecucao_atendimento_id ON RegistroExecucao (atendimento_id);
CREATE INDEX IX_RegistroExecucao_usuario_id ON RegistroExecucao (usuario_id);

CREATE TABLE ConclusaoServico (
    id BIGINT IDENTITY(1,1) NOT NULL,
    atendimento_id BIGINT NOT NULL,
    integrante_id BIGINT NOT NULL,
    resumo NVARCHAR(4000) NOT NULL,
    reparo_efetivo_em DATETIMEOFFSET(3) NOT NULL,
    informado_em DATETIMEOFFSET(3) NOT NULL CONSTRAINT DF_ConclusaoServico_informado_em DEFAULT (CAST(SYSUTCDATETIME() AS DATETIMEOFFSET(3))),
    CONSTRAINT PK_ConclusaoServico PRIMARY KEY (id),
    CONSTRAINT FK_ConclusaoServico_atendimento_id FOREIGN KEY (atendimento_id) REFERENCES Atendimento (id),
    CONSTRAINT FK_ConclusaoServico_integrante_id FOREIGN KEY (integrante_id) REFERENCES Usuario (id)
);
CREATE INDEX IX_ConclusaoServico_atendimento_id ON ConclusaoServico (atendimento_id);
CREATE INDEX IX_ConclusaoServico_integrante_id ON ConclusaoServico (integrante_id);

CREATE TABLE ValidacaoReparo (
    id BIGINT IDENTITY(1,1) NOT NULL,
    conclusao_id BIGINT NOT NULL,
    operador_id BIGINT NOT NULL,
    decisao NVARCHAR(20) NOT NULL,
    justificativa NVARCHAR(1000) NULL,
    validado_em DATETIMEOFFSET(3) NOT NULL CONSTRAINT DF_ValidacaoReparo_validado_em DEFAULT (CAST(SYSUTCDATETIME() AS DATETIMEOFFSET(3))),
    CONSTRAINT PK_ValidacaoReparo PRIMARY KEY (id),
    CONSTRAINT FK_ValidacaoReparo_conclusao_id FOREIGN KEY (conclusao_id) REFERENCES ConclusaoServico (id),
    CONSTRAINT FK_ValidacaoReparo_operador_id FOREIGN KEY (operador_id) REFERENCES Usuario (id),
    CONSTRAINT CK_ValidacaoReparo_decisao CHECK (decisao IN ('APROVADA','REJEITADA'))
);
CREATE INDEX IX_ValidacaoReparo_conclusao_id ON ValidacaoReparo (conclusao_id);
CREATE INDEX IX_ValidacaoReparo_operador_id ON ValidacaoReparo (operador_id);

CREATE TABLE SolicitacaoReabertura (
    id BIGINT IDENTITY(1,1) NOT NULL,
    vazamento_id BIGINT NOT NULL,
    solicitante_id BIGINT NOT NULL,
    motivo NVARCHAR(1000) NOT NULL,
    situacao NVARCHAR(20) NOT NULL CONSTRAINT DF_SolicitacaoReabertura_situacao DEFAULT ('PENDENTE'),
    criado_em DATETIMEOFFSET(3) NOT NULL CONSTRAINT DF_SolicitacaoReabertura_criado_em DEFAULT (CAST(SYSUTCDATETIME() AS DATETIMEOFFSET(3))),
    decidido_por BIGINT NULL,
    decidido_em DATETIMEOFFSET(3) NULL,
    decisao_justificativa NVARCHAR(1000) NULL,
    atendimento_novo_id BIGINT NULL,
    CONSTRAINT PK_SolicitacaoReabertura PRIMARY KEY (id),
    CONSTRAINT FK_SolicitacaoReabertura_vazamento_id FOREIGN KEY (vazamento_id) REFERENCES Vazamento (id),
    CONSTRAINT FK_SolicitacaoReabertura_solicitante_id FOREIGN KEY (solicitante_id) REFERENCES Usuario (id),
    CONSTRAINT FK_SolicitacaoReabertura_decidido_por FOREIGN KEY (decidido_por) REFERENCES Usuario (id),
    CONSTRAINT FK_SolicitacaoReabertura_atendimento_novo_id FOREIGN KEY (atendimento_novo_id) REFERENCES Atendimento (id),
    CONSTRAINT CK_SolReabertura_situacao CHECK (situacao IN ('PENDENTE','APROVADA','REJEITADA'))
);
CREATE INDEX IX_SolicitacaoReabertura_vazamento_id ON SolicitacaoReabertura (vazamento_id);
CREATE INDEX IX_SolicitacaoReabertura_solicitante_id ON SolicitacaoReabertura (solicitante_id);
CREATE INDEX IX_SolicitacaoReabertura_decidido_por ON SolicitacaoReabertura (decidido_por);
CREATE INDEX IX_SolicitacaoReabertura_atendimento_novo_id ON SolicitacaoReabertura (atendimento_novo_id);

CREATE TABLE CancelamentoOcorrencia (
    id BIGINT IDENTITY(1,1) NOT NULL,
    vazamento_id BIGINT NOT NULL,
    responsavel_id BIGINT NOT NULL,
    motivo NVARCHAR(1000) NOT NULL,
    cancelado_em DATETIMEOFFSET(3) NOT NULL CONSTRAINT DF_CancelamentoOcorrencia_cancelado_em DEFAULT (CAST(SYSUTCDATETIME() AS DATETIMEOFFSET(3))),
    CONSTRAINT PK_CancelamentoOcorrencia PRIMARY KEY (id),
    CONSTRAINT FK_CancelamentoOcorrencia_vazamento_id FOREIGN KEY (vazamento_id) REFERENCES Vazamento (id),
    CONSTRAINT FK_CancelamentoOcorrencia_responsavel_id FOREIGN KEY (responsavel_id) REFERENCES Usuario (id)
);
CREATE INDEX IX_CancelamentoOcorrencia_vazamento_id ON CancelamentoOcorrencia (vazamento_id);
CREATE INDEX IX_CancelamentoOcorrencia_responsavel_id ON CancelamentoOcorrencia (responsavel_id);

CREATE TABLE Imagem (
    id BIGINT IDENTITY(1,1) NOT NULL,
    relato_id BIGINT NULL,
    atendimento_id BIGINT NULL,
    caminho_arquivo NVARCHAR(400) NOT NULL,
    nome_original NVARCHAR(255) NULL,
    tipo_mime NVARCHAR(50) NOT NULL,
    tamanho_bytes BIGINT NOT NULL,
    criado_por BIGINT NOT NULL,
    criado_em DATETIMEOFFSET(3) NOT NULL CONSTRAINT DF_Imagem_criado_em DEFAULT (CAST(SYSUTCDATETIME() AS DATETIMEOFFSET(3))),
    CONSTRAINT PK_Imagem PRIMARY KEY (id),
    CONSTRAINT FK_Imagem_relato_id FOREIGN KEY (relato_id) REFERENCES Relato (id),
    CONSTRAINT FK_Imagem_atendimento_id FOREIGN KEY (atendimento_id) REFERENCES Atendimento (id),
    CONSTRAINT FK_Imagem_criado_por FOREIGN KEY (criado_por) REFERENCES Usuario (id),
    CONSTRAINT CK_Imagem_mime CHECK (tipo_mime IN ('image/jpeg','image/png','image/webp','image/heic')),
    CONSTRAINT CK_Imagem_dono CHECK ((relato_id IS NULL AND atendimento_id IS NOT NULL) OR (relato_id IS NOT NULL AND atendimento_id IS NULL)),
    CONSTRAINT CK_Imagem_tamanho CHECK (tamanho_bytes > 0)
);
CREATE INDEX IX_Imagem_relato_id ON Imagem (relato_id);
CREATE INDEX IX_Imagem_atendimento_id ON Imagem (atendimento_id);
CREATE INDEX IX_Imagem_criado_por ON Imagem (criado_por);


CREATE UNIQUE INDEX UX_Equipe_nome ON Equipe (nome);
-- Integrante com no máximo uma equipe vigente.
CREATE UNIQUE INDEX UX_MembroEquipe_vigente ON MembroEquipe (usuario_id) WHERE fim_vinculo IS NULL;
-- No máximo um atendimento em aberto por vazamento.
CREATE UNIQUE INDEX UX_Atendimento_aberto ON Atendimento (vazamento_id) WHERE status NOT IN ('CONCLUIDO','CANCELADO');
CREATE INDEX IX_Atendimento_status ON Atendimento (status);
-- Uma equipe vigente por atendimento.
CREATE UNIQUE INDEX UX_AtribuicaoEquipe_vigente ON AtribuicaoEquipe (atendimento_id) WHERE encerrado_em IS NULL;
CREATE INDEX IX_ConclusaoServico_atendimento ON ConclusaoServico (atendimento_id, informado_em DESC);
CREATE INDEX IX_SolReabertura_situacao ON SolicitacaoReabertura (situacao);
