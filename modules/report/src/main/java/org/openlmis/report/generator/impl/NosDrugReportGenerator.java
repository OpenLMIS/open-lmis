package org.openlmis.report.generator.impl;

import org.openlmis.report.generator.AbstractDrugReportGenerator;
import org.springframework.stereotype.Component;

@Component(value = "nosDrug")
public class NosDrugReportGenerator extends AbstractDrugReportGenerator {

    private final static String WEEKLY_NOS_SOH_CUBE = "vw_weekly_nos_soh";

    @Override
    protected String getFactUri() {
        return super.getBaseFactUri(WEEKLY_NOS_SOH_CUBE);
    }
}