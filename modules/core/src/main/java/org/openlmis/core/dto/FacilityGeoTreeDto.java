package org.openlmis.core.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
public class FacilityGeoTreeDto {

    private long id;
    private String name;
    private long facility;
    private List<FacilityGeoTreeDto> children;
}
