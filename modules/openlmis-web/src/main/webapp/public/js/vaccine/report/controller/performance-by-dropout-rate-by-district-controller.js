/*
 *
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 *  Copyright (C) 2015  John Snow, Inc (JSI). This program was produced for the U.S. Agency for International Development (USAID). It was prepared under the USAID | DELIVER PROJECT, Task Order 4.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 *    You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

function ViewPerformanceByDropoutRateByDistrictController($scope,  PerformanceByDropoutRateByDistrict, VaccineSupervisedIvdPrograms, $routeParams) {

    $scope.customPeriod;
    $scope.products;
    $scope.report;
    $scope.error_message;


    var maxReportSubmission = 10;
    var maxReportSubmissionKey;



    $scope.OnFilterChanged = function () {
        $scope.data = $scope.datarows = [];
        $scope.filter.facilityId='' ;
        $scope.filter.geographicZoneId = $scope.filter.zone;
        $scope.filter.productId = $scope.filter.product;
        $scope.filter.periodId = 0;
        $scope.filter.programId = $scope.filter.program;

        $scope.getStartDate();
        $scope.reportType=false;

        var sd= new Date($scope.filter.periodStart);
        var ed= new Date($scope.filter.periodEnd);
        var monthsDifference=0;
     var param=   $scope.filter;
       if(sd.getTime() <ed.getTime()) {
           monthsDifference=monthDiff(sd,ed);
           if(monthsDifference<=12){

           $scope.error_message='';
           PerformanceByDropoutRateByDistrict.get(param, function (data) {
               if (data !== undefined) {

                   $scope.data = data.PerformanceByDropoutRateList.performanceByDropOutDistrictsList;
                   $scope.datarows = $scope.data;
                   $scope.regionrows = data.PerformanceByDropoutRateList.performanceByDropOutRegionsList;
                   $scope.reportType = data.PerformanceByDropoutRateList.facillityReport;
                   $scope.columnVals = data.PerformanceByDropoutRateList.columnNames;
                   $scope.regionColumnVals = data.PerformanceByDropoutRateList.regionColumnsValueList;
                   $scope.report = data.PerformanceByDropoutRateList;
                   $scope.colValueList = data.PerformanceByDropoutRateList.columnsValueList;


               }
           });}
           else{
               $scope.error_message=' Month Difference between Start and End Cannot be more than 12 !!';
           }
       }else{
           $scope.error_message=' End date Should be Greater than start date !!';
       }
    };
    function monthDiff(d1, d2) {
        var months;
        months = (d2.getFullYear() - d1.getFullYear()) * 12;
        months -= d1.getMonth() + 1;
        months += d2.getMonth();
        return months <= 0 ? 0 : months;
    }
     $scope.getBackGroundColor=function(value) {
        var bgColor='';
        if(value>20){
            bgColor='red';
        }else if(value>10){
            bgColor='lightblue';
        }else if(value>5){
            bgColor='yellow';
        }else{
            bgColor='lightgreen';
        }
        return bgColor;
    };
    $scope.getBackGroundColorSummary=function(value) {
        var bgColor='';
        if(value=='4_dropoutGreaterThanHigh'){
            bgColor='red';
        }else if(value=='3_droOputBetweenMidAndHigh'){
            bgColor='lightblue';
        }else if(value=='2_dropOutBetweenMidAndMin'){
            bgColor='yellow';
        }else{
            bgColor='lightgreen';
        }
        return bgColor;
    };
    $scope.getColumnNameSummary=function(value) {
        var bgColor='';
        if(value=='4_dropoutGreaterThanHigh'){
            bgColor='DO >20%';
        }else if(value=='3_droOputBetweenMidAndHigh'){
            bgColor='5% < DO <=10%';
        }else if(value=='2_dropOutBetweenMidAndMin'){
            bgColor=' 10% < DO <=20%';
        }else{
            bgColor='DO <=5';
        }
        return bgColor;
    };
    $scope.calculateTotalPercentage=function(total_bcg_vaccinated,total_mr_vaccinated) {

        return total_bcg_vaccinated===0 ? 0: (total_bcg_vaccinated/(total_bcg_vaccinated-total_mr_vaccinated)*100);
    };
    $scope.concatPercentage=function(value) {

        return value+'%';
    };
    $scope.showCategory = function (index) {
        var absIndex = ($scope.pageSize * ($scope.currentPage - 1)) + index;
        return !((index > 0 ) && ($scope.colValueList.length > absIndex) && ($scope.rnr.equipmentLineItems[absIndex].equipmentCategory == $scope.rnr.equipmentLineItems[absIndex - 1].equipmentCategory));
    };
    $scope.getStartDate = function () {
        if ($scope.filter.periodType != 5) {
            var currentDate = new Date();
            var endDate;
            var startDate;
            var months = 0;
            var monthBack = 0;
            var currentDays = currentDate.getDate();
            if (currentDays <= maxReportSubmission) {
                monthBack = 1;
            }
            endDate = new Date(currentDate.getFullYear(), currentDate.getMonth() - monthBack, 0);
            startDate = new Date(endDate.getFullYear(), endDate.getMonth() + 1, 1);

            //endDate.set
            switch ($scope.filter.periodType) {
                case '1':
                    months = startDate.getMonth() - 1;
                    break;
                case '2':
                    months = startDate.getMonth() - 3;

                    break;
                case '3':
                    months = startDate.getMonth() - 6;
                    break;
                case '4':
                    months = startDate.getMonth() - 12;
                    break;
                default :
                    months = 0;
            }
            startDate.setMonth(months);
            $scope.filter.periodStart = $.datepicker.formatDate("yy-mm-dd", startDate);
            $scope.filter.periodEnd = $.datepicker.formatDate("yy-mm-dd", endDate);

        }
    };
    $scope.getCurrentPeriodDateRange = function () {
        var d = new Date();
        var quarter = Math.floor((d.getMonth() / 3));
        var firstDate1 = new Date(d.getFullYear(), quarter * 3, 1);
        var endDate1 = new Date(firstDate1.getFullYear(), firstDate1.getMonth() + 3, 0);
        $scope.filter.periodStart = $.datepicker.formatDate("yy-mm-dd", firstDate1);
        $scope.filter.periodEnd = $.datepicker.formatDate("yy-mm-dd", endDate1);

    };

}
