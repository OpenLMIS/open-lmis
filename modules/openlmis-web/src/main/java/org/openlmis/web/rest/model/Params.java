package org.openlmis.web.rest.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Params {

    @JsonProperty("alarm_settings")
    AlarmSettings alarmSettings;

    String created;

    @JsonProperty("end_date")
    String endDate;

    @JsonProperty("start_date")
    String startDate;
}
