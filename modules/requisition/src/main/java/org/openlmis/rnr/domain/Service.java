package org.openlmis.rnr.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.openlmis.core.domain.BaseModel;

import static com.fasterxml.jackson.databind.annotation.JsonSerialize.Inclusion.NON_NULL;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = NON_NULL)
@EqualsAndHashCode(callSuper = false)
public class Service extends BaseModel {

    private String code;

    private String name;

    private String programId;

    private boolean active;

    public Service(String code, String name, String programId, boolean active) {
        this.code = code;
        this.name = name;
        this.programId = programId;
        this.active = active;
    }
}
