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

function RnRFeedbackController($scope, RnRFeedbackReport,SettingsByKey) {


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
        var params = jQuery.param($scope.getSanitizedParameter());
        var url = '/reports/download/rnr_feedback/' + type +'?' + params;
        window.open(url, "_BLANK");

    };



    $scope.OnFilterChanged = function () {

      $scope.data = $scope.datarows = [];
      $scope.filter.max = 10000;
      $scope.filter.page = 1;

      RnRFeedbackReport.get($scope.getSanitizedParameter(), function(data) {
          $scope.data         = data.pages.rows ;
          $scope.paramsChanged( $scope.tableParams );
      });
    };

    $scope.formatNumber = function(value){
        return utils.formatNumber(value,'0,000');
    };

    $scope.showFacility = function(fac) {
        showFacilityName = (fac!=$scope.currentFacility);
        $scope.currentFacility = fac;
        return showFacilityName;
    };


}
