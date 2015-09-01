/*
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 *   Copyright (C) 2015  John Snow, Inc (JSI). This program was produced for the U.S. Agency for International Development (USAID). It was prepared under the USAID | DELIVER PROJECT, Task Order 4.
 *
 *   This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 *   You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.openlmis.core.builder;


import com.natpryce.makeiteasy.Instantiator;
import com.natpryce.makeiteasy.Property;
import com.natpryce.makeiteasy.PropertyLookup;
import org.openlmis.core.domain.DeliveryZone;
import org.openlmis.core.domain.ELMISInterface;
import org.openlmis.core.domain.ELMISInterfaceDataSet;

import static com.natpryce.makeiteasy.Property.newProperty;

public class ELMISInterfaceBuilder {

    public static final Property<ELMISInterface, Boolean> active = newProperty();
    public static final Property<ELMISInterface, String> name = newProperty();
    public static final Property<ELMISInterface, Long> modifiedBy = newProperty();
    public static final Property<ELMISInterface,Long> createdBy = newProperty();

    public static final Boolean ACTIVE = true;
    public static final String DEFAULT_NAME = "default name";
    public static final long MODIFIED_BY = 1l;
    public static final long CREATED_BY = 1l;

    public static final Instantiator<ELMISInterface> defaultELMISInterface = new Instantiator<ELMISInterface>() {

        @Override
        public ELMISInterface instantiate(PropertyLookup<ELMISInterface> lookup) {
            ELMISInterface elmisInterface = new ELMISInterface();
            elmisInterface.setId(1l);
            elmisInterface.setName(lookup.valueOf(name, DEFAULT_NAME));
            elmisInterface.setActive(lookup.valueOf(active, ACTIVE));
            elmisInterface.setCreatedBy(lookup.valueOf(createdBy, CREATED_BY));
            elmisInterface.setModifiedBy(lookup.valueOf(modifiedBy, MODIFIED_BY));

            return elmisInterface;
        }
    };
}
