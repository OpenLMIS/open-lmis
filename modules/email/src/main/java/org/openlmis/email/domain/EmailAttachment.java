package org.openlmis.email.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.activation.DataSource;
import javax.mail.util.ByteArrayDataSource;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailAttachment {
  private static final Logger logger = LoggerFactory.getLogger(EmailAttachment.class);

  private Long id;
  private String attachmentPath;
  private String attachmentName;
  private String attachmentFileType;
  private DataSource fileDataSource;

  protected Date createdDate;

  public DataSource getFileDataSource() {
    if(fileDataSource != null){
      return fileDataSource;
    }

    InputStream attachment = null;
    try {
      attachment = new FileInputStream(new File(attachmentPath));
      DataSource attachmentDataSource = new ByteArrayDataSource(attachment, attachmentFileType);
      return attachmentDataSource;
    } catch (Exception e) {
      logger.error("Error send attachment file " + e.getMessage());
    } finally {
      if (attachment != null) {
        try {
          attachment.close();
        } catch (IOException e) {
          logger.error("Error close file " + e.getMessage());
        }
      }
    }
    return null;
  }
}
