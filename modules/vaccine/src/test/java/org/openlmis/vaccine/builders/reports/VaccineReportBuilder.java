package org.openlmis.vaccine.builders.reports;

import com.natpryce.makeiteasy.Instantiator;
import com.natpryce.makeiteasy.PropertyLookup;
import org.openlmis.vaccine.domain.Status;
import org.openlmis.vaccine.domain.reports.DiseaseLineItem;
import org.openlmis.vaccine.domain.reports.VaccineReport;
import org.openlmis.vaccine.dto.ReportStatusDTO;

public class VaccineReportBuilder {

  public static final Instantiator<VaccineReport> defaultVaccineReport = new Instantiator<VaccineReport>() {

    @Override
    public VaccineReport instantiate(PropertyLookup<VaccineReport> lookup) {
      VaccineReport item = new VaccineReport();
      item.setProgramId(1L);
      item.setFacilityId(1L);
      item.setPeriodId(1L);
      item.setSupervisoryNodeId(1L);
      item.setStatus("DRAFT");
      item.setTrackOutreachCoverage(true);
      return item;
    }
  };
}
