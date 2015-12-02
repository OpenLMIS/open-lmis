package org.openlmis.web.rest.model;


import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AlarmSettings {

    @JsonProperty("high_delay")
    private Double highDelay;

    @JsonProperty("high_threshold")
    private Double highThreshold;

    @JsonProperty("low_delay")
    private Double lowDelay;

    @JsonProperty("low_threshold")
    private Double lowThreshold;

    @JsonProperty("no_data_delay")
    private Double noDataDelay;
}
