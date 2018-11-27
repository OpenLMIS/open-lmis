package org.openlmis.restapi.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.fasterxml.jackson.databind.annotation.JsonSerialize.Inclusion.NON_NULL;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = NON_NULL)
public class ProgramDTO {

    private Long id;
    private String name;
    private String code;
    private String description;
    private Boolean active;

    public ProgramDTO(Long id, String code, String name) {
        this.id = id;
        this.name = name;
        this.code = code;
    }

}