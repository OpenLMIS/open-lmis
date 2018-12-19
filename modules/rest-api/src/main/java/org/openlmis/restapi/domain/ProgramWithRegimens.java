package org.openlmis.restapi.domain;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.core.domain.Regimen;
import org.openlmis.core.utils.DateUtil;
import org.openlmis.rnr.domain.PatientQuantificationLineItem;
import org.openlmis.rnr.domain.RegimenLineItem;
import org.openlmis.rnr.domain.Rnr;
import org.openlmis.rnr.domain.RnrLineItem;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProgramWithRegimens extends ProgramDTO{

    private String parentCode;
    private Boolean isSupportEmergency;
    private List<RegimenForRest> regimens;

    private String categoryName;

}
