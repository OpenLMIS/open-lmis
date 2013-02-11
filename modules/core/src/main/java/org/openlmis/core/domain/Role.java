package org.openlmis.core.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.core.exception.DataException;

import java.util.Date;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class
    Role {
  private Integer id;
  private String name;
  private String description;
  private Integer modifiedBy;
  private Date modifiedDate;
  private Set<Right> rights;

  public Role(String name, String description) {
    this(null, name, description);
  }

  public Role(Integer id, String name, String description) {
    this(id, name, description, null, null, null);
  }

  public void validate() {
    if (name == null || name.isEmpty()) throw new DataException("Role can not be created without name.");
    if (rights == null || rights.isEmpty())
      throw new DataException("Role can not be created without any rights assigned to it.");
  }
}
