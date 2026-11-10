-- V7__estimativas_prioridade_emergencia
-- Gerado a partir da especificação única de tabelas; revisado manualmente nos índices e regras.

CREATE TABLE IntervaloEstimativa (
    id BIGINT IDENTITY(1,1) NOT NULL,
    vazamento_id BIGINT NOT NULL,
    inicio DATETIMEOFFSET(3) NOT NULL,
    fim DATETIMEOFFSET(3) NULL,
    vazao_l_h DECIMAL(18,4) NULL,
    situacao NVARCHAR(20) NOT NULL CONSTRAINT DF_IntervaloEstimativa_situacao DEFAULT ('ATIVO'),
    versao_calculo INT NOT NULL CONSTRAINT DF_IntervaloEstimativa_versao_calculo DEFAULT (1),
    criado_em DATETIMEOFFSET(3) NOT NULL CONSTRAINT DF_IntervaloEstimativa_criado_em DEFAULT (CAST(SYSUTCDATETIME() AS DATETIMEOFFSET(3))),
    CONSTRAINT PK_IntervaloEstimativa PRIMARY KEY (id),
    CONSTRAINT FK_IntervaloEstimativa_vazamento_id FOREIGN KEY (vazamento_id) REFERENCES Vazamento (id),
    CONSTRAINT CK_Intervalo_situacao CHECK (situacao IN ('ATIVO','CANCELADO')),
    CONSTRAINT CK_Intervalo_ordem CHECK (fim IS NULL OR fim > inicio),
    CONSTRAINT CK_Intervalo_vazao CHECK (vazao_l_h IS NULL OR vazao_l_h >= 0)
);
CREATE INDEX IX_IntervaloEstimativa_vazamento_id ON IntervaloEstimativa (vazamento_id);

CREATE TABLE RevisaoCalculo (
    id BIGINT IDENTITY(1,1) NOT NULL,
    vazamento_id BIGINT NOT NULL,
    motivo NVARCHAR(300) NOT NULL,
    versao_anterior INT NOT NULL,
    versao_nova INT NOT NULL,
    executado_em DATETIMEOFFSET(3) NOT NULL CONSTRAINT DF_RevisaoCalculo_executado_em DEFAULT (CAST(SYSUTCDATETIME() AS DATETIMEOFFSET(3))),
    executado_por BIGINT NULL,
    CONSTRAINT PK_RevisaoCalculo PRIMARY KEY (id),
    CONSTRAINT FK_RevisaoCalculo_vazamento_id FOREIGN KEY (vazamento_id) REFERENCES Vazamento (id),
    CONSTRAINT FK_RevisaoCalculo_executado_por FOREIGN KEY (executado_por) REFERENCES Usuario (id)
);
CREATE INDEX IX_RevisaoCalculo_vazamento_id ON RevisaoCalculo (vazamento_id);
CREATE INDEX IX_RevisaoCalculo_executado_por ON RevisaoCalculo (executado_por);

CREATE TABLE MatrizPrazoAtendimento (
    id BIGINT IDENTITY(1,1) NOT NULL,
    porte_id BIGINT NOT NULL,
    infraestrutura_id BIGINT NOT NULL,
    prazo_horas INT NOT NULL,
    vigente_desde DATETIMEOFFSET(3) NOT NULL,
    vigente_ate DATETIMEOFFSET(3) NULL,
    CONSTRAINT PK_MatrizPrazoAtendimento PRIMARY KEY (id),
    CONSTRAINT FK_MatrizPrazoAtendimento_porte_id FOREIGN KEY (porte_id) REFERENCES PorteVazamento (id),
    CONSTRAINT FK_MatrizPrazoAtendimento_infraestrutura_id FOREIGN KEY (infraestrutura_id) REFERENCES TipoInfraestrutura (id),
    CONSTRAINT CK_Matriz_prazo CHECK (prazo_horas > 0)
);
CREATE INDEX IX_MatrizPrazoAtendimento_porte_id ON MatrizPrazoAtendimento (porte_id);
CREATE INDEX IX_MatrizPrazoAtendimento_infraestrutura_id ON MatrizPrazoAtendimento (infraestrutura_id);

