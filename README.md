# Sistema de Feedback - AWS Lambda

Sistema serverless que recebe feedbacks de alunos, envia alertas quando são críticos e gera relatórios semanais.

## O que faz?

1. **Recebe avaliações** - Endpoint que recebe feedbacks com descrição e nota (0 a 10)
2. **Envia alertas** - Se a nota for ≤ 3, envia email automático
3. **Gera relatórios** - Toda semana calcula a média das notas

## Como funciona?

```
POST /avaliacao → Salva no DynamoDB
                    ↓
              Se nota ≤ 3 → SNS → Lambda → Envia email

EventBridge (semanal) → Lambda → Busca no DynamoDB → Calcula média → Log
```

## Tecnologias

- Java 21
- Quarkus
- AWS Lambda, DynamoDB, SNS, SES, EventBridge, CloudWatch

## Como rodar?

```bash
./mvnw quarkus:dev
```

Depois acessa: `http://localhost:9091`

PS: CERTIFIQUE-SE QUE O DOCKER ESTÁ FUNCIONANDO

### Testar

```bash
curl -X POST http://localhost:9091/avaliacao \
  -H "Content-Type: application/json" \
  -d '{"descricao": "Aula boa!", "nota": 9}'
```

Tem mais exemplos no arquivo [CURL_EXAMPLES.md](CURL_EXAMPLES.md)

## Estrutura

```
src/main/java/com/feedback/
 config/      # Config AWS
 dto/         # DTOs
 lambda/      # Handlers Lambda
 model/       # Entidades
 repository/  # DynamoDB
 resource/    # Endpoint REST
 service/     # Lógica
```

## Configuração

Variáveis de ambiente (ou no `application.properties`):

- `AWS_REGION` - Região (padrão: us-east-1)
- `DYNAMODB_TABLE_NAME` - Nome da tabela (padrão: feedbacks)
- `SNS_TOPIC_ARN` - ARN do tópico SNS
- `SES_EMAIL_DESTINO` - Email destino (padrão: eduardaclx@gmail.com)
- `SES_EMAIL_REMETENTE` - Email remetente (precisa estar verificado no SES)

## O que precisa na AWS?

1. **DynamoDB**: Tabela `feedbacks`
   - `id` (String)
   - `dataCriacao` (String)

2. **SNS**: Tópico com subscription para a Lambda de alertas

3. **SES**: Verificar emails (remetente e destino)

4. **EventBridge**: Regra de cron semanal para relatório

5. **IAM**: Permissões para DynamoDB, SNS, SES, CloudWatch

## Build e Deploy

```bash
./mvnw clean package
```

O JAR fica em `target/feedback-1.0.0-SNAPSHOT-runner.jar`

Para deploy, faz upload do JAR na AWS Console ou usa SAM/Serverless Framework.

## Dicas

- Se der erro, verifica as variáveis de ambiente
- Checa as permissões IAM
- Olha os logs no CloudWatch
