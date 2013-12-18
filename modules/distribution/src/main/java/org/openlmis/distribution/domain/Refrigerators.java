package org.openlmis.distribution.domain;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class Refrigerators {
  List<RefrigeratorReading> readings;
}
