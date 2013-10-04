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
import org.openlmis.core.domain.RequisitionGroup;

import java.util.Date;

import static com.natpryce.makeiteasy.Property.newProperty;

public class RequisitionGroupBuilder {
    public static final Property<RequisitionGroup, String> code = newProperty();
    public static final Property<RequisitionGroup, String> name = newProperty();
    public static final Property<RequisitionGroup, Date> modifiedDate = newProperty();

    public static final String REQUISITION_GROUP_CODE = "RG1";
    public static final String REQUISITION_GROUP_NAME = "RG NAME";
    public static final Date REQUISITION_GROUP_DATE = new Date(0);

    public static final Instantiator<RequisitionGroup> defaultRequisitionGroup = new Instantiator<RequisitionGroup>() {
        @Override
        public RequisitionGroup instantiate(PropertyLookup<RequisitionGroup> lookup) {
            RequisitionGroup requisitionGroup = new RequisitionGroup();
            requisitionGroup.setCode(lookup.valueOf(code, REQUISITION_GROUP_CODE));
            requisitionGroup.setName(lookup.valueOf(name, REQUISITION_GROUP_NAME));
            requisitionGroup.setModifiedBy(1L);
            requisitionGroup.setModifiedDate(lookup.valueOf(modifiedDate, REQUISITION_GROUP_DATE));
            return requisitionGroup;
        }
    };
}
