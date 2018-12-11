package org.openlmis.rnr.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ServiceDTO {

    private String code;

    private String name;

    private String programId;

    private boolean active;

    private List<ProgramDataColumnDTO> programDataColumns = new ArrayList<>();
}
