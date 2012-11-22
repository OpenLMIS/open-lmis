package org.openlmis.core.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class FacilityApprovedProduct {

    private int id;

    private String facilityTypeCode;

    private String productCode;

    private String modifiedBy;

    private Date modifiedDate;

}
