package org.openlmis.web.rest.model;

import lombok.Data;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Fridge {

    @JsonProperty("FacilityID")
    private String facilityID;

    @JsonProperty("FridgeID")
    private String fridgeID;

    @JsonProperty("HighAlarmCount")
    private Long highAlarmCount;

    @JsonProperty("LowAlarmCount")
    private Long lowAlarmCount;

    @JsonProperty("MinutesHigh")
    private Long minutesHigh;

    @JsonProperty("MinutesInRange")
    private Long minutesInRange;

    @JsonProperty("MinutesLow")
    private Long minutesLow;

    @JsonProperty("MinutesNoData")
    private Long minutesNoData;

    @JsonProperty("Status")
    private Long status;

    @JsonProperty("URL")
    private String url;

    public void updateURL(String user, String pwd) {
        this.url = url.replace("://", "://" + user + ":" + pwd + "@");
    }
}
