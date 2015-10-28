/*
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 * Copyright (C) 2015  John Snow, Inc (JSI). This program was produced for the U.S. Agency for International Development (USAID). It was prepared under the USAID | DELIVER PROJECT, Task Order 4.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.openlmis.report.service.lookup;

import org.openlmis.core.domain.User;
import org.openlmis.report.mapper.lookup.DashboardMapper;
import org.openlmis.report.mapper.lookup.RnRStatusSummaryReportMapper;
import org.openlmis.report.model.DashboardLookUpReportHeader;
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
        return dashboardMapper.getShipmentLeadTime(userId, periodId, programId, zoneId);

    }

    public List<StockingInfo> getStockEfficiencyData(Long userId,Long periodId, Long programId, Long zoneId, List<Long> productListId){
        return dashboardMapper.getStockEfficiencyData(userId, periodId, programId, zoneId, getCommaSeparatedIds(productListId));

    }
    public List<StockingInfo> getStockEfficiencyDetailData(Long userId,Long periodId, Long programId, Long zoneId, List<Long> productListId){
        return dashboardMapper.getStockEfficiencyDetailData(userId, periodId, programId, zoneId, getCommaSeparatedIds(productListId));

    }

    public List<StockOut> getStockOutFacilities(Long userId, Long periodId, Long programId, Long productId, Long zoneId){
        return dashboardMapper.getStockOutFacilities(userId, periodId, programId, productId, zoneId);

    }
    public List<StockOut> getStockOutFacilitiesByGeographicZoneFilter(Long userId, Long periodId, Long programId, Long productId, Long requisitionGroupId){
        return dashboardMapper.getStockOutFacilitiesForGeographicZone(userId, periodId, programId, productId, requisitionGroupId);

    }
    public List<AlertSummary> getAlerts(Long userId, Long programId, Long periodId, Long zoneId){
        return dashboardMapper.getAlerts(userId, programId, periodId, zoneId);

    }
    public List<AlertSummary> getStockedOutAlerts(Long userId, Long programId, Long periodId, Long zoneId){
        return dashboardMapper.getStockedOutAlerts(userId, programId, periodId, zoneId);

    }

    public List<AlertSummary> getNotificationAlerts(){
        return dashboardMapper.getNotificationAlerts();

    }

    public List<HashMap> getNotificationsByCategory(Long userId, Long programId, Long periodId, Long zoneId,String detailTable) {
        if (detailTable == null || detailTable.isEmpty()) return null;
        return dashboardMapper.getNotificationDetails(userId, programId, periodId, zoneId, detailTable);
    }

    public List<HashMap> getStockedOutNotificationDetails(Long userId, Long programId, Long periodId, Long zoneId, Long productId,String detailTable) {
        if (detailTable == null || detailTable.isEmpty()) return null;
        return dashboardMapper.getStockedOutNotificationDetails(userId, programId, periodId, zoneId, productId, detailTable);
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

    public ReportingStatus getReportingPerformance(Long userId,Long periodId, Long programId,  Long zoneId){
        ReportingStatus status = dashboardMapper.getReportingPerformance(userId,periodId,programId, zoneId);
        if (status != null)
            status.setNonReporting(status.getTotal() - status.getReporting());
        return status;
    }
    public List<ReportingPerformance> getReportingPerformanceDetail(Long userId,Long periodId, Long programId, Long zoneId, String status){
        return dashboardMapper.getReportingPerformanceDetail(userId,periodId,programId,zoneId, status);
    }
    public List<RnRStatusSummaryReport>getRnRStatusDetails(Long requisitionGroupId,Long programId,Long periodId){
        return rnRStatusSummaryReportMapper.getRnRStatusDetails(requisitionGroupId, programId, periodId);
    }

    public List<RnRStatusSummaryReport> getRnRStatusSummary(Long userId, Long zoneId, Long periodId, Long programId){
        return  rnRStatusSummaryReportMapper.getRnRStatusSummary(userId, zoneId, periodId, programId);
    }
    public List<RnRStatusSummaryReport> getEmergencyRnRStatusSummary(Long userId, Long zoneId, Long periodId, Long programId){
        return  rnRStatusSummaryReportMapper.getEmergencyRnRStatusSummary(userId, zoneId, periodId, programId);
    }
    public List<RnRStatusSummaryReport>getRnRStatusByRequisitionGroupAndPeriodData(Long requisitionGroupId,Long periodId){
        return rnRStatusSummaryReportMapper.getRnRStatusByRequisitionGroupAndPeriodData(requisitionGroupId, periodId);
    }

    public List<RnRStatusSummaryReport> getRnRStatusDetail(Long userId, Long periodId, Long programId,  Long zoneId, String status){
        return rnRStatusSummaryReportMapper.getRnRStatusDetail(userId, periodId, programId, zoneId, status);
    }
    public List<RnRStatusSummaryReport>getExtraAnalyticsDataForRnRSummary(Long userId, Long zoneId,Long periodId, Long programId){
        return rnRStatusSummaryReportMapper.getExtraAnalyticsDataForRnRSummary(userId, zoneId, periodId, programId);
    }

    public DashboardLookUpReportHeader getProductNameById(Long id,long periodId) {
        DashboardLookUpReportHeader dashboardLookUpReportHeader= new DashboardLookUpReportHeader();
        String productName=dashboardMapper.getProductNameById(id);
        String periodName= dashboardMapper.getPeriodName(periodId);
        dashboardLookUpReportHeader.setProductName(productName);
        dashboardLookUpReportHeader.setPeriodName(periodName);
        return dashboardLookUpReportHeader;
    }

    public List<HashMap<String, Object>> getProgramPeriodTracerProductsTrend(Long programId, Long periodId, Long userId, Long limit) {
        return dashboardMapper.getProgramPeriodTracerProductTrend(programId, periodId, userId);
    }
    public HashMap<String, Object> getDashboardReportingPerformance(Long programId, Long periodId, Long userId) {
        return dashboardMapper.getDashboardReportingPerformance(programId, periodId, userId);
    }

    public List<HashMap<String, Object>> getDistrictStockSummary(Long programId, Long periodId, Long userId) {
        return dashboardMapper.getDistrictStockSummary(programId, periodId, userId);
    }
    public List<HashMap<String, Object>> getFacilityStockSummary(Long programId, Long periodId, Long userId) {
        return dashboardMapper.getFacilityStockSummary(programId, periodId, userId);
    }
    public List<HashMap<String, Object>> getFacilitiesStockedOut(Long programId, Long periodId, String productCode) {
        return dashboardMapper.getFacilityStockedOut(programId, periodId, productCode);
    }
}
