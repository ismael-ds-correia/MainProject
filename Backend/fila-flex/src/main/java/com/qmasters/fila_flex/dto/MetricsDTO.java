package com.qmasters.fila_flex.dto;

/**
 * DTO para representar métricas de agendamentos.
 * 
 * Esta classe contém estatísticas relacionadas aos agendamentos, incluindo
 * a contagem total de agendamentos concluídos, o tempo médio de espera 
 * (em minutos) entre check-in e início do atendimento, e o tempo médio 
 * de serviço/atendimento (em minutos).
 */

public class MetricsDTO {
    Integer totalAppointmentsCompleteds;
    Integer averageWaitingTime;
    Integer averageServiceTime;

    public MetricsDTO(Integer totalAppointmentsCompleteds, Integer averageWaitingTime, Integer averageServiceTime) {
        this.totalAppointmentsCompleteds = totalAppointmentsCompleteds;
        this.averageWaitingTime = averageWaitingTime;
        this.averageServiceTime = averageServiceTime;
    }

    public Integer getTotalAppointmentsCompleteds() {
        return totalAppointmentsCompleteds;
    }

    public Integer getAverageWaitingTime() {
        return averageWaitingTime;
    }

    public Integer getAverageServiceTime() {
        return averageServiceTime;
    }

    public void setTotalAppointmentsCompleteds(Integer totalAppointmentsCompleteds) {
        this.totalAppointmentsCompleteds = totalAppointmentsCompleteds;
    }

    public void setAverageWaitingTime(Integer averageWaitingTime) {
        this.averageWaitingTime = averageWaitingTime;
    }

    public void setAverageServiceTime(Integer averageServiceTime) {
        this.averageServiceTime = averageServiceTime;
    }
}
