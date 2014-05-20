package org.openlmis.report.service.lookup;

import org.openlmis.report.mapper.AverageConsumptionReportMapper;
import org.openlmis.report.mapper.lookup.DashboardMapper;
import org.openlmis.report.mapper.lookup.RnRStatusSummaryReportMapper;
import org.openlmis.report.model.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

/**
 * User: Issa
 * Date: 2/18/14
 * Time: 5:32 PM
 */
@Service
public class DashboardLookupService {

    @Autowired
    DashboardMapper dashboardMapper;
    @Autowired
    AverageConsumptionReportMapper avgMapper;

    @Autowired
    RnRStatusSummaryReportMapper rnRStatusSummaryReportMapper;

    private String  getCommaSeparatedIds(List<Long> idList){

        return idList == null ? "{}" : idList.toString().replace("[", "{").replace("]", "}");
    }

    public List<ItemFillRate> getItemFillRate(Long periodId, Long facilityId, Long programId,List<Long> productListId){

        return dashboardMapper.getItemFillRate(periodId, facilityId, programId,getCommaSeparatedIds(productListId));
    }

    public OrderFillRate getOrderFillRate(Long periodId, Long facilityId, Long programId){

        return dashboardMapper.getOrderFillRate(periodId, facilityId, programId);
    }

    public List<ShipmentLeadTime> getShipmentLeadTime(Long periodId, Long programId, List<Long> rgroupId){
        return dashboardMapper.getShipmentLeadTime(periodId,programId, getCommaSeparatedIds(rgroupId));

    }

    public List<StockingInfo> getStockEfficiencyData(Long periodId, Long programId,List<Long> rgroupId, List<Long> productListId){
        return dashboardMapper.getStockEfficiencyData(periodId, programId,getCommaSeparatedIds(rgroupId), getCommaSeparatedIds(productListId));

    }
    public List<StockingInfo> getStockEfficiencyDetailData(Long periodId, Long programId, List<Long> rgroupId, List<Long> productListId){
        return dashboardMapper.getStockEfficiencyDetailData(periodId, programId,getCommaSeparatedIds(rgroupId), getCommaSeparatedIds(productListId));

    }

    public List<StockOut> getStockOutFacilities(Long periodId, Long programId, Long productId, List<Long> requisitionGroupId){
        return dashboardMapper.getStockOutFacilities(periodId, programId, productId, getCommaSeparatedIds(requisitionGroupId));

    }
    public List<StockOut> getStockOutFacilitiesByRequisitionGroup(Long periodId, Long programId, Long productId, Long requisitionGroupId){
        return dashboardMapper.getStockOutFacilitiesForRequisitionGroup(periodId, programId, productId, requisitionGroupId);

    }
    public List<AlertSummary> getAlerts(Long userId, Long supervisoryNodeId, Long programId){
        return dashboardMapper.getAlerts(userId,supervisoryNodeId, programId );

    }

    public List<AlertSummary> getNotificationAlerts(){
        return dashboardMapper.getNotificationAlerts();

    }

    public List<HashMap> getNotificationsByCategory(String detailTable, Long alertId) {
        if (detailTable == null || detailTable.isEmpty()) return null;
        return dashboardMapper.getNotificationDetails(detailTable, alertId);
    }

    public void sendNotification(Notification notification){
        if(notification == null) return;

        if(notification.getEmails()!= null && !notification.getEmails().isEmpty()){
            for (String email : notification.getEmails()){
                if(email != null && !email.isEmpty()){
                    dashboardMapper.saveEmailNotification(email,notification.getEmailMessage());
                }
            }
        }

        if (notification.getPhoneNumbers() != null && !notification.getPhoneNumbers().isEmpty()){
            for (String phoneNumber : notification.getPhoneNumbers()){
                if(phoneNumber !=null && !phoneNumber.isEmpty()){
                    dashboardMapper.saveSmsNotification(notification.getSmsMessage(),phoneNumber,"O");
                }
            }
        }
    }

    public String getPeriod(Long id){
        return dashboardMapper.getPeriod(id);
    }

    public List<RnRStatusSummaryReport>getRnRStatusSummary(Long requisionGroupId){
        return rnRStatusSummaryReportMapper.getRnRStatusSummaryData(requisionGroupId);
    }

    public List<HashMap> getReportingPerformance(Long periodId, Long programId){
        return dashboardMapper.getReportingPerformance(periodId,programId);

    }
}
