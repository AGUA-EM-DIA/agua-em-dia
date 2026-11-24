# Água em Dia

Aplicativo para cidadãos registrarem e acompanharem vazamentos de água.
Backend em **Java 21 + Spring Boot** com **MySQL 8**; frontend em HTML, CSS e JavaScript puros.

> **Estado atual (leia antes de rodar):**
> - **Banco (migrations):** testado de verdade num MySQL 8.0.
> - **Frontend ligado à API:** testado num navegador de verdade (Chromium) contra um servidor de mentira que imita a API
>   (29 verificações no modo demonstração e 5 no modo produção, todas passaram).
> - **Backend Java:** escrito com cuidado e com 17 testes de lógica pura executados, mas **nunca compilado nem executado com o Spring**
>   (o ambiente onde foi escrito não alcança o Maven Central). Rode `./mvnw test` e `./mvnw spring-boot:run` e corrija qualquer
>   erro de compilação que aparecer. É o ponto de maior risco.

## Estrutura

```
backend/                       projeto Maven (Spring Boot)
  src/main/java/...            controller, service, repository, entity, dto, security, config, exception
  src/main/resources/
    application.yml            configurações sem segredos (vai para o GitHub)
    application.properties.example   modelo do arquivo com SUAS senhas (copie e preencha; não vai para o GitHub)
    db/migration/              V1 e V2: criam e atualizam as tabelas (Flyway)
    static/                    frontend (index.html, css/app.css, js/app.js, js/api.js)
  src/test/                    testes (JUnit)
aguaEmDia_index.html           protótipo original, mantido como referência
teste.http                     chamadas de exemplo da API
```

## O que instalar

| Programa | Para quê | Link |
|---|---|---|
| JDK 21 (Eclipse Temurin) | rodar o Java | https://adoptium.net/temurin/releases/?version=21 |
| IntelliJ IDEA Community | editar e rodar | https://www.jetbrains.com/idea/download/ |
| MySQL Community Server | banco de dados | https://dev.mysql.com/downloads/mysql/ |
| MySQL Workbench | ver e consultar o banco | https://dev.mysql.com/downloads/workbench/ |
| Postman (ou a extensão REST Client) | testar a API | https://www.postman.com/downloads/ |

O Maven não precisa ser instalado: o projeto traz o `mvnw` (Windows: `mvnw.cmd`).
Use o MySQL **8.x** (8.0 ou 8.4 LTS). As migrations foram testadas no 8.0.

## Configurar

1. No MySQL Workbench, crie o banco (se ainda não existir): `CREATE DATABASE aguaEmDia CHARACTER SET utf8mb4;`
2. Em `backend/src/main/resources/`, copie `application.properties.example` para `application.properties` e preencha:
   a senha do MySQL e o segredo do JWT (um texto aleatório com 32 caracteres ou mais). Esse arquivo **não vai para o GitHub**.
3. O `ddl-auto` fica `none`: quem cria as tabelas é o Flyway, na primeira vez que a aplicação sobe.
   - Banco **novo**: V1 e V2 criam tudo.
   - Banco que **já tinha** a tabela `usuario`: ela é mantida (com seus dados) e V2 acrescenta o que falta. Nada é apagado.

Variáveis de ambiente (alternativa ao arquivo): `JWT_SECRET`, `APP_DEMO`, `GEMINI_API_KEY`, `GOOGLE_MAPS_API_KEY`,
`GOOGLE_CLIENT_ID`, `SERVICE_KEY`, `UPLOADS_DIR`, `MAIL_FROM`.

## Rodar

```
cd backend
./mvnw spring-boot:run        # Windows: mvnw.cmd spring-boot:run
```
Abra http://localhost:8080 (o frontend é servido pelo próprio backend, então não há problema de CORS).

## Modo demonstração (`app.demo=true`)

Existe para a apresentação do protótipo. Com ele ligado:
- qualquer cidadão logado pode mudar a situação de um vazamento (é o "painel de operação" simulado);
- o código de recuperação de senha também volta na resposta da API (para aparecer no Gmail simulado do celular).

**Em produção use `app.demo=false`.** Aí só quem tem a chave de serviço (`SERVICE_KEY`, cabeçalho `X-Chave-Servico`) muda a situação.

## API (todas as rotas, menos as marcadas, exigem `Authorization: Bearer <token>`)

