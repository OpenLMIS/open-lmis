package org.openlmis.core.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FacilityImages {

  private byte[] firstPicture;
  private byte[] secondPicture;
  private byte[] thirdPicture;
  private byte[] fourthPicture;
  private byte[] fifthPicture;

}
