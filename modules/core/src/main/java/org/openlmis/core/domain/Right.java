package org.openlmis.core.domain;

public enum Right {
    VIEW_REQUISITION(1, "view requisition", "Permission to view requisitions"),
    CREATE_REQUISITION(2, "create requisition", "Permission to create, edit, submit and recall requisitions"),
    APPROVE_REQUISITION(3, "approve requisition", "Permission to approve and reject requisitions");

    private int id;
    private final String name;
    private final String description;

    Right(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    @SuppressWarnings("unused")
    public int getId() {
        return this.id;
    }

    public String getName() {
        return name;
    }
}
