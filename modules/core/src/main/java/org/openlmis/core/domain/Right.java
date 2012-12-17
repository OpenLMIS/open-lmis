package org.openlmis.core.domain;

public enum Right {

  VIEW_REQUISITION("view requisition", "Permission to view requisitions"),
  CREATE_REQUISITION("create requisition", "Permission to create, edit, submit and recall requisitions"),
  APPROVE_REQUISITION("approve requisition", "Permission to approve and reject requisitions"),
  UPLOADS("upload", "Permission to upload"),
  MANAGE_FACILITY("manage facility", "Permission to manage facility(crud)"),
  CONFIGURE_RNR("configure rnr", "Permission to create and edit r&r template for any program");

  private final String rightName;
  private final String description;

  private Right(String rightName, String description) {
    this.rightName = rightName;
    this.description = description;
  }

  public String getRightName() {
    return rightName;
  }

}
