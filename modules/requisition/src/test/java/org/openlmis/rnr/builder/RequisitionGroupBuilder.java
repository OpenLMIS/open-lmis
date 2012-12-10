package org.openlmis.rnr.builder;

import com.natpryce.makeiteasy.Instantiator;
import com.natpryce.makeiteasy.Property;
import com.natpryce.makeiteasy.PropertyLookup;
import org.openlmis.core.builder.FacilityBuilder;
import org.openlmis.rnr.domain.RequisitionGroup;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static com.natpryce.makeiteasy.Property.newProperty;
import static org.openlmis.core.builder.FacilityBuilder.defaultFacility;

public class RequisitionGroupBuilder {
    public static final Property<RequisitionGroup, String> code = newProperty();
    public static final Property<RequisitionGroup, String> headFacilityCode = newProperty();
    public static final Property<RequisitionGroup, String> name = newProperty();
    public static final Property<RequisitionGroup, String> parent = newProperty();

    public static final String REQUISITION_GROUP_CODE = "RG1";
    public static final String REQUISITION_GROUP_NAME = "RG NAME";
    public static final String PARENT_CODE = "RG2";
    public static final String HEAD_FACILITY_CODE = "HF";

    public static final Instantiator<RequisitionGroup> defaultRequisitionGroup = new Instantiator<RequisitionGroup>() {
        @Override
        public RequisitionGroup instantiate(PropertyLookup<RequisitionGroup> lookup) {
            RequisitionGroup RequisitionGroup = new RequisitionGroup();
            RequisitionGroup.setCode(lookup.valueOf(code, REQUISITION_GROUP_CODE));
            RequisitionGroup.setName(lookup.valueOf(name, REQUISITION_GROUP_NAME));
            RequisitionGroup.setDescription("Requisition group Desc");
            RequisitionGroup.setLevelId("L1");
            RequisitionGroup.setModifiedBy("user");
            RequisitionGroup.setHeadFacility(make(a(defaultFacility,
                    with(FacilityBuilder.code,
                        lookup.valueOf(headFacilityCode, HEAD_FACILITY_CODE)))));
            RequisitionGroup.setActive(true);
            return RequisitionGroup;
        }
    };
}
