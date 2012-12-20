package org.openlmis.core.domain;

import lombok.Getter;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.openlmis.core.serializer.RightSerializer;
import org.openlmis.core.serializer.RightsDeSerializer;

@JsonSerialize(using = RightSerializer.class)
@JsonDeserialize(using = RightsDeSerializer.class)
public enum Right {

  VIEW_REQUISITION("view requisition", "Permission to view requisitions"),
  CREATE_REQUISITION("create requisition", "Permission to create, edit, submit and recall requisitions"),
  APPROVE_REQUISITION("approve requisition", "Permission to approve and reject requisitions"),
  UPLOADS("upload", "Permission to upload"),
  MANAGE_FACILITY("manage facility", "Permission to manage facility(crud)"),
  CONFIGURE_RNR("configure rnr", "Permission to create and edit r&r template for any program");

    @Getter
    private final String rightName;
    @Getter
    private final String description;

  private Right(String rightName, String description) {
    this.rightName = rightName;
    this.description = description;
  }
}
