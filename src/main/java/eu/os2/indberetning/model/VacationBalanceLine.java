package eu.os2.indberetning.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class VacationBalanceLine {
    private String cpr;
    private int extraNumber;
    private int vacationEarnedYear;
    private double freeVacationHoursTotal;
    private double transferredVacationHours;
    private double vacationHoursWithPay;
    private Date updatedDate;
}
