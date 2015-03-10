/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.core.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.joda.time.DateTime;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.serializer.DateDeserializer;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.apache.commons.lang.StringUtils.isBlank;
import static com.fasterxml.jackson.databind.annotation.JsonSerialize.Inclusion.NON_EMPTY;
import static org.joda.time.format.DateTimeFormat.forPattern;

/**
 * ProcessingPeriod represents the time period belonging to a particular schedule according to which requisition life cycle
 * will be followed.
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@JsonSerialize(include = NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProcessingPeriod extends BaseModel {

  private Long scheduleId;
  private String name;
  private String description;
  private Integer numberOfMonths;

  @JsonDeserialize(using = DateDeserializer.class)
  private Date startDate;

  @JsonDeserialize(using = DateDeserializer.class)
  private Date endDate;

  public ProcessingPeriod(Long id) {
    this.id = id;
  }

  public ProcessingPeriod(Long id, Date startDate, Date endDate, Integer numberOfMonths, String name) {
    this.id = id;
    this.startDate = startDate;
    this.endDate = endDate;
    this.numberOfMonths = numberOfMonths;
    this.name = name;
  }

  public void validate() {
    if (scheduleId == null) {
      throw new DataException("error.period.without.schedule");
    }
    if (startDate == null || startDate.toString().isEmpty()) {
      throw new DataException("error.period.without.start.date");
    }
    if (endDate == null || endDate.toString().isEmpty()) {
      throw new DataException("error.period.without.end.date");
    }
    if (isBlank(name)) {
      throw new DataException("error.period.without.name");
    }
    if (endDate.compareTo(startDate) <= 0) {
      throw new DataException("error.period.invalid.dates");
    }
  }

  public void includeEntireDuration() throws ParseException {
    SimpleDateFormat dateFormatWithoutTime = new SimpleDateFormat("dd/MM/yyyy");
    SimpleDateFormat dateFormatWithTime = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    startDate = dateFormatWithoutTime.parse(dateFormatWithoutTime.format(startDate) + " 00:00:00");
    endDate = dateFormatWithTime.parse(dateFormatWithoutTime.format(endDate) + " 23:59:59");
  }

  @SuppressWarnings("unused")
  public String getStringStartDate() throws ParseException {
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
    return simpleDateFormat.format(this.startDate);
  }

  @SuppressWarnings("unused")
  public String getStringEndDate() throws ParseException {
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
    return simpleDateFormat.format(this.endDate);
  }

  public String getStringYear(){
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy");
    return simpleDateFormat.format(this.startDate);
  }

  public String getNextStartDate() {
    return forPattern("yyyy-MM-dd").print(new DateTime(getEndDate()).plusDays(1));
  }
}