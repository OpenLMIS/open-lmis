package org.openlmis.web.rest.model;


import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AlarmSettings {

    @JsonProperty("high_delay")
    float highDelay;

    @JsonProperty("high_threshold")
    float highThreshold;

    @JsonProperty("low_delay")
    float lowDelay;

    @JsonProperty("low_threshold")
    float lowThreshold;

    @JsonProperty("no_data_delay")
    float noDataDelay;
}
