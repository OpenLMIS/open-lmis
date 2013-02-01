package org.openlmis.core.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.core.exception.DataException;

import java.util.Date;

@Data
@NoArgsConstructor
public class ProcessingPeriod {

  private Integer id;
  private Integer scheduleId;

  private String name;
  private String description;
  private Integer modifiedBy;
  private Date startDate;
  private Date endDate;
  private Integer numberOfMonths;
  private Date modifiedDate;

  public ProcessingPeriod(Integer id) {
    this.id = id;
  }

  public ProcessingPeriod(Integer id, Date startDate, Date endDate, Integer numberOfMonths) {
    this.id = id;
    this.startDate = startDate;
    this.endDate = endDate;
    this.numberOfMonths = numberOfMonths;
  }

  public void validate() {
    if (scheduleId == null || scheduleId == 0)
      throw new DataException("Period can not be saved without its parent Schedule.");
    if (startDate == null || startDate.toString().isEmpty())
      throw new DataException("Period can not be saved without its Start Date.");
    if (endDate == null || endDate.toString().isEmpty())
      throw new DataException("Period can not be saved without its End Date.");
    if (name == null || name.isEmpty())
      throw new DataException("Period can not be saved without its Name.");
    if (endDate.compareTo(startDate)<=0) {
      throw new DataException("Period End Date can not be earlier than Start Date.");
    }
  }

  public ProcessingPeriod basicInformation() {
    return new ProcessingPeriod(id, startDate, endDate, numberOfMonths);
  }
}
