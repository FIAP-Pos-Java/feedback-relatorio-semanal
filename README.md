Sistema de Feedback - Relatório Semanal

Sistema serverless que recebe feedbacks, envia alertas críticos e faz relatórios semanais.

## O que faz
1. Recebe avaliações - endpoint com descrição e nota (0-10)
2. Envia alertas - se nota ≤ 3, manda email
3. Gera relatórios - calcula média semanal

## Como funciona
Quando recebe POST /avaliacao, salva no DynamoDB.
Se nota for ≤ 3, publica no SNS, que dispara lambda para enviar email.

EventBridge roda semanalmente, lambda busca no DynamoDB, calcula média e faz log.

## Tecnologias
Java 21, Quarkus, AWS Lambda, DynamoDB, SNS, SES, EventBridge, CloudWatch

## Como rodar
./mvnw quarkus:dev

Roda em http://localhost:9091
Docker precisa estar ligado.

## Testar
curl -X POST http://localhost:9091/avaliacao \
  -H "Content-Type: application/json" \
  -d '{"descricao": "Aula boa!", "nota": 9}'

Mais exemplos no CURL_EXAMPLES.md

## Configuração
Variáveis de ambiente ou application.properties:
- AWS_REGION (padrão: us-east-1)
- DYNAMODB_TABLE_NAME (padrão: feedbacks)
- SNS_TOPIC_ARN
- SES_EMAIL_DESTINO (padrão: eduardaclx@gmail.com)
- SES_EMAIL_REMETENTE (verificado no SES)

## Na AWS precisa
1. DynamoDB: tabela "feedbacks" com id e dataCriacao
2. SNS: tópico com subscription para lambda de alertas
3. SES: verificar emails remetente e destino
4. EventBridge: cron semanal para relatório
5. IAM: permissões para DynamoDB, SNS, SES, CloudWatch

## Build
./mvnw clean package

JAR fica em target/feedback-1.0.0-SNAPSHOT-runner.jar

## Deploy
upload na AWS Console ou usa SAM/Serverless.

## Problemas?
- Verifica variáveis de ambiente
- Permissões IAM
- Logs no CloudWatch
