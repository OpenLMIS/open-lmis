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
var dashboard = angular.module('dashboard',['openlmis', 'ui.calendar', 'ui.bootstrap','easypiechart','ui.chart','ngTable','nsPopover','gridshore.c3js.chart']).config(['$routeProvider', function ($routeProvider) {

    $routeProvider.
        when('/dashboard', {controller: AdminDashboardController, templateUrl: 'partials/dashboard.html', resolve : ResolveDashboardFormData}).
       // when('/dashboard', {controller: DashboardProgramController, templateUrl: 'partials/dashboard-program.html', resolve : ResolveDashboardFormData}).
        when('/dashboard-new', {controller: DashboardProgramController, templateUrl: 'partials/dashboard-program.html', resolve : ResolveDashboardFormData}).
        when('/view-stock-detail/:programId/:periodId/:productId', {controller: StockController, templateUrl: 'partials/stock.html', resolve : ResolveDashboardFormData}).
        when('/stock', {controller: StockController, templateUrl: 'partials/stock.html', resolve : ResolveDashboardFormData }).
        when('/leadTime', {controller: ShipmentLeadTimeController, templateUrl: 'partials/shipment-lead-time.html', resolve : ResolveDashboardFormData }).
        when('/stock-out', {controller: StockedOutController, templateUrl: 'partials/stocked-out.html', resolve : ResolveDashboardFormData }).
        when('/district-stock-out/:programId/:periodId/:zoneId/:productId', {controller: DistrictStockedOutController, templateUrl: 'partials/requisition-group-stocked-out.html', resolve : ResolveDashboardFormData }).
        when('/stock-out-detail/:programId/:periodId/:zoneId/:productId', {controller: StockedOutDetailController, templateUrl: 'partials/stocked-out-detail.html', resolve : ResolveDashboardFormData }).
        when('/notifications/:programId/:periodId/:zoneId/:productId/:detailTable', {controller: NotificationsDetailController, templateUrl: 'partials/notifications-detail.html', resolve : ResolveDashboardFormData }).
        when('/notifications/:programId/:periodId/:zoneId/:detailTable', {controller: NotificationsDetailController, templateUrl: 'partials/notifications-detail.html', resolve : ResolveDashboardFormData }).

        when('/notifications', {controller: SendNotificationController, templateUrl: 'partials/send-notifications.html', resolve : ResolveDashboardFormData }).
        when('/rnr-status-summary', {controller: RequisitionStatusSummaryController, templateUrl: 'partials/rnr-status-summary.html', resolve : ResolveDashboardFormData }).
        when('/reporting-performance/program/:programId/period/:periodId', {controller: ReportingPerformanceDetailController, templateUrl: 'partials/reporting-performance-detail.html', resolve : ResolveDashboardFormData }).
        when('/rnr-status-report/program/:programId/period/:periodId', {controller: RnRStatusDetailsController, templateUrl: 'partials/rnr-status-details.html', resolve : ResolveDashboardFormData }).
        otherwise({redirectTo: '/dashboard'});
}]);

dashboard.directive('notificationDetail', function ($compile, $http, $templateCache) {

    var getTemplate = function(contentType) {
        var templateLoader,
            baseUrl = '/public/pages/dashboard/templates/';
        var templateUrl = baseUrl + contentType.toLowerCase()+'.html';
        templateLoader = $http.get(templateUrl, {cache: $templateCache});

        return templateLoader;
    };

    var linker = function(scope, element, attrs) {
        var loader = getTemplate(scope.content.tableName);
        var promise = loader.success(function(html) {
            element.html(html);
        }).error(function(){
            var defaultLoader = getTemplate('default_template');
            defaultLoader.success(function(html){
                element.html(html);
            }).then(function(response){
                element.replaceWith($compile(element.html())(scope));
            });
        }).then(function (response) {
            element.replaceWith($compile(element.html())(scope));
        });
    };

    return {
        restrict: "E",
        rep1ace: true,
        link: linker,
        scope: {
            content:'=ngModel'
        }
    };
});
