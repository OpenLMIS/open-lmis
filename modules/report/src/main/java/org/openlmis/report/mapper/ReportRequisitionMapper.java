package org.openlmis.report.mapper;

import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportRequisitionMapper {

  @Select("SELECT max(name) from " +
    " facilities where facilities.id in ( select facilityid from requisitions where requisitions.id = #{rnrId} )")
  String getFacilityNameForRnrId(Long rnrId);

  @Select("SELECT max(name) from " +
    " programs where programs.id in ( select programid from requisitions where requisitions.id = #{rnrId} )")
  String getProgramNameForRnrId(Long rnrId);

  @Select("SELECT max(name) from " +
    " processing_periods where processing_periods.id in ( select periodid from requisitions where requisitions.id = #{rnrId} )")
  String getPeriodTextForRnrId(Long rnrId);

}
