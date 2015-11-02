package org.openlmis.core.dto;

import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.openlmis.core.domain.BaseModel;

import static com.fasterxml.jackson.databind.annotation.JsonSerialize.Inclusion.NON_EMPTY;

@Getter
@Setter
@NoArgsConstructor
@JsonSerialize(include = NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class GeographicZoneGeometry extends BaseModel {

  private long zoneId;

  private long geoJsonId;

  private String geometry;

  @Override
  public boolean equals(Object o){
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;

    GeographicZoneGeometry that = (GeographicZoneGeometry) o;
    return id.equals(that.id);
  }

  @Override
  public int hashCode(){
    return id.hashCode();
  }

}
