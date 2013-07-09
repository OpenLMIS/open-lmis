/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.builder;

import com.natpryce.makeiteasy.Instantiator;
import com.natpryce.makeiteasy.Property;
import com.natpryce.makeiteasy.PropertyLookup;
import org.openlmis.core.domain.DeliveryZone;
import org.openlmis.core.domain.ProcessingPeriod;
import org.openlmis.core.domain.Program;
import org.openlmis.distribution.domain.Distribution;
import org.openlmis.distribution.domain.DistributionStatus;

import static com.natpryce.makeiteasy.Property.newProperty;

public class DistributionBuilder {

  public static Property<Distribution, DeliveryZone> deliveryZone = newProperty();
  public static Property<Distribution, Program> program = newProperty();
  public static Property<Distribution, ProcessingPeriod> period = newProperty();
  public static Property<Distribution, DistributionStatus> status = newProperty();
  public static Property<Distribution, Long> modifiedBy = newProperty();
  public static Property<Distribution, Long> createdBy = newProperty();

  public static final DistributionStatus DEFAULT_STATUS = DistributionStatus.INITIATED;
  public static final long DEFAULT_MODIFIED_BY = 1l;
  public static final long DEFAULT_CREATED_BY = 1l;

  public static final Instantiator<Distribution> defaultDistribution = new Instantiator<Distribution>() {

    @Override
    public Distribution instantiate(PropertyLookup<Distribution> lookup) {
      Distribution distribution = new Distribution();
      distribution.setDeliveryZone(lookup.valueOf(deliveryZone, new DeliveryZone()));
      distribution.setProgram(lookup.valueOf(program, new Program()));
      distribution.setPeriod(lookup.valueOf(period, new ProcessingPeriod()));
      distribution.setStatus(lookup.valueOf(status, DEFAULT_STATUS));
      distribution.setCreatedBy(lookup.valueOf(createdBy, DEFAULT_CREATED_BY));
      distribution.setModifiedBy(lookup.valueOf(modifiedBy, DEFAULT_MODIFIED_BY));

      return distribution;
    }
  };
}
