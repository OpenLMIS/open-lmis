package org.openlmis.distribution.domain;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.openlmis.core.domain.BaseModel;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class RefrigeratorReading extends BaseModel {

  Float temperature;
  RadioOptions functioningCorrectly;
  Integer lowAlarmEvents;
  Integer highAlarmEvents;
  RadioOptions problemSinceLastTime;
  List<String> problems;
  String notes;
  String refrigeratorSerialNumber;
  Long facilityId;
  Long distributionId;
}

enum RadioOptions {
  Y,
  N,
  D
}
