package com.qmasters.fila_flex.dto;

import com.qmasters.fila_flex.model.Adress;
import com.qmasters.fila_flex.model.AppointmentTypeDetails;

public class AppointmentTypeDTO {
    private AppointmentTypeDetails appointmentTypeDetails;
    private Integer estimatedTime;
    private Adress adress;

    public AppointmentTypeDTO() {}
    
    public AppointmentTypeDTO(AppointmentTypeDetails appointmentTypeDetails, Integer estimatedTime, Adress adress) {
        this.appointmentTypeDetails = appointmentTypeDetails;
        this.estimatedTime = estimatedTime;
        this.adress = adress;
    }

    public AppointmentTypeDetails getAppointmentTypeDetails() {
        return appointmentTypeDetails;
    }
    
    public void setAppointmentTypeDetails(AppointmentTypeDetails appointmentTypeDetails) {
        this.appointmentTypeDetails = appointmentTypeDetails;
    }
    
    public Integer getEstimatedTime() {
        return estimatedTime;
    }
    
    public void setEstimatedTime(Integer estimatedTime) {
        this.estimatedTime = estimatedTime;
    }

    public Adress getAdress() {
        return adress;
    }

    public void setAdress(Adress adress) {
        this.adress = adress;
    }
}
