package org.openlmis.web.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.openlmis.core.domain.DeliveryZone;
import org.openlmis.core.domain.GeographicZone;
import org.openlmis.core.domain.ProcessingPeriod;
import org.openlmis.core.domain.Program;

import static org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion.NON_EMPTY;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = NON_EMPTY)
public class ReviewDataFilter {

    private Program program;
    private GeographicZone province;
    private DeliveryZone deliveryZone;
    private ProcessingPeriod period;
    private ReviewDataColumnOrder order;

    public boolean isProvinceSelected() {
        return null != province;
    }

    public boolean isDeliveryZoneSelected() {
        return null != deliveryZone;
    }

    public boolean isPeriodSelected() {
        return null != period;
    }
}
