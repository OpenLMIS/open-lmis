package org.openlmis.core.service;

import org.openlmis.core.domain.User;
import org.openlmis.core.repository.mapper.ApproverMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * e-lmis
 * Created by: Elias Muluneh
 * Date: 8/13/13
 * Time: 11:48 AM
 */
public class ApproverService {

  @Autowired
  ApproverMapper approverMapper;

  public List<User> getNextApprovers(Long RnrID){
     return approverMapper.getFacilityBasedAuthorizers(RnrID);
  }

  public List<User> getFacilityBasedAutorizers(Long RnrID){
     return approverMapper.getFacilityBasedAuthorizers(RnrID);
  }



}
