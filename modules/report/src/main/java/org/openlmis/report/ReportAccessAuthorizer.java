/*
 * Copyright © 2013 John Snow, Inc. (JSI). All Rights Reserved.
 *
 * The U.S. Agency for International Development (USAID) funded this section of the application development under the terms of the USAID | DELIVER PROJECT contract no. GPO-I-00-06-00007-00.
 */

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
