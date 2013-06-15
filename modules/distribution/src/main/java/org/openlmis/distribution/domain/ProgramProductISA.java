/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.distribution.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.openlmis.core.domain.BaseModel;

@EqualsAndHashCode(callSuper = false)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProgramProductISA extends BaseModel {

  Long programProductId;
  Float whoRatio;
  Integer dosesPerYear;
  Float wastageRate;
  Float bufferPercentage;
  Integer minimumValue;
  Integer adjustmentValue;

  Integer calculatedIsa;

  public ProgramProductISA(Float whoRatio, Integer dosesPerYear, Float wastageRate, Float bufferPercentage, Integer minimumValue, Integer adjustmentValue) {
    this.whoRatio = whoRatio;
    this.dosesPerYear = dosesPerYear;
    this.wastageRate = wastageRate;
    this.bufferPercentage = bufferPercentage;
    this.minimumValue = minimumValue;
    this.adjustmentValue = adjustmentValue;
  }
}
