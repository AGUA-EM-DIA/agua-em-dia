-- V3__vazamento_e_classificacao
-- Gerado a partir da especificação única de tabelas; revisado manualmente nos índices e regras.

CREATE TABLE Vazamento (
    id BIGINT IDENTITY(1,1) NOT NULL,
    categoria_id BIGINT NOT NULL,
    localizacao_atual_id BIGINT NOT NULL,
    status NVARCHAR(30) NOT NULL CONSTRAINT DF_Vazamento_status DEFAULT ('PROVISORIA'),
    primeiro_relato_valido_em DATETIMEOFFSET(3) NOT NULL,
    prazo_inicio_em DATETIMEOFFSET(3) NOT NULL,
    criado_em DATETIMEOFFSET(3) NOT NULL CONSTRAINT DF_Vazamento_criado_em DEFAULT (CAST(SYSUTCDATETIME() AS DATETIMEOFFSET(3))),
    resolvido_em DATETIMEOFFSET(3) NULL,
    prazo_efetivo_horas INT NULL,
    matriz_prazo_id BIGINT NULL,
    vazao_incerta BIT NOT NULL CONSTRAINT DF_Vazamento_vazao_incerta DEFAULT (0),
    prioridade_atual DECIMAL(18,6) NULL,
    prioridade_calculada_em DATETIMEOFFSET(3) NULL,
    versao BIGINT NOT NULL CONSTRAINT DF_Vazamento_versao DEFAULT (0),
    CONSTRAINT PK_Vazamento PRIMARY KEY (id),
    CONSTRAINT FK_Vazamento_categoria_id FOREIGN KEY (categoria_id) REFERENCES CategoriaOcorrencia (id),
    CONSTRAINT FK_Vazamento_localizacao_atual_id FOREIGN KEY (localizacao_atual_id) REFERENCES Localizacao (id),
    CONSTRAINT CK_Vazamento_status CHECK (status IN ('PROVISORIA','EM_ANALISE','CONFIRMADA','AGUARDANDO_ATENDIMENTO','EM_ATENDIMENTO','AGUARDANDO_VALIDACAO','RESOLVIDA','REABERTA','DUPLICADA','CANCELADA'))
);
CREATE INDEX IX_Vazamento_categoria_id ON Vazamento (categoria_id);
CREATE INDEX IX_Vazamento_localizacao_atual_id ON Vazamento (localizacao_atual_id);

CREATE TABLE ClassificacaoTecnica (
    id BIGINT IDENTITY(1,1) NOT NULL,
    vazamento_id BIGINT NOT NULL,
    porte_id BIGINT NOT NULL,
    infraestrutura_id BIGINT NULL,
    operador_id BIGINT NOT NULL,
    vigente_desde DATETIMEOFFSET(3) NOT NULL,
    vigente_ate DATETIMEOFFSET(3) NULL,
    motivo NVARCHAR(500) NULL,
    CONSTRAINT PK_ClassificacaoTecnica PRIMARY KEY (id),
    CONSTRAINT FK_ClassificacaoTecnica_vazamento_id FOREIGN KEY (vazamento_id) REFERENCES Vazamento (id),
    CONSTRAINT FK_ClassificacaoTecnica_porte_id FOREIGN KEY (porte_id) REFERENCES PorteVazamento (id),
    CONSTRAINT FK_ClassificacaoTecnica_infraestrutura_id FOREIGN KEY (infraestrutura_id) REFERENCES TipoInfraestrutura (id),
    CONSTRAINT FK_ClassificacaoTecnica_operador_id FOREIGN KEY (operador_id) REFERENCES Usuario (id)
);
CREATE INDEX IX_ClassificacaoTecnica_vazamento_id ON ClassificacaoTecnica (vazamento_id);
CREATE INDEX IX_ClassificacaoTecnica_porte_id ON ClassificacaoTecnica (porte_id);
CREATE INDEX IX_ClassificacaoTecnica_infraestrutura_id ON ClassificacaoTecnica (infraestrutura_id);
CREATE INDEX IX_ClassificacaoTecnica_operador_id ON ClassificacaoTecnica (operador_id);


CREATE INDEX IX_Vazamento_status ON Vazamento (status) INCLUDE (prioridade_atual, primeiro_relato_valido_em);
CREATE INDEX IX_Vazamento_primeiro_relato ON Vazamento (primeiro_relato_valido_em);
CREATE INDEX IX_Vazamento_resolvido_em ON Vazamento (resolvido_em);
-- Uma classificação técnica vigente por vazamento.
CREATE UNIQUE INDEX UX_ClassificacaoTecnica_vigente ON ClassificacaoTecnica (vazamento_id) WHERE vigente_ate IS NULL;
