package org.openlmis.core.domain;

import lombok.Getter;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.openlmis.core.serializer.RightDeSerializer;
import org.openlmis.core.serializer.RightSerializer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@JsonSerialize(using = RightSerializer.class)
@JsonDeserialize(using = RightDeSerializer.class)
public enum Right {

  CONFIGURE_RNR("Admin - Configure Requisition Templates", "admin", "Permission to create and edit r&r template for any program"),
  MANAGE_FACILITY("Admin - Manage Facilities", "admin", "Permission to manage facility(crud)"),
  MANAGE_ROLE("Admin - Manage Roles", "admin", "Permission to create and edit roles in the system"),
  MANAGE_SCHEDULE("Admin - Manage Schedules", "admin", "Permission to create and edit schedules in the system"),
  MANAGE_USERS("Admin - Manage Users", "admin", "Permission to manage users(crud)"),
  UPLOADS("Admin - Uploads", "admin", "Permission to upload"),
  CREATE_REQUISITION("Requisition - Create", "requisition", "Permission to create, edit, submit and recall requisitions"),
  AUTHORIZE_REQUISITION("Requisition - Authorize", "requisition", "Permission to edit, authorize and recall requisitions"),
  APPROVE_REQUISITION("Requisition - Approve", "requisition", "Permission to approve requisitions"),
  CONVERT_TO_ORDER("Requisition - Convert to Order", "requisition", "Permission to convert requisitions to order"),
  VIEW_REQUISITION("Requisition - View", "requisition", "Permission to view requisitions");

  @Getter
  private final String rightName;

  @Getter
  private String category;

  @Getter
  private final String description;

  private Right(String rightName, String category, String description) {
    this.rightName = rightName;
    this.category = category;
    this.description = description;
  }

  public static String commaSeparateRightNames(Right... rights) {
    List<String> rightNames = new ArrayList<>();
    for (Right right : rights) {
      rightNames.add(right.name());
    }
    return rightNames.toString().replace("[", "{").replace("]", "}");
  }

  public List<Right> getDependentRights() {
    if (this == CREATE_REQUISITION || this == AUTHORIZE_REQUISITION || this == APPROVE_REQUISITION || this == CONVERT_TO_ORDER) {
      return Arrays.asList(VIEW_REQUISITION);
    }
    return new ArrayList<>();
  }
}
