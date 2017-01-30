package org.openlmis.web.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.openlmis.core.domain.DeliveryZone;
import org.openlmis.core.domain.ProcessingPeriod;
import org.openlmis.core.serializer.DateSerializer;
import org.openlmis.core.serializer.DateTimeSerializer;

import java.util.Date;

import static org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion.NON_EMPTY;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = NON_EMPTY)
public class SynchronizedDistribution {

    private Long distributionId;

    private String province;
    private DeliveryZone deliveryZone;
    private ProcessingPeriod period;

    @JsonSerialize(using = DateSerializer.class)
    private Date initiated;

    @JsonProperty("synchronized")
    @JsonSerialize(using = DateSerializer.class)
    private Date sync;

    @JsonSerialize(using = DateSerializer.class)
    private Date lastViewed;

    @JsonSerialize(using = DateTimeSerializer.class)
    private Date lastEdited;

    private String editedBy;

    private Boolean edit;
    private Boolean view;

}
