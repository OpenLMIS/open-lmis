package org.openlmis.web.rest.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Fridge {

    @JsonProperty("FacillityID")
    String facilityID;

    @JsonProperty("FridgeID")
    String fridgeID;

    @JsonProperty("HighAlarmCount")
    Long highAlarmCount;

    @JsonProperty("LowAlarmCount")
    Long lowAlarmCount;

    @JsonProperty("MinutesHigh")
    Long minutesHigh;

    @JsonProperty("MinutesInRange")
    Long minutesInRange;

    @JsonProperty("MinutesLow")
    Long minutesLow;

    @JsonProperty("MinutesNoData")
    Long minutesNoData;

    @JsonProperty("Status")
    Long status;

    @JsonProperty("URL")
    String url;
}
