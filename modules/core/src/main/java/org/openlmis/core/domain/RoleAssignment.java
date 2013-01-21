package org.openlmis.core.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoleAssignment {
    private Integer userId;
    private Integer roleId;
    private Integer programId;
    private SupervisoryNode supervisoryNode;
}
