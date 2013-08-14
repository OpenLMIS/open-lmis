package org.openlmis.core.service;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.openlmis.core.domain.User;
import org.openlmis.core.repository.mapper.ApproverMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * e-lmis
 * Created by: Elias Muluneh
 * Date: 8/13/13
 * Time: 11:48 AM
 */
@Service
@NoArgsConstructor
@AllArgsConstructor
public class ApproverService {

  @Autowired
  ApproverMapper approverMapper;

  public List<User> getNextApprovers(Long RnrID){
    return approverMapper.getNextSupervisors(RnrID);
  }

  public List<User> getFacilityBasedAutorizers(Long RnrID){
    return approverMapper.getFacilityBasedAuthorizers(RnrID);
  }



}
