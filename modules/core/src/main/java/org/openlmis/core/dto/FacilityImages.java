package org.openlmis.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FacilityImages {

  private byte[] firstPicture;
  private byte[] secondPicture;
  private byte[] thirdPicture;
  private byte[] fourthPicture;
  private byte[] fifthPicture;

}
