package org.openlmis.restapi.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonDeserialize
public class RestAppInfoRequest {
    private String facilityCode;
    private String userName;
    private String version;

}