CREATE TABLE HistoricoPrazo (
    id BIGINT IDENTITY(1,1) NOT NULL,
    vazamento_id BIGINT NOT NULL,
    prazo_anterior INT NULL,
    prazo_novo INT NULL,
    alterado_em DATETIMEOFFSET(3) NOT NULL CONSTRAINT DF_HistoricoPrazo_alterado_em DEFAULT (CAST(SYSUTCDATETIME() AS DATETIMEOFFSET(3))),
    motivo NVARCHAR(300) NOT NULL,
    CONSTRAINT PK_HistoricoPrazo PRIMARY KEY (id),
    CONSTRAINT FK_HistoricoPrazo_vazamento_id FOREIGN KEY (vazamento_id) REFERENCES Vazamento (id)
);
CREATE INDEX IX_HistoricoPrazo_vazamento_id ON HistoricoPrazo (vazamento_id);

CREATE TABLE HistoricoPrioridade (
    id BIGINT IDENTITY(1,1) NOT NULL,
    vazamento_id BIGINT NOT NULL,
    pontuacao DECIMAL(18,6) NULL,
    parametros_utilizados NVARCHAR(MAX) NOT NULL,
    calculado_em DATETIMEOFFSET(3) NOT NULL CONSTRAINT DF_HistoricoPrioridade_calculado_em DEFAULT (CAST(SYSUTCDATETIME() AS DATETIMEOFFSET(3))),
    CONSTRAINT PK_HistoricoPrioridade PRIMARY KEY (id),
    CONSTRAINT FK_HistoricoPrioridade_vazamento_id FOREIGN KEY (vazamento_id) REFERENCES Vazamento (id),
    CONSTRAINT CK_HistPrioridade_json CHECK (ISJSON(parametros_utilizados) = 1)
);
CREATE INDEX IX_HistoricoPrioridade_vazamento_id ON HistoricoPrioridade (vazamento_id);

CREATE TABLE SinalizacaoEmergencia (
    id BIGINT IDENTITY(1,1) NOT NULL,
    vazamento_id BIGINT NOT NULL,
    usuario_id BIGINT NOT NULL,
    motivo NVARCHAR(1000) NOT NULL,
    sinalizado_em DATETIMEOFFSET(3) NOT NULL CONSTRAINT DF_SinalizacaoEmergencia_sinalizado_em DEFAULT (CAST(SYSUTCDATETIME() AS DATETIMEOFFSET(3))),
    CONSTRAINT PK_SinalizacaoEmergencia PRIMARY KEY (id),
    CONSTRAINT FK_SinalizacaoEmergencia_vazamento_id FOREIGN KEY (vazamento_id) REFERENCES Vazamento (id),
    CONSTRAINT FK_SinalizacaoEmergencia_usuario_id FOREIGN KEY (usuario_id) REFERENCES Usuario (id)
);
CREATE INDEX IX_SinalizacaoEmergencia_vazamento_id ON SinalizacaoEmergencia (vazamento_id);
CREATE INDEX IX_SinalizacaoEmergencia_usuario_id ON SinalizacaoEmergencia (usuario_id);

