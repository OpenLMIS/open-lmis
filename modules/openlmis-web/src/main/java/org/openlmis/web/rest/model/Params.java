package org.openlmis.web.rest.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Params {

    @JsonProperty("alarm_settings")
    private AlarmSettings alarmSettings;

    private String created;

    @JsonProperty("end_date")
    private String endDate;

    @JsonProperty("start_date")
    private String startDate;
}
