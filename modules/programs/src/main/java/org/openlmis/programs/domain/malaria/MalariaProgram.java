package org.openlmis.programs.domain.malaria;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class MalariaProgram {
    private int id;
    private String username;
    private Date reportedDate;
    private Date periodStartDate;
    private Date periodEndDate;
    private List<Implementation> implementations;

    public MalariaProgram(String username,
                          Date reportedDate,
                          Date periodStartDate,
                          Date periodEndDate,
                          List<Implementation> implementations) {
        this.username = username;
        this.reportedDate = reportedDate;
        this.periodStartDate = periodStartDate;
        this.periodEndDate = periodEndDate;
        this.implementations = implementations;
    }
}
