package org.openlmis.rnr.builder;

import com.natpryce.makeiteasy.Instantiator;
import com.natpryce.makeiteasy.Property;
import com.natpryce.makeiteasy.PropertyLookup;
import org.openlmis.rnr.domain.SupervisoryNode;

import static com.natpryce.makeiteasy.Property.newProperty;

public class SupervisoryNodeBuilder {
    public static final Property<SupervisoryNode, String> code = newProperty();
    public static final Property<SupervisoryNode, String> name = newProperty();
    public static final Property<SupervisoryNode, Boolean> approvalPoint = newProperty();

    public static final String SUPERVISORY_NODE_CODE = "N1";
    public static final String SUPERVISORY_NODE_NAME = "Approval Point 1";
    public static final boolean SUPERVISORY_NODE_APPROVAL_POINT = true;

    public static final Instantiator<SupervisoryNode> defaultSupervisoryNode = new Instantiator<SupervisoryNode>() {
        @Override
        public SupervisoryNode instantiate(PropertyLookup<SupervisoryNode> lookup) {
            SupervisoryNode supervisoryNode = new SupervisoryNode();
            supervisoryNode.setCode(lookup.valueOf(code, SUPERVISORY_NODE_CODE));
            supervisoryNode.setName(lookup.valueOf(name, SUPERVISORY_NODE_NAME));
            supervisoryNode.setApprovalPoint(lookup.valueOf(approvalPoint, SUPERVISORY_NODE_APPROVAL_POINT));
            supervisoryNode.setModifiedBy("user");
            return supervisoryNode;
        }
    };
}
