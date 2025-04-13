# HubSpot Integration API

Esta aplicação Java + Spring Boot integra com a API pública da HubSpot, oferecendo suporte para autenticação OAuth2, criação, atualização e leitura de contatos, além de processamento de Webhooks.

---

## 🔧 Tecnologias Utilizadas

- Java 21
- Spring Boot 3.4.4
- Spring Security (OAuth2 Client)
- WebClient (Spring WebFlux)
- Maven
- HubSpot Public API
- Ngrok (para testes locais com Webhooks)

---

## 🚀 Como Rodar Localmente

1. Clone o repositório:
```bash
git clone <repo>
```

2. Navegue até o diretório:
```bash
cd hubspotintegration
```

3. Rode a aplicação com:
```bash
./mvnw spring-boot:run
```

A aplicação será iniciada na porta `8085`.

---

## 🔐 Configuração do OAuth2

Para que a integração funcione corretamente com a API pública da HubSpot, é necessário configurar o `client_id` e `client_secret` de um aplicativo criado na HubSpot.

Essas credenciais devem ser atualizadas no arquivo `application.properties`:

```properties
hubspot.client-id=SEU_CLIENT_ID
hubspot.client-secret=SEU_CLIENT_SECRET
hubspot.redirect-uri=https://SEU_DOMINIO/auth/callback
```

> ⚠️ **IMPORTANTE:** Nunca compartilhe seu `client_secret` em repositórios públicos.

---

## 🌐 Teste Local com Ngrok

Para testar a aplicação localmente com webhooks ou a autorização OAuth2, você pode utilizar o [Ngrok](https://ngrok.com/) para expor sua aplicação local:

1. Rode sua aplicação local na porta `8085` (ou a definida).
2. Execute o comando:

```bash
ngrok http 8085
```

3. Pegue o link `https://...ngrok-free.app` gerado e atualize o `redirect-uri` no `application.properties` e na configuração do seu app HubSpot.

**Exemplo:**

```properties
hubspot.redirect-uri=https://seu-subdominio.ngrok-free.app/auth/callback
```

⚠️ Lembre-se de sempre alinhar esse link com o que está cadastrado na HubSpot como URI de redirecionamento.

---

## 🔁 Endpoints Disponíveis

### Autenticação

- `GET /auth/authorize`  
  Redireciona para o login do HubSpot

- `GET /auth/callback`  
  Recebe o authorization code e troca por um access token.

### Contatos

- `POST /contact`  
  Cria um novo contato no HubSpot (ou atualiza se já existir pelo e-mail)

- `PUT /contact/{id}`  
  Atualiza um contato existente pelo ID do HubSpot

- `GET /contact`  
  Lista todos os contatos

- `GET /contact/{id}`  
  Retorna um contato pelo ID

### Webhooks

- `POST /webhook`  
  Recebe notificações do HubSpot como `contact.creation`, processa e cria/atualiza o contato.

---

## 📦 Estrutura do Projeto

- `controller` - Define os endpoints da API
- `service` - Contém as regras de negócio (integração com HubSpot, processamento de webhook, etc.)
- `dto` - Define os objetos de transferência (request/response)
- `config` - Configurações do WebClient, OAuth2, entre outros
- `exception` - Tratamento global de exceções
- `common` - Utilitários diversos (como criptografia)

---

## ⚠️ Tratamento de Erros

A API conta com tratamento global usando `@ControllerAdvice`, que encapsula erros e evita exposição de dados sensíveis. Exceções específicas como `ContactCreationException` e `ContactFetchException` são tratadas de forma segura.

---

## 📒 Observações

- Os tokens são armazenados em memória para simplificação.
- O controle de escopo de acesso usa:
  - `crm.objects.contacts.read`
  - `crm.objects.contacts.write`
  - `oauth`

---

## 📬 Contato

Em caso de dúvidas ou sugestões, entre em contato com o mantenedor do projeto.

---

**Licença**: MIT

---

## 📚 Documentação Técnica

### Estrutura de Pacotes

- **auth**: Contém classes relacionadas à autenticação OAuth2 com a HubSpot (ex: `TokenData`, `TokenResponse`). Justifica-se a separação para modularizar a gestão de tokens e facilitar a extensão futura, mantendo o SRP do SOLID.
- **config**: Centraliza configurações do projeto, como `SecurityConfig`, `WebClientFactory` e tratamento de erros. Isso melhora a manutenção e promove reutilização.
- **contact**: Define os DTOs utilizados nas integrações com a API de contatos da HubSpot. Isso separa claramente as representações de dados da lógica de negócio.
- **controller**: Agrupa endpoints REST, separando responsabilidades entre autenticação, contatos, webhooks e erros.
- **exception**: Gerencia exceções personalizadas para falhas comuns, promovendo tratamento unificado e seguro.
- **service**: Implementa a lógica de negócio principal. Cada classe tem uma responsabilidade clara (ex: `ContactService` gerencia contatos, `WebhookService` lida com notificações).
- **util**: Utilitários genéricos como criptografia e geração de URLs. São ferramentas de apoio reutilizáveis.
- **webhook**: Agrupa modelos e regras específicas para processar notificações de eventos externos da HubSpot.

### Decisões Técnicas

- **Spring Boot + WebClient**: WebClient foi usado por sua natureza reativa e fluída, ideal para chamadas HTTP assíncronas à API da HubSpot.
- **OAuth2 Manual**: O controle explícito do fluxo OAuth2 permite personalizações como armazenar tokens manualmente e usar refresh tokens com mais segurança.
- **Logging com SLF4J/Logback**: Permite rastreamento detalhado de execuções com logs sem acoplamento à implementação.
- **Exceções customizadas**: Aplicadas para identificar rapidamente falhas em integrações com a HubSpot, de forma segura e descritiva.
- **Principais práticas SOLID**:
  - SRP: Cada classe tem uma responsabilidade única.
  - OCP: Serviços e handlers podem ser estendidos sem alteração no núcleo.
  - DIP: Injeção de dependências evita acoplamento direto.
- **Webhooks da HubSpot**: Habilitados para receber notificações assíncronas de criação de contato. Utiliza o `objectId` para buscar os dados completos.

### Segurança

- Tokens OAuth2 são armazenados em memória temporariamente e nunca logados.
- O projeto evita retornar stacktraces completos ou dados sensíveis para o cliente.
- Controle de exceções centralizado evita vazamentos de detalhes da infraestrutura.
- URL de redirecionamento OAuth2 deve sempre corresponder ao domínio real de uso (ex: `https://seu-dominio.com/auth/callback`), e não ao ngrok utilizado apenas para testes.