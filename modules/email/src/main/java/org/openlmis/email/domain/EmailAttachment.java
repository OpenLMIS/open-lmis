package org.openlmis.email.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.activation.DataSource;

@Data
@AllArgsConstructor
public class EmailAttachment {
  private String attachmentName;
  private DataSource fileDataSource;

}
