package org.openlmis.web.rest.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.util.List;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ColdTradeData {

    List<Fridge> fridges;
    Params params;
}
