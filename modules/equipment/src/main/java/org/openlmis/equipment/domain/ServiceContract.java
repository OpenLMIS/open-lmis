package org.openlmis.equipment.domain;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.openlmis.core.domain.BaseModel;
import org.openlmis.equipment.dto.ContractDetail;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ServiceContract extends BaseModel {

  private Long vendorId;
  private String identifier;
  private Date startDate;
  private Date endDate;
  private String description;
  private String terms;
  private String coverage;
  private Date contractDate;

  private List<ContractDetail> facilities;
  private List<ContractDetail> serviceTypes;
  private List<ContractDetail> equipments;


  public String getStartDateString() {
    return getFormattedDate(this.startDate);
  }

  public String getEndDateString() {
    return getFormattedDate(this.endDate);
  }

  public String getContractDateString() {
    return getFormattedDate(this.contractDate);
  }

}
