package org.openlmis.core.builder;

import com.natpryce.makeiteasy.Instantiator;
import com.natpryce.makeiteasy.Property;
import com.natpryce.makeiteasy.PropertyLookup;
import org.openlmis.core.domain.ProcessingPeriod;

import java.util.Calendar;
import java.util.Date;

import static com.natpryce.makeiteasy.Property.newProperty;

public class ProcessingPeriodBuilder {

  public static final Property<ProcessingPeriod, String> name = newProperty();
  public static final Property<ProcessingPeriod, String> description = newProperty();
  public static final Property<ProcessingPeriod, Integer> scheduleId = newProperty();
  public static final Property<ProcessingPeriod, Date> startDate = newProperty();
  public static final Property<ProcessingPeriod, Date> endDate = newProperty();
  public static final Property<ProcessingPeriod, Integer> numberOfMonths = newProperty();
  public static final Property<ProcessingPeriod, Integer> modifiedBy = newProperty();
  public static final Property<ProcessingPeriod, Integer> id = newProperty();

  public static final String PERIOD_NAME = "Month1";
  public static final String PERIOD_DESC = "first month";
  public static final Integer MODIFIED_BY = 1;
  public static final Calendar START_DATE;
  public static final Calendar END_DATE;
  public static final Integer NUMBER_OF_MONTHS = 1;
  public static final Integer SCHEDULE_ID = 1;

  static{
    START_DATE = Calendar.getInstance();
    END_DATE = (Calendar) START_DATE.clone();
    END_DATE.add(Calendar.MONTH, 1);
  }

  public static final Instantiator<ProcessingPeriod> defaultProcessingPeriod = new Instantiator<ProcessingPeriod>() {


    @Override
    public ProcessingPeriod instantiate(PropertyLookup<ProcessingPeriod> lookup) {
      Integer nullInteger = null;
      ProcessingPeriod period = new ProcessingPeriod();
      period.setName(lookup.valueOf(name, PERIOD_NAME));
      period.setDescription(lookup.valueOf(description, PERIOD_DESC));
      period.setStartDate(lookup.valueOf(startDate, START_DATE.getTime()));
      period.setEndDate(lookup.valueOf(endDate, END_DATE.getTime()));
      period.setNumberOfMonths(lookup.valueOf(numberOfMonths, NUMBER_OF_MONTHS));
      period.setModifiedBy(lookup.valueOf(modifiedBy, MODIFIED_BY));
      period.setScheduleId(lookup.valueOf(scheduleId, SCHEDULE_ID));
      period.setId(lookup.valueOf(id, nullInteger));
      return period;
    }
  };
}
