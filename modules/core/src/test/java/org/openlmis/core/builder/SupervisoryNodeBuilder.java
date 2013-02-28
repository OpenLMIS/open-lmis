package org.openlmis.core.builder;

import com.natpryce.makeiteasy.Instantiator;
import com.natpryce.makeiteasy.Property;
import com.natpryce.makeiteasy.PropertyLookup;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.SupervisoryNode;

import java.util.Date;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static com.natpryce.makeiteasy.Property.newProperty;
import static org.openlmis.core.builder.FacilityBuilder.defaultFacility;

public class SupervisoryNodeBuilder {
    public static final Property<SupervisoryNode, String> code = newProperty();
    public static final Property<SupervisoryNode, String> name = newProperty();
    public static final Property<SupervisoryNode, Date> modifiedDate = newProperty();
    public static final Property<SupervisoryNode, Facility> facility = newProperty();

    public static final String SUPERVISORY_NODE_CODE = "N1";
    public static final String SUPERVISORY_NODE_NAME = "Approval Point 1";
    public static final Date SUPERVISORY_NODE_DATE = new Date(0);

    public static final Instantiator<SupervisoryNode> defaultSupervisoryNode = new Instantiator<SupervisoryNode>() {
        @Override
        public SupervisoryNode instantiate(PropertyLookup<SupervisoryNode> lookup) {
            SupervisoryNode supervisoryNode = new SupervisoryNode();
            supervisoryNode.setCode(lookup.valueOf(code, SUPERVISORY_NODE_CODE));
            supervisoryNode.setName(lookup.valueOf(name, SUPERVISORY_NODE_NAME));
            supervisoryNode.setFacility(lookup.valueOf(facility, make(a(defaultFacility))));
            supervisoryNode.setModifiedBy(1);
            supervisoryNode.setModifiedDate(lookup.valueOf(modifiedDate, SUPERVISORY_NODE_DATE));
            return supervisoryNode;
        }
    };
}
