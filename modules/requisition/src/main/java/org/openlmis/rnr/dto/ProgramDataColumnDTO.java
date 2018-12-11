package org.openlmis.rnr.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ProgramDataColumnDTO {

    private String code;
    private String label;
    private String description;
    private ServiceLineItemDTO serviceLineItem;
}
