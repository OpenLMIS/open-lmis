package org.openlmis.core.domain;

public enum Right {
  VIEW_REQUISITION("view requisition", "Permission to view requisitions"),
  CREATE_REQUISITION("create requisition", "Permission to create, edit, submit and recall requisitions"),
  APPROVE_REQUISITION("approve requisition", "Permission to approve and reject requisitions"),
  UPLOADS("upload", "Permission to upload"),
  MANAGE_FACILITY("manage facility", "Permission to manage facility(crud)"),
  CONFIGURE_RNR("configure rnr", "Permission to create and edit r&r template for any program");


  private final String name;
  private final String description;

  Right(String name, String description) {
    this.name = name;
    this.description = description;
  }

  public String getName() {
    return name;
  }
}
