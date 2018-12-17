package org.openlmis.restapi.domain;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.core.domain.Regimen;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProgramWithRegimens extends ProgramDTO{

    private String parentCode;
    private Boolean isSupportEmergency;
    private List<Regimen> regimens;

}
