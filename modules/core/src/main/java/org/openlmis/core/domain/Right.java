package org.openlmis.core.domain;

import lombok.Getter;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.openlmis.core.serializer.RightDeSerializer;
import org.openlmis.core.serializer.RightSerializer;

import java.util.ArrayList;
import java.util.List;

@JsonSerialize(using = RightSerializer.class)
@JsonDeserialize(using = RightDeSerializer.class)
public enum Right {

  CONFIGURE_RNR("Admin - Configure Requisition Templates", "Permission to create and edit r&r template for any program"),
  MANAGE_FACILITY("Admin - Manage Facilities", "Permission to manage facility(crud)"),
  MANAGE_ROLE("Admin - Manage Roles", "Permission to create and edit roles in the system"),
  MANAGE_SCHEDULE("Admin - Manage Schedules", "Permission to create and edit schedules in the system"),
  UPLOADS("Admin - Uploads", "Permission to upload"),
  CREATE_REQUISITION("Requisition - Create", "Permission to create, edit, submit and recall requisitions"),
  AUTHORIZE_REQUISITION("Requisition - Authorize", "Permission to edit, authorize and recall requisitions"),
  MANAGE_USERS("Admin - Manage Roles", "Permission to manage users(crud)"),
  APPROVE_REQUISITION("Requisition - Approve", "Permission to approve requisitions"),
  CONVERT_TO_ORDER("Requisition - Convert to Order", "Permission to convert requisitions to order");

  @Getter
  private final String rightName;
  @Getter
  private final String description;

  private Right(String rightName, String description) {
    this.rightName = rightName;
    this.description = description;
  }

  public static String getCommaSeparatedRightNames(Right... rights) {
    List<String> rightNames = new ArrayList<>();
    for (Right right: rights) {
      rightNames.add(right.name());
      }
      return rightNames.toString().replace("[", "{").replace("]", "}");
  }
}
