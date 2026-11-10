# Água em Dia — relatório de reorganização

A estrutura foi reorganizada a partir do ZIP enviado, preservando o código existente. Esta etapa NÃO valida compilação, inicialização, conexão com banco ou funcionalidades.

## Estrutura
- `backend/`: projeto Maven Spring Boot, código Java em `src/main/java/br/com/aguaemdia/`, migrations e configuração em `src/main/resources/`.
- `frontend/prototipo/`: HTML original do protótipo, sem alterações.
- `docs/README-original.md`: README original.
- `docs/legacy/`: classe principal alternativa preservada como texto, fora da compilação.

## Ajustes estruturais efetuados
- 135 classes Java de `br.com.aguaemdia` organizadas pelo package declarado.
- `StatusController.java`: declaração package alterada de `br.aguaemdia.aguaemdia_api.controller` para `br.com.aguaemdia.controller`.
- `AguaemdiaApiApplication.java`: preservada em docs/legacy como referência; a classe principal ativa é `AguaEmDiaApplication.java`.
- Maven Wrapper (`mvnw`, `mvnw.cmd`, `.mvn/wrapper/maven-wrapper.properties`), `pom.xml`, Docker Compose e scripts SQL preservados.

## Problemas pendentes (não corrigidos)
1. `AuthController`: instancia `MensaguemResponse` mas importa `MensagemResponse` (erro de compilação).
2. `application.yml`: possui valores padrão de senha de banco e segredo JWT. Trocar por variáveis de ambiente antes de compartilhar ou publicar.
3. Necessário validar imports e referências após a realocação, além de verificar mapeamentos JPA, migrations e configuração de segurança.
4. Não foram encontrados testes Java na origem.
5. Maven e banco SQL Server não foram executados neste ambiente. O projeto NÃO está confirmado funcional.

## Próximos passos no VS Code (Windows)
Abra a pasta `agua-em-dia/backend` e execute `java -version` e `./mvnw.cmd clean compile` (no PowerShell, use `.\mvnw.cmd clean compile`). Envie os primeiros `[ERROR]` para corrigirmos juntos.

## Nota
Não foram apagadas entidades, serviços, repositórios, migrations ou o protótipo. A classe principal alternativa está arquivada para evitar ambiguidade.
