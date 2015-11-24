package org.openlmis.email.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.activation.DataSource;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailAttachment {
  private Long id;
  private String attachmentPath;
  private String attachmentName;
  private DataSource fileDataSource;

  protected Date createdDate;


}
