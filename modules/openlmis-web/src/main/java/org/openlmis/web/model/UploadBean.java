package org.openlmis.web.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonMethod;
import org.codehaus.jackson.annotate.JsonProperty;
import org.openlmis.upload.Importable;
import org.openlmis.upload.RecordHandler;

@NoArgsConstructor
@AllArgsConstructor
@JsonAutoDetect(value = JsonMethod.NONE, fieldVisibility = JsonAutoDetect.Visibility.NONE)
public class UploadBean {
  @Setter
  private String displayName;

  @Setter @Getter
  private RecordHandler recordHandler;

  @Setter @Getter
  private Class<? extends Importable> importableClass;

  @Setter @Getter
  private String tableName;

  public UploadBean(String displayName, RecordHandler handler, Class<? extends Importable> importableClass) {
    this.displayName = displayName;
    this.recordHandler = handler;
    this.importableClass = importableClass;
  }


  @JsonProperty
  @SuppressWarnings("unused")
  public String getDisplayName() {
    return displayName;
  }
}