CREATE TABLE AvaliacaoEmergencia (
    id BIGINT IDENTITY(1,1) NOT NULL,
    vazamento_id BIGINT NOT NULL,
    sinalizacao_id BIGINT NULL,
    operador_id BIGINT NOT NULL,
    decisao NVARCHAR(20) NOT NULL,
    gravidade SMALLINT NULL,
    justificativa NVARCHAR(1000) NOT NULL,
    avaliado_em DATETIMEOFFSET(3) NOT NULL CONSTRAINT DF_AvaliacaoEmergencia_avaliado_em DEFAULT (CAST(SYSUTCDATETIME() AS DATETIMEOFFSET(3))),
    CONSTRAINT PK_AvaliacaoEmergencia PRIMARY KEY (id),
    CONSTRAINT FK_AvaliacaoEmergencia_vazamento_id FOREIGN KEY (vazamento_id) REFERENCES Vazamento (id),
    CONSTRAINT FK_AvaliacaoEmergencia_sinalizacao_id FOREIGN KEY (sinalizacao_id) REFERENCES SinalizacaoEmergencia (id),
    CONSTRAINT FK_AvaliacaoEmergencia_operador_id FOREIGN KEY (operador_id) REFERENCES Usuario (id),
    CONSTRAINT CK_AvalEmerg_decisao CHECK (decisao IN ('CONFIRMADA','REJEITADA')),
    CONSTRAINT CK_AvalEmerg_gravidade CHECK ((decisao = 'CONFIRMADA' AND gravidade BETWEEN 1 AND 4) OR (decisao = 'REJEITADA' AND gravidade IS NULL))
);
CREATE INDEX IX_AvaliacaoEmergencia_vazamento_id ON AvaliacaoEmergencia (vazamento_id);
CREATE INDEX IX_AvaliacaoEmergencia_sinalizacao_id ON AvaliacaoEmergencia (sinalizacao_id);
CREATE INDEX IX_AvaliacaoEmergencia_operador_id ON AvaliacaoEmergencia (operador_id);

CREATE TABLE TriagemTecnica (
    id BIGINT IDENTITY(1,1) NOT NULL,
    vazamento_id BIGINT NOT NULL,
    situacao NVARCHAR(20) NOT NULL CONSTRAINT DF_TriagemTecnica_situacao DEFAULT ('ABERTA'),
    motivo NVARCHAR(500) NOT NULL,
    responsavel_id BIGINT NULL,
    criado_em DATETIMEOFFSET(3) NOT NULL CONSTRAINT DF_TriagemTecnica_criado_em DEFAULT (CAST(SYSUTCDATETIME() AS DATETIMEOFFSET(3))),
    concluido_em DATETIMEOFFSET(3) NULL,
    CONSTRAINT PK_TriagemTecnica PRIMARY KEY (id),
    CONSTRAINT FK_TriagemTecnica_vazamento_id FOREIGN KEY (vazamento_id) REFERENCES Vazamento (id),
    CONSTRAINT FK_TriagemTecnica_responsavel_id FOREIGN KEY (responsavel_id) REFERENCES Usuario (id),
    CONSTRAINT CK_Triagem_situacao CHECK (situacao IN ('ABERTA','CONCLUIDA'))
);
CREATE INDEX IX_TriagemTecnica_vazamento_id ON TriagemTecnica (vazamento_id);
CREATE INDEX IX_TriagemTecnica_responsavel_id ON TriagemTecnica (responsavel_id);


-- No máximo um intervalo aberto (fim nulo) ativo por vazamento.
CREATE UNIQUE INDEX UX_Intervalo_aberto ON IntervaloEstimativa (vazamento_id) WHERE fim IS NULL AND situacao = 'ATIVO';
CREATE INDEX IX_Intervalo_vazamento_inicio ON IntervaloEstimativa (vazamento_id, inicio);
CREATE UNIQUE INDEX UX_Matriz_vigente ON MatrizPrazoAtendimento (porte_id, infraestrutura_id) WHERE vigente_ate IS NULL;
CREATE UNIQUE INDEX UX_Triagem_aberta ON TriagemTecnica (vazamento_id) WHERE situacao = 'ABERTA';
ALTER TABLE Vazamento ADD CONSTRAINT FK_Vazamento_matriz FOREIGN KEY (matriz_prazo_id) REFERENCES MatrizPrazoAtendimento (id);
CREATE INDEX IX_Vazamento_matriz_prazo_id ON Vazamento (matriz_prazo_id);
