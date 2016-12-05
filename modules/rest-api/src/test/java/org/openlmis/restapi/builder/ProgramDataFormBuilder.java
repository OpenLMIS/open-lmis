package org.openlmis.restapi.builder;

import com.natpryce.makeiteasy.Instantiator;
import com.natpryce.makeiteasy.Property;
import com.natpryce.makeiteasy.PropertyLookup;
import org.openlmis.core.utils.DateUtil;
import org.openlmis.restapi.domain.ProgramDataFormDTO;
import org.openlmis.restapi.domain.ProgramDataFormItemDTO;

import java.util.ArrayList;
import java.util.Date;

import static com.natpryce.makeiteasy.Property.newProperty;

public class ProgramDataFormBuilder {

  public static final Property<ProgramDataFormDTO, Long> facilityId = newProperty();
  public static final Property<ProgramDataFormDTO, String> programCode = newProperty();
  public static final Property<ProgramDataFormDTO, Date> periodBegin = newProperty();
  public static final Property<ProgramDataFormDTO, Date> periodEnd = newProperty();
  public static final Property<ProgramDataFormDTO, Date> submittedTime = newProperty();

  public static final Instantiator<ProgramDataFormDTO> defaultProgramDataForm = new Instantiator<ProgramDataFormDTO>() {
    @Override
    public ProgramDataFormDTO instantiate(PropertyLookup<ProgramDataFormDTO> lookup) {
      ProgramDataFormDTO programDataFormDTO = new ProgramDataFormDTO();
      programDataFormDTO.setFacilityId(lookup.valueOf(facilityId, 1L));
      programDataFormDTO.setPeriodBegin(lookup.valueOf(periodBegin, DateUtil.parseDate("2016-10-21", DateUtil.FORMAT_DATE)));
      programDataFormDTO.setPeriodEnd(lookup.valueOf(periodEnd, DateUtil.parseDate("2016-11-20", DateUtil.FORMAT_DATE)));
      programDataFormDTO.setProgramCode(lookup.valueOf(programCode, "some program"));
      programDataFormDTO.setSubmittedTime(lookup.valueOf(submittedTime, DateUtil.parseDate("2016-12-01 11:11:11")));
      programDataFormDTO.setProgramDataFormItems(new ArrayList<ProgramDataFormItemDTO>());
      return programDataFormDTO;
    }
  };
}
