
IF COL_LENGTH('Usuario', 'cpf') IS NULL
BEGIN
    ALTER TABLE Usuario
    ADD cpf NVARCHAR(11) NULL;
END;
GO

IF NOT EXISTS (
    SELECT 1
    FROM sys.indexes
    WHERE name = 'UX_Usuario_cpf'
      AND object_id = OBJECT_ID('Usuario')
)
BEGIN
    CREATE UNIQUE INDEX UX_Usuario_cpf
    ON Usuario (cpf)
    WHERE cpf IS NOT NULL;
END;
GO
