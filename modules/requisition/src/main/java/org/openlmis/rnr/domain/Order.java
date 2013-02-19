package org.openlmis.rnr.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Order {

  private Integer id;
  private String orderedDate;
  private String orderedBy;
  private List<Rnr> rnrList;
}
