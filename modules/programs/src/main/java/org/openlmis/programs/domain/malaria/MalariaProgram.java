package org.openlmis.programs.domain.malaria;

import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;
import org.openlmis.programs.domain.malaria.validators.annotations.ValidateMalariaPeriodDates;
import org.openlmis.programs.domain.malaria.validators.annotations.ValidateUsernameExists;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;

@Data
@ValidateMalariaPeriodDates
public class MalariaProgram {
    private int id;

    @NotEmpty
    @ValidateUsernameExists
    private String username;
    private Date reportedDate;
    private Date periodStartDate;
    private Date periodEndDate;

    @Valid
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
