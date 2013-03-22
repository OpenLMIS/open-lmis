package org.openlmis.report;

import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

/**
 */
@Component
@NoArgsConstructor
public class ReportAccessAuthorizer {

    public boolean hasReadAccess(String reportKey){
        return false;
    }
}
