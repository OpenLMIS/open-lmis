/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.distribution.builder;

import com.natpryce.makeiteasy.Instantiator;
import com.natpryce.makeiteasy.Property;
import com.natpryce.makeiteasy.PropertyLookup;
import org.openlmis.core.domain.DeliveryZone;
import org.openlmis.core.domain.ProcessingPeriod;
import org.openlmis.core.domain.Program;
import org.openlmis.distribution.domain.Distribution;
import org.openlmis.distribution.domain.DistributionStatus;

import java.util.Date;

import static com.natpryce.makeiteasy.Property.newProperty;
import static org.openlmis.distribution.domain.DistributionStatus.INITIATED;

public class DistributionBuilder {

  public static Property<Distribution, DeliveryZone> deliveryZone = newProperty();
  public static Property<Distribution, Program> program = newProperty();
  public static Property<Distribution, ProcessingPeriod> period = newProperty();
  public static Property<Distribution, Long> modifiedBy = newProperty();
  public static Property<Distribution, Long> createdBy = newProperty();
  public static Property<Distribution, Date> createdDate = newProperty();
  public static Property<Distribution, DistributionStatus> status = newProperty();

  public static final long DEFAULT_MODIFIED_BY = 1l;
  public static final long DEFAULT_CREATED_BY = 1l;
  public static final Date DEFAULT_CREATED_DATE = new Date();

  public static final Instantiator<Distribution> defaultDistribution = new Instantiator<Distribution>() {

    @Override
    public Distribution instantiate(PropertyLookup<Distribution> lookup) {
      return basicDistribution(lookup);
    }
  };


  public static final Instantiator<Distribution> initiatedDistribution = new Instantiator<Distribution>() {

    @Override
    public Distribution instantiate(PropertyLookup<Distribution> lookup) {
      Distribution distribution = basicDistribution(lookup);
      distribution.setStatus(INITIATED);
      return distribution;
    }
  };

  private static Distribution basicDistribution(PropertyLookup<Distribution> lookup) {
    Distribution distribution = new Distribution();
    distribution.setDeliveryZone(lookup.valueOf(deliveryZone, new DeliveryZone()));
    distribution.setProgram(lookup.valueOf(program, new Program()));
    distribution.setPeriod(lookup.valueOf(period, new ProcessingPeriod()));
    distribution.setStatus(lookup.valueOf(status, DistributionStatus.INITIATED));
    distribution.setCreatedBy(lookup.valueOf(createdBy, DEFAULT_CREATED_BY));
    distribution.setModifiedBy(lookup.valueOf(modifiedBy, DEFAULT_MODIFIED_BY));
    distribution.setCreatedDate(lookup.valueOf(createdDate, DEFAULT_CREATED_DATE));
    return distribution;
  }

}
