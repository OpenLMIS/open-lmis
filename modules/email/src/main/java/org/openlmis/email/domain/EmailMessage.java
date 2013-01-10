package org.openlmis.email.domain;

import lombok.Data;

import java.util.Date;

@Data
public class EmailMessage {

  private String to;
  private String from;
  private String subject;
  private String text;
  private String replyTo;
  private String cc;
  private String bcc;
  private Date sentDate;


}
