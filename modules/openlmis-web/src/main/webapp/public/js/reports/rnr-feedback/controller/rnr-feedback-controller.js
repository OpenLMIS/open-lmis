/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

function RnRFeedbackController($scope, ngTableParams, $filter, RnRFeedbackReport, Products ,ReportFacilityTypes,OperationYears,ReportPeriods,ReportPeriodsByScheduleAndYear,AllReportPeriods,ReportFilteredPeriods, $http,ReportSchedules, ReportPrograms,RequisitionGroups,AllFacilites,GetFacilityByFacilityType,SettingsByKey, $routeParams,$location) {
    //to minimize and maximize the filter section
    var section = 1;

    $scope.section = function (id) {
        section = id;
    };

    $scope.showMessage = true;
    $scope.message = "Indicates a required field." ;

    $scope.IndicatorProductsKey = "INDICATOR_PRODUCTS";

    SettingsByKey.get({key: $scope.IndicatorProductsKey},function (data){
        $scope.IndicatorProductsDescription = data.settings.value;
    });

    $scope.orderTypes = [
        {'name':'Regular', 'value':'Regular'},
        {'name':'Emergency', 'value':'Emergency'}
    ];

    //Order type defaults to Regular
    $scope.orderType = 'Regular' ;


    $scope.exportReport   = function (type){
        $scope.filter.pdformat =1;
        var params = jQuery.param($scope.filter);
        var url = '/reports/download/rnr_feedback/' + type +'?' + params;
        window.open(url);

    };



    $scope.OnFilterChanged = function () {

      $scope.data = $scope.datarows = [];
      $scope.filter.max = 10000;
      $scope.filter.page = 1;

      RnRFeedbackReport.get($scope.filter, function(data) {
          $scope.data         = data.pages.rows ;
          $scope.paramsChanged( $scope.tableParams );
      });
    };

    $scope.formatNumber = function(value){
        return utils.formatNumber(value,'0,000');
    };

    function checkMinimumFilled()
    {
        // check valid value of each field minimum selection to run the application
        if ($scope.program > 0 && $scope.schedule > 0 && $scope.period > 0 && (typeof($scope.orderType) !== "undefined")){
            return true;        }
        return false;
    }

    $scope.showFacility = function(fac) {
        showFacilityName = (fac!=$scope.currentFacility);
        $scope.currentFacility = fac;
        return showFacilityName;
    };


}
