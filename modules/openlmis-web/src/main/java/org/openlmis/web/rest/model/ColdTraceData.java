package org.openlmis.web.rest.model;

import lombok.Data;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ColdTraceData {

    private List<Fridge> fridges;
    private Params params;
}
