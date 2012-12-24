package org.openlmis.core.builder;

import com.natpryce.makeiteasy.Instantiator;
import com.natpryce.makeiteasy.Property;
import com.natpryce.makeiteasy.PropertyLookup;
import org.openlmis.core.domain.SupervisoryNode;

import java.util.Date;

import static com.natpryce.makeiteasy.Property.newProperty;

public class SupervisoryNodeBuilder {
    public static final Property<SupervisoryNode, String> code = newProperty();
    public static final Property<SupervisoryNode, String> name = newProperty();
    public static final Property<SupervisoryNode, Date> modifiedDate = newProperty();

    public static final String SUPERVISORY_NODE_CODE = "N1";
    public static final String SUPERVISORY_NODE_NAME = "Approval Point 1";
    public static final Date SUPERVISORY_NODE_DATE = new Date(0);

    public static final Instantiator<SupervisoryNode> defaultSupervisoryNode = new Instantiator<SupervisoryNode>() {
        @Override
        public SupervisoryNode instantiate(PropertyLookup<SupervisoryNode> lookup) {
            SupervisoryNode supervisoryNode = new SupervisoryNode();
            supervisoryNode.setCode(lookup.valueOf(code, SUPERVISORY_NODE_CODE));
            supervisoryNode.setName(lookup.valueOf(name, SUPERVISORY_NODE_NAME));
            supervisoryNode.setModifiedBy("user");
            supervisoryNode.setModifiedDate(lookup.valueOf(modifiedDate, SUPERVISORY_NODE_DATE));
            return supervisoryNode;
        }
    };
}
