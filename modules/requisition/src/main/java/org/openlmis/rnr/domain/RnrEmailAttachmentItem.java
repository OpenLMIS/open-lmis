package org.openlmis.rnr.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RnrEmailAttachmentItem {

  private String facilityName;
  private String clientSubmittedTime;
  private String productCode;
  private String beginningBalance;
  private String quantityDispensed;
  private String quantityReceived;

  private String totalLossesAndAdjustments;
  private String stockInhand;
  private String quantityRequested;
  private String quantityApproved;

}
