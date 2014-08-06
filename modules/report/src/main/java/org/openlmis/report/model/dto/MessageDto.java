package org.openlmis.report.model.dto;


import lombok.Data;

@Data
public class MessageDto {

  private String type;
  private String message;
  private String address;
  private int facility_id;

}
