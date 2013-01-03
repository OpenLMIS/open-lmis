package org.openlmis.core.domain;

import lombok.Data;

import java.util.Date;

@Data
public class ProcessingPeriod {

  private Integer id;
  private Integer scheduleId;

  private String name;
  private String description;
  private Integer modifiedBy;
  private Date startDate;
  private Date endDate;
  private Date modifiedDate;

}
