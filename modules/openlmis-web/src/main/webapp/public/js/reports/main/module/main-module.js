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
angular.module('mainReport', ['openlmis', 'ngTable', 'angularCombine', 'ui.bootstrap.modal', 'ui.bootstrap.dropdownToggle'])
    .config(['$routeProvider', function ($routeProvider) {
      $routeProvider.
        when('/adjustment-summary', {controller: AdjustmentSummaryReportController, templateUrl:'partials/adjustment-summary.html',reloadOnSearch:false}).
        when('/aggregate-consumption', {controller: AggregateConsumptionReportController, templateUrl:'partials/aggregate-consumption.html',reloadOnSearch:false}).
        when('/aggregate-regimen', {controller: RegimenSummaryControllers, templateUrl:'partials/aggregate-regimen.html',reloadOnSearch:false}).
        when('/cce-repair-management', {controller: RepairManagementController, templateUrl:'partials/cce-repair-management.html',reloadOnSearch:false}).
        when('/cce-storage-capacity', {controller: CCEStorageCapacityReportController, templateUrl:'partials/cce-storage-capacity.html',reloadOnSearch:false}).
        when('/district-consumption', {controller: DistrictConsumptionReportController, templateUrl:'partials/district-consumption.html',reloadOnSearch:false}).
        when('/district-financial-summary', {controller: DistrictFinancialSummaryControllers, templateUrl:'partials/district-financial-summary.html',reloadOnSearch:false}).
        when('/cold-chain-equipment-inventory', {controller: ColdChainEquipmentReportController, templateUrl:'partials/cold-chain-equipment-inventory.html',reloadOnSearch:false}).
        when('/facility-list', {controller: ListFacilitiesController, templateUrl:'partials/facility-list.html',reloadOnSearch:false}).
        when('/lab-equipments-by-donor', {controller: LabEquipmentListByDonorReportController, templateUrl:'partials/lab-equipments-by-donor.html',reloadOnSearch:false}).
        when('/lab-equipments-list', {controller: LabEquipmentListReportController, templateUrl:'partials/lab-equipment-list.html',reloadOnSearch:false}).
        when('/non-reporting', {controller: NonReportingController, templateUrl:'partials/non-reporting.html',reloadOnSearch:false}).
        when('/order', {controller: OrderReportController, templateUrl:'partials/order.html',reloadOnSearch:false}).
        when('/order-fill-rate', {controller: OrderFillRateController, templateUrl:'partials/order-fill-rate.html',reloadOnSearch:false}).
        when('/order-fill-rate-summary', {controller: OrderFillRateReportSummaryController, templateUrl:'partials/order-fill-rate-summary.html',reloadOnSearch:false}).
        when('/pipeline-export', {controller: PipelineExportController, templateUrl:'partials/pipeline-export.html',reloadOnSearch:false}).
        when('/regimen-distribution', {controller: RegimenSummaryControllers, templateUrl:'partials/regimen-distribution.html',reloadOnSearch:false}).
        when('/regimen-summary', {controller: RegimenSummaryControllers, templateUrl:'partials/regimen-summary.html',reloadOnSearch:false}).
        when('/replacement-plan-summary', {controller: ReplacementPlanSummary, templateUrl:'partials/replacement-plan-summary.html',reloadOnSearch:false}).
        when('/rnr-feedback', {controller: RnRFeedbackController, templateUrl:'partials/rnr-feedback.html',reloadOnSearch:false}).
        when('/seasonality-rationing', {controller: SeasonalRationingAdjustment, templateUrl:'partials/seasonality-rationing.html',reloadOnSearch:false}).
        when('/stock-imbalance', {controller: StockImbalanceController, templateUrl:'partials/stock-imbalance.html',reloadOnSearch:false}).
        when('/summary', {controller: SummaryReportController, templateUrl:'partials/summary.html',reloadOnSearch:false}).
        when('/supply-status', {controller: SupplyStatusController, templateUrl:'partials/supply-status.html',reloadOnSearch:false}).
        when('/timeliness', {controller: TimelinessReportController, templateUrl:'partials/timeliness.html',reloadOnSearch:false}).
        when('/user-summary', {controller: UserSummaryReportController, templateUrl:'partials/user-summary.html',reloadOnSearch:false}).

        otherwise({redirectTo:'/adjustment-summary'});
    }]).config(function(angularCombineConfigProvider) {
    angularCombineConfigProvider.addConf(/filter-/, '/public/pages/reports/shared/filters.html');
  });
