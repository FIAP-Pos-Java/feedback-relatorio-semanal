package com.feedback.dto;

public record RelatorioEstatisticas(
    double mediaNotas,
    int total,
    int totalCriticos,
    int totalNormais
) {}

