package org.openlmis.restapi.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
public class LatestProgramsWithProducts {

    private List<ProgramWithProducts> programWithProductsList;
    private Date latestUpdatedTime;
}
