package org.openlmis.rnr.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Order {

  private Integer id;
  private Date orderedDate;
  private Integer orderedBy;
  private List<Rnr> rnrList;
}
