package com.feedback.repository;

import com.feedback.model.Feedback;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.ScanEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class FeedbackRepository {

    private static final Logger LOG = Logger.getLogger(FeedbackRepository.class);

    @Inject
    DynamoDbEnhancedClient dynamoDbEnhancedClient;

    private String tableName;

    public void initTableName(String tableName) {
        this.tableName = tableName != null && !tableName.isEmpty() 
            ? tableName 
            : "feedbacks";
    }

    public Feedback salvar(Feedback feedback) {
        try {
            DynamoDbTable<Feedback> table = getTable();
            table.putItem(feedback);
            LOG.infof("Feedback salvo com sucesso. ID: %s", feedback.getId());
            return feedback;
        } catch (DynamoDbException e) {
            LOG.errorf(e, "Erro ao salvar feedback no DynamoDB. ID: %s", feedback.getId());
            throw new RuntimeException("Erro ao salvar feedback no DynamoDB", e);
        }
    }

    public List<Feedback> buscarPorPeriodo(int dias) {
        try {
            Instant dataLimite = Instant.now().minus(dias, ChronoUnit.DAYS);
            String dataLimiteStr = dataLimite.toString();

            DynamoDbTable<Feedback> table = getTable();
            
            // Em produção, considere usar GSI para melhor performance
            List<Feedback> feedbacks = table.scan(ScanEnhancedRequest.builder().build())
                .items()
                .stream()
                .filter(f -> f.getDataCriacao() != null && 
                            f.getDataCriacao().compareTo(dataLimiteStr) >= 0)
                .collect(Collectors.toList());

            LOG.infof("Encontrados %d feedbacks nos últimos %d dias", feedbacks.size(), dias);
            return feedbacks;
        } catch (DynamoDbException e) {
            LOG.errorf(e, "Erro ao buscar feedbacks por período. Dias: %d", dias);
            throw new RuntimeException("Erro ao buscar feedbacks no DynamoDB", e);
        }
    }

    public Feedback buscarPorId(String id, String dataCriacao) {
        try {
            DynamoDbTable<Feedback> table = getTable();
            Key key = Key.builder()
                .partitionValue(id)
                .sortValue(dataCriacao)
                .build();
            
            return table.getItem(key);
        } catch (DynamoDbException e) {
            LOG.errorf(e, "Erro ao buscar feedback. ID: %s", id);
            throw new RuntimeException("Erro ao buscar feedback no DynamoDB", e);
        }
    }

    private DynamoDbTable<Feedback> getTable() {
        if (tableName == null) {
            initTableName(System.getenv("DYNAMODB_TABLE_NAME"));
        }
        return dynamoDbEnhancedClient.table(tableName, TableSchema.fromBean(Feedback.class));
    }
}