| Método e rota | O que faz |
|---|---|
| `POST /api/usuarios` (pública) | cadastro (nome, nomeUsuario, cpf, email, telefone, senha, aceitouTermos) |
| `POST /api/auth/login` (pública) | login só com **e-mail e senha**; devolve o token |
| `POST /api/auth/recuperacao` · `POST /api/auth/redefinir-senha` (públicas) | código de 6 dígitos por e-mail (10 min, 5 tentativas) |
| `POST /api/auth/google` · `/api/auth/google/completar` (públicas) | login com Google (precisa de `GOOGLE_CLIENT_ID`) |
| `GET /api/config` (pública) | modo demo, chave do Maps, IA disponível |
| `GET /api/usuarios/me` · `/api/usuarios/me/estatisticas` | meus dados · estatísticas e conquistas |
| `POST /api/vazamentos` | registrar (multipart: `dados` JSON + `foto` opcional) |
| `GET /api/vazamentos` · `/meus` · `/{id}` | todos · só os meus · detalhe |
| `PATCH /api/vazamentos/{id}/descricao` | editar a descrição (só do dono) |
| `POST /api/vazamentos/{id}/apoios` | "eu também vi" |
| `POST /api/vazamentos/{id}/avaliacao` | avaliar (só o dono, só depois de RESOLVIDO, uma vez) |
| `POST /api/vazamentos/meus/vistos` | marca as novidades como vistas |
| `PATCH /api/vazamentos/{id}/situacao` | ABERTO → REPARO → RESOLVIDO (chave de serviço ou modo demo) |
| `POST /api/ia/identificar` | foto → tipo e tamanho do vazamento (Gemini; precisa de `GEMINI_API_KEY`) |
| `POST /api/aquari/mensagens` | assistente Aquari (perguntas frequentes) |

Exemplos prontos em `teste.http`. Erros vêm sempre como `{"mensagem": "..."}`.

**Estimativa de perda:** litros/hora do tamanho (gotejamento 3, fio 60, médio 600, forte 2.400, jorro 9.000)
vezes o multiplicador do tipo (adutora 3, rede 1,6, ramal 0,7, desperdício 0,5); ligação clandestina é fixa em 800 L/h.
Os litros perdidos são calculados na hora, não gravados.

## Testes

```
cd backend
# PowerShell:  $env:TEST_DB_PASSWORD="sua_senha"      Linux/Mac:  export TEST_DB_PASSWORD="sua_senha"
./mvnw test
```
Os testes usam um banco **separado** (`aguaEmDia_teste`, criado sozinho) e nunca mexem nos seus dados.
Cobrem: inicialização e conexão com o MySQL, cadastro (válido, inválido, CPF e e-mail duplicados), login (correto, senha errada,
CPF no lugar do e-mail), token inválido, recuperação de senha, registro de vazamento (com e sem foto, foto inválida,
coordenadas inválidas), consulta, edição, acesso de outros cidadãos, reforço, avaliação, situação, estatísticas e Aquari.

## Integrações que dependem de credenciais

| Recurso | O que fazer |
|---|---|
| Foto → tipo (Gemini) | criar chave em https://aistudio.google.com/apikey e definir `app.gemini.chave` |
| Mapa do Google | criar chave no Google Cloud Console, **restringir ao seu domínio** e definir `app.google.maps-key`. Sem ela o app usa o mapa esquemático |
| Login com Google | criar um "ID do cliente OAuth" (aplicativo da Web) e definir `app.google.client-id` |
| E-mail real (recuperação de senha) | configurar `spring.mail.*` (exemplo com Gmail e "senha de app" no `.example`) |
| WhatsApp real | **não implementado**: exige provedor aprovado (ex.: API oficial da Meta). O WhatsApp do celular do protótipo é simulado |

## Segurança: atenção

O protótipo original trazia uma **chave do Google Maps escrita no código** (`aguaEmDia_index.html`) e ela está no histórico do GitHub.
Entre no Google Cloud Console, **revogue ou restrinja essa chave** e crie outra. Nesta versão a chave não fica mais no código.

## O que mudou no frontend (em relação ao protótipo)

- O login e o cadastro agora são **reais** (antes, qualquer dado entrava). O login é só por e-mail e senha.
- Chamados, apoios ("eu também vi"), avaliações, situação e recuperação de senha vão para o servidor e para o MySQL.
- A tela mostra os registros de todos os cidadãos e se atualiza a cada 10 segundos.
- A identificação por foto (Gemini) passou para o servidor; a chave não fica mais no navegador.
- Removidos os textos explicativos ao redor do celular. Mantidos o WhatsApp e o Gmail simulados e o painel de operação,
  **só no modo demonstração** (`app.demo=true`). Os chamados fictícios do protótipo também só aparecem nesse modo, só na tela,
  com protocolo `SIM-...`, e nunca são gravados no banco.
- Corrigido um bug do protótipo: havia duas funções `hashTexto`, e a assíncrona sobrescrevia a síncrona.
- O "contato com o cidadão" do painel de operação (marcar que ligou depois de uma nota baixa) continua só na tela: não é gravado.

## O que falta

- **Compilar e rodar o backend na sua máquina** (veja o aviso no topo).
- **Testar o login com Google** (precisa de um `GOOGLE_CLIENT_ID` real) e a **identificação por foto** (precisa de `GEMINI_API_KEY`):
  o código existe, mas não pôde ser testado sem as chaves.
- Mostrar as **conquistas** na tela de perfil (a API já devolve em `/api/usuarios/me/estatisticas`; o perfil ainda calcula as estatísticas localmente).
- Interface do **Aquari** (a API existe em `/api/aquari/mensagens`, mas o protótipo não tem tela para ele).
- WhatsApp real, limite de tentativas de login e de registros por usuário, backup do MySQL, hospedagem e HTTPS.
