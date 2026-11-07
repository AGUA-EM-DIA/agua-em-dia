-- Dados de configuração iniciais (todos ajustáveis em runtime pela API de configuração).

-- Matriz de prazo efetivo em horas (Requisitos V1.1, seção 7.3).
INSERT INTO MatrizPrazoAtendimento (porte_id, infraestrutura_id, prazo_horas, vigente_desde)
SELECT p.id, i.id, v.horas, CAST(SYSUTCDATETIME() AS DATETIMEOFFSET(3))
FROM (VALUES
 ('GOTEJAMENTO','CAVALETE',72),('GOTEJAMENTO','RAMAL',72),('GOTEJAMENTO','REDE',48),('GOTEJAMENTO','ADUTORA',24),
 ('FIO_DAGUA','CAVALETE',48),('FIO_DAGUA','RAMAL',48),('FIO_DAGUA','REDE',24),('FIO_DAGUA','ADUTORA',12),
 ('JATO_MEDIO','CAVALETE',24),('JATO_MEDIO','RAMAL',24),('JATO_MEDIO','REDE',12),('JATO_MEDIO','ADUTORA',6),
 ('JATO_FORTE','CAVALETE',12),('JATO_FORTE','RAMAL',12),('JATO_FORTE','REDE',6),('JATO_FORTE','ADUTORA',3),
 ('JORRO_GEISER','CAVALETE',4),('JORRO_GEISER','RAMAL',4),('JORRO_GEISER','REDE',2),('JORRO_GEISER','ADUTORA',1)
) AS v (porte, infra, horas)
JOIN PorteVazamento p ON p.codigo = v.porte
JOIN TipoInfraestrutura i ON i.codigo = v.infra;

INSERT INTO ParametroSistema (chave, valor, descricao) VALUES
 ('duplicidade.raio_metros',         '50',      N'Raio de busca de candidatos a duplicidade (m)'),
 ('duplicidade.janela_horas',        '72',      N'Janela de busca de candidatos a duplicidade (h)'),
 ('concentracao.raio_metros',        '100',     N'Raio de concentração geográfica (m)'),
 ('prioridade.bonus_por_cidadao',    '0.05',    N'Bônus R por cidadão distinto adicional'),
 ('prioridade.bonus_maximo_relatos', '0.30',    N'Teto de R'),
 ('prioridade.bonus_por_vizinho',    '0.05',    N'Bônus G por vizinho ativo e independente'),
 ('prioridade.bonus_maximo_concentracao', '0.20', N'Teto de G'),
 ('prioridade.recalculo_minutos',    '15',      N'Periodicidade do recálculo (pendente de definição)'),
 ('economia.janela_horas',           '720',     N'Janela da economia potencial hipotética (30 dias)'),
 ('consumo.familiar_litros_mes',     '12000',   N'Consumo familiar mensal de referência (ilustrativo)'),
 ('imagem.limite_bytes',             '10485760',N'Limite por imagem (PROPOSTA: 10 MB)'),
 ('imagem.maximo_por_contexto',      '3',       N'Imagens por relato e por atendimento'),
 ('token.verificacao_horas',         '24',      N'Validade do token de confirmação de e-mail (a aprovar)'),
 ('token.recuperacao_minutos',       '30',      N'Validade do token de recuperação de senha'),
 ('geo.latitude_minima',             '-34.0',   N'Caixa geográfica aceita (Brasil, com folga)'),
 ('geo.latitude_maxima',             '6.0',     N'Caixa geográfica aceita'),
 ('geo.longitude_minima',            '-74.5',   N'Caixa geográfica aceita'),
 ('geo.longitude_maxima',            '-34.0',   N'Caixa geográfica aceita'),
 ('notificacao.maximo_tentativas',   '5',       N'Tentativas de envio de e-mail antes de marcar FALHA');
