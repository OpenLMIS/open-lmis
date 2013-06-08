/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
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
