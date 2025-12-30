package com.feedback.lambda;

import com.feedback.dto.EventBridgeEvent;
import com.feedback.service.RelatorioService;
import io.quarkus.funqy.Funq;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

@ApplicationScoped
public class RelatorioLambda {

    private static final Logger LOG = Logger.getLogger(RelatorioLambda.class);

    @Inject
    RelatorioService relatorioService;

    @Funq
    public void gerarRelatorioSemanal(EventBridgeEvent event) {
        try {
            LOG.info("Iniciando execução do relatório semanal agendado");
            
            relatorioService.gerarRelatorioSemanal();
            
            LOG.info("Relatório semanal executado com sucesso");
        } catch (Exception e) {
            LOG.errorf(e, "Erro ao executar relatório semanal");
            throw new RuntimeException("Erro ao gerar relatório semanal", e);
        }
    }
}

