package org.openlmis.web.model;


import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.core.dto.GeographicZoneGeometry;

import java.util.List;

@NoArgsConstructor
@Data
public class GeoZoneInfo {
  private List<GeographicZoneGeometry> features;
}
