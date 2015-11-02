package org.openlmis.vaccine.domain.inventory;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.openlmis.core.domain.BaseModel;
import org.openlmis.core.domain.Product;
import org.openlmis.core.serializer.DateDeserializer;

import java.util.Date;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@JsonIgnoreProperties(ignoreUnknown = true)
public class VaccineDistribution extends BaseModel {

    Long toFacilityId;

    Long fromFacilityId;

    String voucherNumber;

    Long periodId;

    Long orderId;

    String status;

    String distributionType;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @JsonDeserialize(using = DateDeserializer.class)
    Date distributionDate;

    List<VaccineDistributionLineItem> lineItems;

}