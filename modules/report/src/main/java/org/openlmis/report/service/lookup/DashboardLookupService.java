package org.openlmis.report.service.lookup;

import org.openlmis.core.domain.User;
import org.openlmis.report.mapper.lookup.DashboardMapper;
import org.openlmis.report.mapper.lookup.RnRStatusSummaryReportMapper;
import org.openlmis.report.model.dto.*;
import org.openlmis.report.util.Constants;
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
   // @Autowired
    //AverageConsumptionReportMapper avgMapper;

    @Autowired
    RnRStatusSummaryReportMapper rnRStatusSummaryReportMapper;

    public static String  getCommaSeparatedIds(List<Long> idList){

        return idList == null ? "{}" : idList.toString().replace("[", "{").replace("]", "}");
    }

    public List<ItemFillRate> getItemFillRate(Long periodId, Long facilityId, Long programId,List<Long> productListId){

        return dashboardMapper.getItemFillRate(periodId, facilityId, programId,getCommaSeparatedIds(productListId));
    }

    public OrderFillRate getOrderFillRate(Long periodId, Long facilityId, Long programId){

        return dashboardMapper.getOrderFillRate(periodId, facilityId, programId);
    }

    public List<ShipmentLeadTime> getShipmentLeadTime(Long userId,Long periodId, Long programId, Long zoneId){
        return dashboardMapper.getShipmentLeadTime(userId,periodId,programId, zoneId);

    }

    public List<StockingInfo> getStockEfficiencyData(Long userId,Long periodId, Long programId, Long zoneId, List<Long> productListId){
        return dashboardMapper.getStockEfficiencyData(userId,periodId, programId, zoneId , getCommaSeparatedIds(productListId));

    }
    public List<StockingInfo> getStockEfficiencyDetailData(Long userId,Long periodId, Long programId, Long zoneId, List<Long> productListId){
        return dashboardMapper.getStockEfficiencyDetailData(userId, periodId, programId,zoneId, getCommaSeparatedIds(productListId));

    }

    public List<StockOut> getStockOutFacilities(Long userId, Long periodId, Long programId, Long productId, Long zoneId){
        return dashboardMapper.getStockOutFacilities(userId, periodId, programId, productId, zoneId);

    }
    public List<StockOut> getStockOutFacilitiesByGeographicZoneFilter(Long userId, Long periodId, Long programId, Long productId, Long requisitionGroupId){
        return dashboardMapper.getStockOutFacilitiesForGeographicZone(userId, periodId, programId, productId, requisitionGroupId);

    }
    public List<AlertSummary> getAlerts(Long userId, Long programId, Long periodId, Long zoneId){
        return dashboardMapper.getAlerts(userId, programId, periodId, zoneId );

    }
    public List<AlertSummary> getStockedOutAlerts(Long userId, Long programId, Long periodId, Long zoneId){
        return dashboardMapper.getStockedOutAlerts(userId, programId, periodId, zoneId );

    }

    public List<AlertSummary> getNotificationAlerts(){
        return dashboardMapper.getNotificationAlerts();

    }

    public List<HashMap> getNotificationsByCategory(Long userId, Long programId, Long periodId, Long zoneId,String detailTable) {
        if (detailTable == null || detailTable.isEmpty()) return null;
        return dashboardMapper.getNotificationDetails(userId,programId,periodId,zoneId,detailTable);
    }

    public List<HashMap> getStockedOutNotificationDetails(Long userId, Long programId, Long periodId, Long zoneId, Long productId,String detailTable) {
        if (detailTable == null || detailTable.isEmpty()) return null;
        return dashboardMapper.getStockedOutNotificationDetails(userId,programId,periodId,zoneId,productId,detailTable);
    }

    public void sendNotification(Notification notification){
        if(notification == null) return;

        for(String notificationMethod : notification.getNotificationMethods()){
            if(notificationMethod.equalsIgnoreCase(Constants.NOTIFICATION_METHOD_EMAIL)){
                for(User receiver: notification.getReceivers()){
                    if(receiver.getPrimaryNotificationMethod() == null || receiver.getPrimaryNotificationMethod().equalsIgnoreCase(Constants.USER_PRIMARY_NOTIFICATION_METHOD_EMAIL)){
                        if (receiver.getEmail() != null && !receiver.getEmail().isEmpty()){
                            dashboardMapper.saveEmailNotification(receiver.getEmail(),notification.getEmailMessage());
                        }
                    }
                }

            }else if (notificationMethod.equalsIgnoreCase(Constants.NOTIFICATION_METHOD_SMS)){
                for(User receiver: notification.getReceivers()){
                    if(receiver.getPrimaryNotificationMethod() == null || receiver.getPrimaryNotificationMethod().equalsIgnoreCase(Constants.USER_PRIMARY_NOTIFICATION_METHOD_CELL_PHONE)){
                        if (receiver.getCellPhone() != null && !receiver.getCellPhone().isEmpty()){
                            dashboardMapper.saveSmsNotification(notification.getSmsMessage(),receiver.getCellPhone(),"O");
                        }
                    }
                }
            }
        }

    }

    public String getYearOfPeriodById(Long id){
        return dashboardMapper.getYearOfPeriodById(id);
    }

    public List<RnRStatusSummaryReport>getRnRStatusSummary(Long requisionGroupId){
        return rnRStatusSummaryReportMapper.getRnRStatusSummaryData(requisionGroupId);
    }

    public List<HashMap> getReportingPerformance(Long userId,Long periodId, Long programId,  Long zoneId){
        return dashboardMapper.getReportingPerformance(userId,periodId,programId, zoneId);
    }
    public List<ReportingPerformance> getReportingPerformanceDetail(Long userId,Long periodId, Long programId, Long zoneId, String status){
        return dashboardMapper.getReportingPerformanceDetail(userId,periodId,programId,zoneId, status);
    }
    public List<RnRStatusSummaryReport>getRnRStatusDetails(Long requisitionGroupId,Long programId,Long periodId){
        return rnRStatusSummaryReportMapper.getRnRStatusDetails(requisitionGroupId,programId,periodId);
    }

    public List<RnRStatusSummaryReport> getRnRStatusSummary(Long userId, Long zoneId, Long periodId, Long programId){
        return  rnRStatusSummaryReportMapper.getRnRStatusSummary(userId, zoneId, periodId, programId);
    }
    public List<RnRStatusSummaryReport>getRnRStatusByRequisitionGroupAndPeriodData(Long requisitionGroupId,Long periodId){
        return rnRStatusSummaryReportMapper.getRnRStatusByRequisitionGroupAndPeriodData(requisitionGroupId,periodId);
    }

    public List<RnRStatusSummaryReport> getRnRStatusDetail(Long userId, Long periodId, Long programId,  Long zoneId, String status){
        return rnRStatusSummaryReportMapper.getRnRStatusDetail(userId,periodId,programId, zoneId, status);
    }

}
