package org.openlmis.distribution.domain;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.core.domain.BaseModel;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RefrigeratorReading extends BaseModel {

  Float temperature;
  RadioOptions functioningCorrectly;
  Integer lowAlarmEvents;
  Integer highAlarmEvents;
  RadioOptions problemSinceLastTime;
  String problemList;
  String notes;
  Long refrigeratorId;
  Long distributionId;
}

enum RadioOptions {
  Y,
  N,
  D
}
