package org.openlmis.core.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class FacilityGeoTreeDto {

  private long id;
  private String name;
  private long facility;
  private List<FacilityGeoTreeDto> children;
}
