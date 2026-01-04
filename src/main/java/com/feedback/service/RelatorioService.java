package com.feedback.service;

import com.feedback.dto.RelatorioEstatisticas;
import com.feedback.model.Feedback;
import com.feedback.repository.FeedbackRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@ApplicationScoped
public class RelatorioService {

    private static final Logger LOG = Logger.getLogger(RelatorioService.class);

    @Inject
    FeedbackRepository feedbackRepository;

    public void gerarRelatorioSemanal() {
        var feedbacks = feedbackRepository.buscarPorPeriodo(7);
        var estatisticas = calcularEstatisticas(feedbacks);
        gerarLogRelatorio(estatisticas, feedbacks.size());
    }

    private RelatorioEstatisticas calcularEstatisticas(List<Feedback> feedbacks) {
        if (feedbacks.isEmpty()) {
            return new RelatorioEstatisticas(0.0, 0, 0, 0);
        }

        var somaNotas = 0.0;
        var totalCriticos = 0;
        var totalNormais = 0;
        var total = feedbacks.size();

        for (Feedback feedback : feedbacks) {
            somaNotas += feedback.getNota();
            if (feedback.getCritico() != null && feedback.getCritico()) {
                totalCriticos++;
            } else {
                totalNormais++;
            }
        }

        var mediaNotas = somaNotas / total;
        return new RelatorioEstatisticas(mediaNotas, total, totalCriticos, totalNormais);
    }

    private void gerarLogRelatorio(RelatorioEstatisticas estatisticas, int totalFeedbacks) {
        var agora = LocalDateTime.now();
        var inicioPeriodo = agora.minusDays(7);

        var dataInicio = DateTimeFormatter.ISO_LOCAL_DATE.format(inicioPeriodo);
        var dataFim = DateTimeFormatter.ISO_LOCAL_DATE.format(agora);

        LOG.infof(
            "\n" +
            "╔══════════════════════════════════════════════════════════════╗\n" +
            "║           RELATÓRIO SEMANAL DE FEEDBACKS                     ║\n" +
            "╠══════════════════════════════════════════════════════════════╣\n" +
            "║ Período: %s a %s                                    ║\n" +
            "║ Total de Feedbacks: %d                                        ║\n" +
            "║ Média das Notas: %.2f/10                                      ║\n" +
            "║ Feedbacks Críticos (nota ≤ 3): %d                             ║\n" +
            "║ Feedbacks Normais (nota > 3): %d                              ║\n" +
            "╚══════════════════════════════════════════════════════════════╝",
            dataInicio,
            dataFim,
            estatisticas.total(),
            estatisticas.mediaNotas(),
            estatisticas.totalCriticos(),
            estatisticas.totalNormais()
        );

        LOG.infof(
            "{\"tipo\":\"relatorio_semanal\",\"periodo\":{\"inicio\":\"%s\",\"fim\":\"%s\"}," +
            "\"estatisticas\":{\"total\":%d,\"media_notas\":%.2f,\"criticos\":%d,\"normais\":%d}}",
            dataInicio,
            dataFim,
            estatisticas.total(),
            estatisticas.mediaNotas(),
            estatisticas.totalCriticos(),
            estatisticas.totalNormais()
        );
    }
}

