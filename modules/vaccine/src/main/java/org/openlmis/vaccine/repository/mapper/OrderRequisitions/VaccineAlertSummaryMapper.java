package org.openlmis.vaccine.repository.mapper.OrderRequisitions;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.openlmis.vaccine.domain.VaccineOrderRequisition.VaccineAlertSummary;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VaccineAlertSummaryMapper {

    @Select("SELECT  s.programId,s.periodId, sum (statics_value) staticsValue ,max(s.description) description,max(alerttype) alerttype,max(display_section) displaySection, max(detail_table) detailTable, max(sms_msg_template_key) smsMessageTemplateKey, max(email_msg_template_key) emailMessageTemplateKey\n" +
            "            FROM alert_summary s\n" +
            "            JOIN alerts a on s.alertTypeId = a.alertType\n" +
            "            WHERE s.programId = #{programId}\n" +
            "            AND s.periodId = #{periodId} \n" +
            "            AND s.geographiczoneid in (select geographiczoneid from fn_get_user_geographiczone_children(#{userId}::int, #{zoneId}::int))\n" +
            "            AND a.alerttype = 'VACCINE_ORDER_REQUISITION_PENDING'\n" +
            "            group by s.programId,s.periodId ")
    public List<VaccineAlertSummary>getAlerts(@Param("zoneId") Long zoneId, @Param("programId") Long programId,@Param("periodId") Long periodId,@Param("userId") Long userId);

}
