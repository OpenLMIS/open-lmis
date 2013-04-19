package org.openlmis.core.domain;

import lombok.Getter;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.openlmis.core.serializer.RightDeSerializer;
import org.openlmis.core.serializer.RightSerializer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

@JsonSerialize(using = RightSerializer.class)
@JsonDeserialize(using = RightDeSerializer.class)
public enum Right {

  CONFIGURE_RNR("Admin - Configure Requisition Templates", TRUE, "Permission to create and edit r&r template for any program"),
  MANAGE_FACILITY("Admin - Manage Facilities", TRUE, "Permission to manage facility(crud)"),
  MANAGE_ROLE("Admin - Manage Roles", TRUE, "Permission to create and edit roles in the system"),
  MANAGE_SCHEDULE("Admin - Manage Schedules", TRUE, "Permission to create and edit schedules in the system"),
  MANAGE_USERS("Admin - Manage Users", TRUE, "Permission to manage users(crud)"),
  UPLOADS("Admin - Uploads", TRUE, "Permission to upload"),
  CREATE_REQUISITION("Requisition - Create", FALSE, "Permission to create, edit, submit and recall requisitions"),
  AUTHORIZE_REQUISITION("Requisition - Authorize", FALSE, "Permission to edit, authorize and recall requisitions"),
  APPROVE_REQUISITION("Requisition - Approve", FALSE, "Permission to approve requisitions"),
  CONVERT_TO_ORDER("Requisition - Convert to Order", TRUE, "Permission to convert requisitions to order"),
  VIEW_ORDER("Requisition - View Orders", TRUE, "Permission to view orders"),
  VIEW_REQUISITION("Requisition - View", FALSE, "Permission to view requisitions");
  VIEW_REQUISITION("Requisition - View", "requisition", "Permission to view requisitions"),
  VIEW_FACILITY_REPORT("Report - Facility Listing (V1)", "report", "Permission to view Facility List Report"),
  VIEW_MAILING_LABEL_REPORT("Report - Facility Listing (V2)", "report", "Permission to view Mailing labels for Facilities"),
  VIEW_SUMMARY_REPORT("Report - Summary Report", "report", "Permission to view Sumamry Report"),
  VIEW_CONSUMPTION_REPORT("Report - Consumption Report", "report", "Permission to view Consumption Report"),
  VIEW_REPORTING_RATE_REPORT("Report - Reporting Rate Report", "report", "Permission to view Reporting Rate Report");

    @Getter
  private final String rightName;

  @Getter
  private Boolean adminRight;

  @Getter
  private final String description;

  private Right(String rightName, Boolean adminRight, String description) {
    this.rightName = rightName;
    this.adminRight = adminRight;
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

  public static class RightComparator implements Comparator<Right> {
    @Override
    public int compare(Right right1, Right right2) {
      if(right1 == right2) return 0;
      if(right1 == null ){
        return 1;
      }
      if(right2 == null){
        return -1;
      }
      return right1.getRightName().compareTo(right2.getRightName());
    }
  }
}
