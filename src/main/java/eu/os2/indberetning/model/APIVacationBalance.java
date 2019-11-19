package eu.os2.indberetning.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class APIVacationBalance {
    private int vacationEarnedYear;
    private double freeVacationHoursTotal;
    private double transferredVacationHours;
    private double vacationHoursWithPay;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date updatedDate;
}