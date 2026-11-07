-- V2__referencias_e_localizacao
-- Gerado a partir da especificação única de tabelas; revisado manualmente nos índices e regras.

CREATE TABLE Bairro (
    id BIGINT IDENTITY(1,1) NOT NULL,
    nome NVARCHAR(120) NOT NULL,
    nome_normalizado NVARCHAR(120) NOT NULL,
    municipio NVARCHAR(120) NULL,
    ativo BIT NOT NULL CONSTRAINT DF_Bairro_ativo DEFAULT (1),
    CONSTRAINT PK_Bairro PRIMARY KEY (id)
);


CREATE TABLE Localizacao (
    id BIGINT IDENTITY(1,1) NOT NULL,
    latitude DECIMAL(9,6) NOT NULL,
    longitude DECIMAL(9,6) NOT NULL,
    endereco NVARCHAR(300) NULL,
    bairro_id BIGINT NULL,
    origem_localizacao NVARCHAR(10) NOT NULL,
    registrada_em DATETIMEOFFSET(3) NOT NULL CONSTRAINT DF_Localizacao_registrada_em DEFAULT (CAST(SYSUTCDATETIME() AS DATETIMEOFFSET(3))),
    CONSTRAINT PK_Localizacao PRIMARY KEY (id),
    CONSTRAINT FK_Localizacao_bairro_id FOREIGN KEY (bairro_id) REFERENCES Bairro (id),
    CONSTRAINT CK_Localizacao_origem CHECK (origem_localizacao IN ('GPS','ENDERECO','MAPA')),
    CONSTRAINT CK_Localizacao_lat CHECK (latitude BETWEEN -90 AND 90),
    CONSTRAINT CK_Localizacao_lon CHECK (longitude BETWEEN -180 AND 180)
);
CREATE INDEX IX_Localizacao_bairro_id ON Localizacao (bairro_id);

CREATE TABLE CategoriaOcorrencia (
    id BIGINT IDENTITY(1,1) NOT NULL,
    codigo NVARCHAR(40) NOT NULL,
    nome NVARCHAR(100) NOT NULL,
    exige_estimativa_fisica BIT NOT NULL,
    ativo BIT NOT NULL CONSTRAINT DF_CategoriaOcorrencia_ativo DEFAULT (1),
    CONSTRAINT PK_CategoriaOcorrencia PRIMARY KEY (id)
);


CREATE TABLE PorteVazamento (
    id BIGINT IDENTITY(1,1) NOT NULL,
    codigo NVARCHAR(30) NOT NULL,
    nome NVARCHAR(60) NOT NULL,
    ordem INT NOT NULL,
    vazao_base_l_h DECIMAL(18,4) NOT NULL,
    ativo BIT NOT NULL CONSTRAINT DF_PorteVazamento_ativo DEFAULT (1),
    CONSTRAINT PK_PorteVazamento PRIMARY KEY (id),
    CONSTRAINT CK_Porte_vazao CHECK (vazao_base_l_h >= 0)
);


CREATE TABLE TipoInfraestrutura (
    id BIGINT IDENTITY(1,1) NOT NULL,
    codigo NVARCHAR(30) NOT NULL,
    nome NVARCHAR(60) NOT NULL,
    ativo BIT NOT NULL CONSTRAINT DF_TipoInfraestrutura_ativo DEFAULT (1),
    CONSTRAINT PK_TipoInfraestrutura PRIMARY KEY (id)
);



CREATE UNIQUE INDEX UX_Bairro_nome_normalizado ON Bairro (nome_normalizado);
CREATE UNIQUE INDEX UX_Categoria_codigo ON CategoriaOcorrencia (codigo);
CREATE UNIQUE INDEX UX_Porte_codigo ON PorteVazamento (codigo);
CREATE UNIQUE INDEX UX_Porte_ordem ON PorteVazamento (ordem);
CREATE UNIQUE INDEX UX_TipoInfra_codigo ON TipoInfraestrutura (codigo);

-- Coluna espacial preenchida pela aplicação (LocalizacaoService) logo após o INSERT.
ALTER TABLE Localizacao ADD geo GEOGRAPHY NULL;
GO
CREATE SPATIAL INDEX SIX_Localizacao_geo ON Localizacao (geo) USING GEOGRAPHY_AUTO_GRID;
GO

INSERT INTO CategoriaOcorrencia (codigo, nome, exige_estimativa_fisica, ativo) VALUES
 ('VAZAMENTO',           N'Vazamento físico',   1, 1),
 ('LIGACAO_CLANDESTINA', N'Ligação clandestina', 0, 1);

-- Vazões-base ILUSTRATIVAS (documentação 7.4): validar antes de produção.
INSERT INTO PorteVazamento (codigo, nome, ordem, vazao_base_l_h, ativo) VALUES
 ('GOTEJAMENTO',  N'Gotejamento',   1,    3.0000, 1),
 ('FIO_DAGUA',    N'Fio d''água',   2,   60.0000, 1),
 ('JATO_MEDIO',   N'Jato médio',    3,  600.0000, 1),
 ('JATO_FORTE',   N'Jato forte',    4, 2400.0000, 1),
 ('JORRO_GEISER', N'Jorro/gêiser',  5, 9000.0000, 1);

INSERT INTO TipoInfraestrutura (codigo, nome, ativo) VALUES
 ('CAVALETE', N'Cavalete', 1),
 ('RAMAL',    N'Ramal', 1),
 ('REDE',     N'Rede de distribuição', 1),
 ('ADUTORA',  N'Adutora', 1);
