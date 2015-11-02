package org.openlmis.equipment.domain;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.openlmis.core.domain.BaseModel;
import org.openlmis.equipment.dto.ContractDetail;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ServiceContract extends BaseModel{

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


  public String getStartDateString()  {
    return getFormattedDate(this.startDate);
  }

  public String getEndDateString()  {
    return getFormattedDate(this.endDate);
  }

  public String getContractDateString()  {
    return getFormattedDate(this.contractDate);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;

    if (o == null || getClass() != o.getClass()) return false;

    ServiceContract that = (ServiceContract) o;

    return new EqualsBuilder()
        .append(id, that.id)
        .isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37)
        .append(id)
        .toHashCode();
  }
}
