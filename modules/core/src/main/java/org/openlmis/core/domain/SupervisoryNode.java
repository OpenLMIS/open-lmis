package org.openlmis.core.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.upload.Importable;
import org.openlmis.upload.annotation.ImportField;

import java.util.Date;

@Data
@NoArgsConstructor
public class SupervisoryNode implements Importable, BaseModel {

    Integer id;

    @ImportField(name = "Supervisory Node Code", mandatory = true)
    private String code;

    @ImportField(name = "Name of Node", mandatory = true)
    private String name;

    @ImportField(name = "Description")
    private String description;

    @ImportField(name = "Parent Node", nested = "code")
    private SupervisoryNode parent;

    @ImportField(name = "Facility Code", mandatory = true, nested = "code")
    private Facility facility;

    private Date modifiedDate;
    private Integer modifiedBy;

  public SupervisoryNode(Integer id) {
    this.id = id;
  }

  @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SupervisoryNode that = (SupervisoryNode) o;

        if (!id.equals(that.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
