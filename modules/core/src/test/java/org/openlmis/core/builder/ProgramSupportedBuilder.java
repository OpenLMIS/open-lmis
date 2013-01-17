package org.openlmis.core.builder;

import com.natpryce.makeiteasy.Instantiator;
import com.natpryce.makeiteasy.Property;
import com.natpryce.makeiteasy.PropertyLookup;
import org.openlmis.core.domain.ProgramSupported;

import java.util.Date;

import static com.natpryce.makeiteasy.Property.newProperty;
import static org.joda.time.DateTime.now;

public class ProgramSupportedBuilder {
  public static Property<ProgramSupported, String> supportedFacilityCode = newProperty();
  public static Property<ProgramSupported, String> supportedProgramCode = newProperty();
  public static Property<ProgramSupported, Integer> supportedFacilityId = newProperty();
  public static Property<ProgramSupported, Integer> supportedProgramId = newProperty();
  public static Property<ProgramSupported, Boolean> isActive = newProperty();
  public static Property<ProgramSupported, Date> startDate = newProperty();

  public static final String FACILITY_CODE = "F_CD";
  public static final String PROGRAM_CODE = "P_CD";
  public static final Integer FACILITY_ID = 101;
  public static final Integer PROGRAM_ID = 101;
  public static final Boolean IS_ACTIVE = true;
  public static final Date START_DATE = now().minusYears(5).toDate();

  public static final Instantiator<ProgramSupported> defaultProgramSupported = new Instantiator<ProgramSupported>() {
    @Override
    public ProgramSupported instantiate(PropertyLookup<ProgramSupported> lookup) {
      ProgramSupported programSupported = new ProgramSupported();
      programSupported.setFacilityCode(lookup.valueOf(supportedFacilityCode, FACILITY_CODE));
      programSupported.setProgramCode(lookup.valueOf(supportedProgramCode, PROGRAM_CODE));
      programSupported.setFacilityId(lookup.valueOf(supportedFacilityId, FACILITY_ID));
      programSupported.setProgramId(lookup.valueOf(supportedProgramId, PROGRAM_ID));
      programSupported.setStartDate(lookup.valueOf(startDate, START_DATE));
      programSupported.setActive(IS_ACTIVE);
      programSupported.setModifiedBy("user");
      programSupported.setModifiedDate(now().toDate());
      return programSupported;
    }
  };
}
