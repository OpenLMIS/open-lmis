package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import org.openlmis.core.domain.User;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * e-lmis
 * Created by: Elias Muluneh
 * Date: 8/13/13
 * Time: 12:08 PM
 */
@Repository
public interface ApproverMapper {

  @Select("select distinct u.* from requisitions r " +
      "join facilities f on r.facilityid = f.id " +
      "join users u on u.facilityid = f.id " +
      "join role_assignments ra " +
          "on ra.userid = u.id and ra.programid = r.programid " +
          "and roleid in (select roleid from role_rights where rightname = 'AUTHORIZE_REQUISITION') \n" +
      "where r.id = #{RnrID}")
  List<User> getFacilityBasedAuthorizers( @Param(value = "RnrID") Long RnrID );

  @Select("select distinct u.* from requisitions r " +
      "join role_assignments ra on ra.supervisoryNodeId = r.supervisoryNodeId " +
      "join users u on u.id = ra.userid " +
      " and ra.programid = r.programid " +
      "and roleid in (select roleid from role_rights where rightname = 'APPROVE_REQUISITION') \n" +
      "where r.id = #{RnrID}")
  List<User> getNextSupervisors( @Param(value = "RnrID") Long RnrID );

}
