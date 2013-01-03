package org.openlmis.core.builder;

import com.natpryce.makeiteasy.Instantiator;
import com.natpryce.makeiteasy.Property;
import com.natpryce.makeiteasy.PropertyLookup;
import org.openlmis.core.domain.ProcessingPeriod;

import java.util.Date;

import static com.natpryce.makeiteasy.Property.newProperty;

public class ProcessingPeriodBuilder {

  public static final Property<ProcessingPeriod, String> name = newProperty();
  public static final Property<ProcessingPeriod, String> description = newProperty();
  public static final Property<ProcessingPeriod, Integer> scheduleId = newProperty();
  public static final Property<ProcessingPeriod, Date> startDate = newProperty();
  public static final Property<ProcessingPeriod, Date> endDate = newProperty();
  public static final Property<ProcessingPeriod, Integer> modifiedBy = newProperty();

  public static final String PERIOD_NAME = "Month1";
  public static final String PERIOD_DESC = "first month";
  public static final Integer MODIFIED_BY = 1;
  public static final Date START_DATE = new Date();
  public static final Date END_DATE = new Date(START_DATE.getTime() + 24 * 60 * 60 * 1000 * 30);
  private static final Integer SCHEDULE_ID = 1;

  public static final Instantiator<ProcessingPeriod> defaultProcessingPeriod = new Instantiator<ProcessingPeriod>() {

    @Override
    public ProcessingPeriod instantiate(PropertyLookup<ProcessingPeriod> lookup) {
      ProcessingPeriod period = new ProcessingPeriod();
      period.setName(lookup.valueOf(name, PERIOD_NAME));
      period.setDescription(lookup.valueOf(description, PERIOD_DESC));
      period.setStartDate(lookup.valueOf(startDate, START_DATE));
      period.setEndDate(lookup.valueOf(endDate, END_DATE));
      period.setModifiedBy(lookup.valueOf(modifiedBy, MODIFIED_BY));
      period.setScheduleId(lookup.valueOf(scheduleId, SCHEDULE_ID));
      return period;
    }
  };
}
