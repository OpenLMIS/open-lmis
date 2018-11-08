package org.openlmis.report.generator.impl;

import org.openlmis.report.generator.AbstractDrugReportGenerator;
import org.springframework.stereotype.Component;

@Component(value = "tracerDrug")
public class TracerDrugReportGenerator extends AbstractDrugReportGenerator {

    private final static String WEEKLY_TRACER_SOH_CUBE = "vw_weekly_tracer_soh";

    @Override
    protected String getFactUri() {
        return super.getBaseFactUri(WEEKLY_TRACER_SOH_CUBE);
    }
}