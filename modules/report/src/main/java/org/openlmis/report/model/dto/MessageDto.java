package org.openlmis.report.model.dto;


import lombok.Data;

@Data
public class MessageDto {

  private String type;
  private String message;
  private String contact;
  private int facility_id;

}
