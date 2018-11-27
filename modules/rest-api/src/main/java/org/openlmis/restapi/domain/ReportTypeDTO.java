package org.openlmis.restapi.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

import static com.fasterxml.jackson.databind.annotation.JsonSerialize.Inclusion.NON_NULL;


@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = NON_NULL)
public class ReportTypeDTO {

    private Long id;

    private String code;

    private Long programId;

    private String name;

    private String description;

    private boolean active;

    private Date startTime;

    private ProgramDTO program;

}
