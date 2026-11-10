-- Monitoramento de integridade (RNF011): deve sempre retornar zero linhas.
CREATE VIEW vw_intervalos_sobrepostos AS
SELECT a.vazamento_id, a.id AS intervalo_a, b.id AS intervalo_b
FROM IntervaloEstimativa a
JOIN IntervaloEstimativa b
  ON b.vazamento_id = a.vazamento_id AND b.id > a.id
WHERE a.situacao = 'ATIVO' AND b.situacao = 'ATIVO'
  AND a.inicio < COALESCE(b.fim, CAST('9999-12-31T23:59:59.997+00:00' AS DATETIMEOFFSET(3)))
  AND b.inicio < COALESCE(a.fim, CAST('9999-12-31T23:59:59.997+00:00' AS DATETIMEOFFSET(3)));
GO

-- Auditoria e histórico de status são imutáveis (RF026).
CREATE TRIGGER TR_Auditoria_imutavel ON Auditoria INSTEAD OF UPDATE, DELETE AS
BEGIN
    THROW 51000, 'Auditoria é imutável: UPDATE e DELETE não são permitidos.', 1;
END;
GO
CREATE TRIGGER TR_HistoricoStatus_imutavel ON HistoricoStatus INSTEAD OF UPDATE, DELETE AS
BEGIN
    THROW 51001, 'HistoricoStatus é imutável: UPDATE e DELETE não são permitidos.', 1;
END;
GO
