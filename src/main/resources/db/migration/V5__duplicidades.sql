-- V5__duplicidades
-- Gerado a partir da especificação única de tabelas; revisado manualmente nos índices e regras.

CREATE TABLE AnaliseDuplicidade (
    id BIGINT IDENTITY(1,1) NOT NULL,
    origem_id BIGINT NOT NULL,
    candidato_id BIGINT NOT NULL,
    distancia_metros DECIMAL(10,2) NOT NULL,
    situacao NVARCHAR(20) NOT NULL CONSTRAINT DF_AnaliseDuplicidade_situacao DEFAULT ('SUGERIDA'),
    analisado_por BIGINT NULL,
    analisado_em DATETIMEOFFSET(3) NULL,
    criado_em DATETIMEOFFSET(3) NOT NULL CONSTRAINT DF_AnaliseDuplicidade_criado_em DEFAULT (CAST(SYSUTCDATETIME() AS DATETIMEOFFSET(3))),
    CONSTRAINT PK_AnaliseDuplicidade PRIMARY KEY (id),
    CONSTRAINT FK_AnaliseDuplicidade_origem_id FOREIGN KEY (origem_id) REFERENCES Vazamento (id),
    CONSTRAINT FK_AnaliseDuplicidade_candidato_id FOREIGN KEY (candidato_id) REFERENCES Vazamento (id),
    CONSTRAINT FK_AnaliseDuplicidade_analisado_por FOREIGN KEY (analisado_por) REFERENCES Usuario (id),
    CONSTRAINT CK_AnaliseDup_situacao CHECK (situacao IN ('SUGERIDA','CONFIRMADA','REJEITADA')),
    CONSTRAINT CK_AnaliseDup_auto CHECK (origem_id <> candidato_id)
);
CREATE INDEX IX_AnaliseDuplicidade_origem_id ON AnaliseDuplicidade (origem_id);
CREATE INDEX IX_AnaliseDuplicidade_candidato_id ON AnaliseDuplicidade (candidato_id);
CREATE INDEX IX_AnaliseDuplicidade_analisado_por ON AnaliseDuplicidade (analisado_por);

CREATE TABLE VinculoDuplicidade (
    id BIGINT IDENTITY(1,1) NOT NULL,
    origem_id BIGINT NOT NULL,
    canonico_id BIGINT NOT NULL,
    associado_por BIGINT NOT NULL,
    associado_em DATETIMEOFFSET(3) NOT NULL CONSTRAINT DF_VinculoDuplicidade_associado_em DEFAULT (CAST(SYSUTCDATETIME() AS DATETIMEOFFSET(3))),
    justificativa NVARCHAR(1000) NOT NULL,
    revertido_por BIGINT NULL,
    revertido_em DATETIMEOFFSET(3) NULL,
    motivo_reversao NVARCHAR(1000) NULL,
    CONSTRAINT PK_VinculoDuplicidade PRIMARY KEY (id),
    CONSTRAINT FK_VinculoDuplicidade_origem_id FOREIGN KEY (origem_id) REFERENCES Vazamento (id),
    CONSTRAINT FK_VinculoDuplicidade_canonico_id FOREIGN KEY (canonico_id) REFERENCES Vazamento (id),
    CONSTRAINT FK_VinculoDuplicidade_associado_por FOREIGN KEY (associado_por) REFERENCES Usuario (id),
    CONSTRAINT FK_VinculoDuplicidade_revertido_por FOREIGN KEY (revertido_por) REFERENCES Usuario (id),
    CONSTRAINT CK_VinculoDup_auto CHECK (origem_id <> canonico_id)
);
CREATE INDEX IX_VinculoDuplicidade_origem_id ON VinculoDuplicidade (origem_id);
CREATE INDEX IX_VinculoDuplicidade_canonico_id ON VinculoDuplicidade (canonico_id);
CREATE INDEX IX_VinculoDuplicidade_associado_por ON VinculoDuplicidade (associado_por);
CREATE INDEX IX_VinculoDuplicidade_revertido_por ON VinculoDuplicidade (revertido_por);

CREATE TABLE HistoricoVinculoRelato (
    id BIGINT IDENTITY(1,1) NOT NULL,
    relato_id BIGINT NOT NULL,
    vazamento_anterior_id BIGINT NOT NULL,
    vazamento_novo_id BIGINT NOT NULL,
    vinculo_id BIGINT NULL,
    alterado_por BIGINT NOT NULL,
    alterado_em DATETIMEOFFSET(3) NOT NULL CONSTRAINT DF_HistoricoVinculoRelato_alterado_em DEFAULT (CAST(SYSUTCDATETIME() AS DATETIMEOFFSET(3))),
    motivo NVARCHAR(1000) NULL,
    CONSTRAINT PK_HistoricoVinculoRelato PRIMARY KEY (id),
    CONSTRAINT FK_HistoricoVinculoRelato_relato_id FOREIGN KEY (relato_id) REFERENCES Relato (id),
    CONSTRAINT FK_HistoricoVinculoRelato_vazamento_anterior_id FOREIGN KEY (vazamento_anterior_id) REFERENCES Vazamento (id),
    CONSTRAINT FK_HistoricoVinculoRelato_vazamento_novo_id FOREIGN KEY (vazamento_novo_id) REFERENCES Vazamento (id),
    CONSTRAINT FK_HistoricoVinculoRelato_vinculo_id FOREIGN KEY (vinculo_id) REFERENCES VinculoDuplicidade (id),
    CONSTRAINT FK_HistoricoVinculoRelato_alterado_por FOREIGN KEY (alterado_por) REFERENCES Usuario (id)
);
CREATE INDEX IX_HistoricoVinculoRelato_relato_id ON HistoricoVinculoRelato (relato_id);
CREATE INDEX IX_HistoricoVinculoRelato_vazamento_anterior_id ON HistoricoVinculoRelato (vazamento_anterior_id);
CREATE INDEX IX_HistoricoVinculoRelato_vazamento_novo_id ON HistoricoVinculoRelato (vazamento_novo_id);
CREATE INDEX IX_HistoricoVinculoRelato_vinculo_id ON HistoricoVinculoRelato (vinculo_id);
CREATE INDEX IX_HistoricoVinculoRelato_alterado_por ON HistoricoVinculoRelato (alterado_por);


CREATE UNIQUE INDEX UX_AnaliseDuplicidade_par ON AnaliseDuplicidade (origem_id, candidato_id);
-- Uma única associação canônica vigente por ocorrência de origem.
CREATE UNIQUE INDEX UX_VinculoDuplicidade_vigente ON VinculoDuplicidade (origem_id) WHERE revertido_em IS NULL;
