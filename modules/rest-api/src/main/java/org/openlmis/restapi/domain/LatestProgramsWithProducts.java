package org.openlmis.restapi.domain;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@JsonSerialize
public class LatestProgramsWithProducts {

    private List<ProgramWithProducts> programWithProductsList;
    private Date latestUpdatedTime;
}
