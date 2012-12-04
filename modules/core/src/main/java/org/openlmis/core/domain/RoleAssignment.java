package org.openlmis.core.domain;

import lombok.Data;

@Data
public class RoleAssignment {
    private int userName;
    private int roleId;
    private int programId;
}
