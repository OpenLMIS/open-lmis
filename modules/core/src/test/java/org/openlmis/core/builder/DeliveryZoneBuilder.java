/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */
package org.openlmis.core.builder;

import com.natpryce.makeiteasy.Instantiator;
import com.natpryce.makeiteasy.Property;
import com.natpryce.makeiteasy.PropertyLookup;
import org.openlmis.core.domain.DeliveryZone;

import static com.natpryce.makeiteasy.Property.newProperty;

public class DeliveryZoneBuilder {

  private static Property<DeliveryZone, String> code = newProperty();
  private static Property<DeliveryZone, String> name = newProperty();
  private static Property<DeliveryZone, Long> modifiedBy = newProperty();
  private static Property<DeliveryZone,Long> createdBy = newProperty();

  public static final String DEFAULT_CODE = "defaultCode";
  public static final String DEFAULT_NAME = "default name";
  public static final long MODIFIED_BY = 1l;
  public static final long CREATED_BY = 1l;

  public static final Instantiator<DeliveryZone> defaultDeliveryZone = new Instantiator<DeliveryZone>() {

    @Override
    public DeliveryZone instantiate(PropertyLookup<DeliveryZone> lookup) {
      DeliveryZone deliveryZone = new DeliveryZone();
      deliveryZone.setId(1l);
      deliveryZone.setCode(lookup.valueOf(code, DEFAULT_CODE));
      deliveryZone.setName(lookup.valueOf(name, DEFAULT_NAME));
      deliveryZone.setDescription("description");
      deliveryZone.setCreatedBy(lookup.valueOf(createdBy, CREATED_BY));
      deliveryZone.setModifiedBy(lookup.valueOf(modifiedBy, MODIFIED_BY));

      return deliveryZone;
    }
  };
}
