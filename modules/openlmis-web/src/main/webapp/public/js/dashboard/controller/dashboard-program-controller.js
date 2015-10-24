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

function DashboardProgramController($scope,$routeParams,$timeout,messageService, dashboardMenuServiceNew, UserSupervisedActivePrograms, GetLastPeriods, GetProgramPeriodTracerProductsTrend, GetStockOutFacilitiesForProgramPeriodAndProductCode, DashboardReportingPerformance, DashboardDistrictStockSummary, DashboardFacilityStockSummary) {
    var dashboardMenuService = dashboardMenuServiceNew;
    $scope.slideTransitionInterval = 20000;

    /* $scope.totalItems = 64;
    $scope.currentPage = 4;
    $scope.maxSize = 5;

    $scope.setPage = function (pageNo) {
        $scope.currentPage = pageNo;
    };

    $scope.bigTotalItems = 175;
    $scope.bigCurrentPage = 1;*/

    var  colors = ["bg-green", "bg-red","bg-blue"];
    /**
     * Returns Random color
     * @returns {string}
     */
    $scope.colorify = function(){
        var index = Math.floor(Math.random() * (2 - 0 + 1)) + 0;
         return colors[index];

    };
    var itemFillRateColors = [{'minRange': -100, 'maxRange': 0, 'color' : '#E23E3E', 'description' : 'Red color for product with a fill rate <= 0 '},
        {'minRange': 1, 'maxRange': 50, 'color' : '#FEBA50', 'description' : 'Yellow color for product with a fill rate > 0 and <= 50 '},
        {'minRange': 51, 'maxRange': 100, 'color' : '#38AB49', 'description' : 'Green color for product with a fill rate > 50 '}];
    var $scaleColor = '#D7D5D5';
    var defaultBarColor = '#FEBA50';
    var $lineWidth = 5;
    var barColor = defaultBarColor;
    var barColors =['#E23E3E','#FEBA50','#38AB49'];

    $scope.tabs = [{title:'Overview', content:'Overview content'},{title:'Legend', content:'Legend content'}];

    $scope.dashboardTabs = dashboardMenuService.tabs;
    $scope.dashboardTabs = [];
    UserSupervisedActivePrograms.get(function(data){
        $scope.programsList = data.programs;
        angular.forEach(data.programs, function(program){

            dashboardMenuService.addTab("'"+program.name+"'",'/public/pages/dashboard/index_new.html#/dashboard-new?programId='+program.id,program.name,false, program.id);
        });
        dashboardMenuService.addTab('Facility','/public/pages/dashboard/index_new.html#/dashboard-new?facilityId=0',messageService.get('label.facility'),false, messageService.get('label.facility'));
        dashboardMenuService.addTab('Notification','/public/pages/dashboard/index_new.html#/dashboard-new?notificationId=0',messageService.get('label.notification'),false, messageService.get('label.notification'));
        $scope.dashboardTabs = dashboardMenuService.tabs;

            if(!isUndefined($routeParams.programId)){
                $scope.currentTab = $scope.programId = $routeParams.programId;
            }else if(!isUndefined($routeParams.facilityId)){

                $scope.currentTab = 'Facility';
            }else if(!isUndefined($routeParams.notificationId)){

                $scope.currentTab = 'Notification';
            }else{

                $scope.currentTab = $scope.programId =  dashboardMenuService.getTab(0).id;
            }
        getLastPeriods();

    });

    var options ={};
    $scope.getFIllRateOption = function(fillRate){

        if(fillRate <= 0){
            barColor = barColors[0];
        }else if(fillRate > 0 && fillRate <= 50){
            barColor = barColors[1];
        }else{
            barColor = barColors[2];
        }
  //  alert('fillrate '+fillRate)
      /*  angular.forEach(itemFillRateColors, function(item){
            if(fillRate <= item.maxRange && fillRate >= item.minRange){
                barColor = item.color;
            }
        });*/
        /*$.each(itemFillRateColors, function(index, item){
            if(fillRate <= item.maxRange && fillRate >= item.minRange){
                barColor = item.color;
            }
        });*/
    //    alert('options '+JSON.stringify({animate:3000, barColor: barColor, scaleColor: $scaleColor, lineWidth: $lineWidth}))
        //options = {animate:3000, barColor: barColor, scaleColor: $scaleColor, lineWidth: $lineWidth};
        options.animate = 3000;
        options.barColor = barColor;
        options.scaleColor = $scaleColor;
        options.lineWidth = $lineWidth;
        return options;
    };

    function getLastPeriods(){
        GetLastPeriods.get({programId: $scope.programId}, function(data){
            $scope.lastPeriods = data.lastPeriods;
            dashboardMenuService.tabs = [];
            angular.forEach( $scope.lastPeriods, function(period){

                dashboardMenuService.addTab("'"+period.name+"'",'/public/pages/dashboard/index_new.html#/dashboard-new?programId='+$scope.currentTab +'&periodId='+period.id,period.name,false, period.id);
            });
            $scope.dashboardPeriodTabs = dashboardMenuService.tabs;
            if(!isUndefined($routeParams.periodId)){
                $scope.currentSubTab = $scope.periodId = $routeParams.periodId;
            }else if(!isUndefined($scope.dashboardPeriodTabs[0])){
                $scope.currentSubTab = $scope.periodId =  $scope.dashboardPeriodTabs[0].id;
            }

            dashboardMenuService.tabs = [];
        });


        $timeout(function(){
            if(!isUndefined($scope.programId) && !isUndefined($scope.periodId)) {
                DashboardReportingPerformance.get({
                    programId: $scope.programId,
                    periodId: $scope.periodId
                }, function (data) {
                    $scope.reportingPerformance = data.reportingPerformance;
                });
            }

            getDistrictStockSummary();

        }, 200);

    }

    function getDistrictStockSummary(){

        $scope.districtStockStatus = {};

        if(!isUndefined($scope.programId) && !isUndefined($scope.periodId)) {
            DashboardDistrictStockSummary.get({programId: $scope.programId, periodId: $scope.periodId}, function(data){
                var stockSummary = data.stockSummary;

                var groupByProduct = _.groupBy(stockSummary, function(record){return record.productcode;});

                angular.forEach(groupByProduct, function(product){
                    var groupByStock = _.groupBy(product, function(prd){return prd.indicator});
                    $scope.districtStockStatus[product[0].productcode] = groupByStock;
                });

                getFacilityStockSummary();
            });
        }
    }


    function getFacilityStockSummary(){
        $scope.facilityStockStatus = {};

        if(!isUndefined($scope.programId) && !isUndefined($scope.periodId)) {
            DashboardFacilityStockSummary.get({programId: $scope.programId, periodId: $scope.periodId}, function(data){
                var stockStatus = data.facilityStockSummary;

                var groupByProduct = _.groupBy(stockStatus, function(record){return record.productcode;});

                angular.forEach(groupByProduct, function(product){
                    var groupByStock = _.groupBy(product, function(prd){return prd.indicator});
                    $scope.facilityStockStatus[product[0].productcode] = groupByStock;
                });

                getDashboardSummary();

            });
        }
    }

    function getFacilityStatusByCodeAndIndicator(code, indicator){
        if(isUndefined($scope.facilityStockStatus) || isUndefined($scope.facilityStockStatus[code])) return null;
        if(indicator == 'FILLRATE'){

            return $scope.facilityStockStatus[code][indicator];
        }
        return {'values': _.pluck($scope.facilityStockStatus[code][indicator], 'indicator_value').toString(), 'facilities': _.pluck($scope.facilityStockStatus[code][indicator], 'facilityname').toString() } ;
    }


    function getDistrictStatusByCodeAndIndicator(code, indicator){
       // alert('district summary '+JSON.stringify($scope.districtStockStatus))
        if(isUndefined($scope.districtStockStatus) || isUndefined($scope.districtStockStatus[code])) return null;

        return {'values': _.pluck($scope.districtStockStatus[code][indicator], 'indicator_value').toString(), 'districts': _.pluck($scope.districtStockStatus[code][indicator], 'geographiczonename').toString() } ;


    }

    function getDashboardSummary(){

        $scope.productsTrend = [];
        var defaultProducts = 4;
        var count = 0;
        if(!isUndefined($scope.programId) && !isUndefined($scope.periodId)){
            GetProgramPeriodTracerProductsTrend.get({programId: $scope.programId, periodId: $scope.periodId,  limit: 5}, function(data){


                $scope.tracerProducts = data.tracerProducts;
                $scope.sparkOption =  {  fillColor:'#F0F0F0', lineColor:'#ADA8A8',spotColor:'#ADA8A8e', width: '100%', chartRangeMin:'0', height:'20px'};
                $scope.tracerProducts = _.groupBy(data.tracerProducts, function(record){return record.product_code;});

                if(!isUndefined($scope.tracerProducts)){

                    angular.forEach( $scope.tracerProducts , function(productTrend){
                        count = count + 1;
                       // var productSummary = [];
                        $scope.sohValue = _.pluck(productTrend,'quantity_dispensed');
                        $scope.amcValues = _.pluck(productTrend, 'amc');
                        $scope.overStockedValues = _.pluck(productTrend, 'number_of_facilities_overstocked');
                        $scope.adequetlyStockedValues = _.pluck(productTrend, 'number_of_facilities_adquatelystocked');
                        $scope.understockedValues = _.pluck(productTrend, 'number_of_facilities_understocked');
                        $scope.stockedout = _.pluck(productTrend, 'total_facilities_stocked_out');
                        $scope.quantityLost = _.pluck(productTrend, 'total_quantity_lost');
                        $scope.quantityDamaged = _.pluck(productTrend, 'total_quantity_damaged');
                        $scope.quantityExpired = _.pluck(productTrend, 'total_quantity_expired');
                        //alert('periods '+ JSON.stringify(_.pluck(productTrend, 'startdate')))
                        $scope.periods = _.pluck(productTrend, 'startdate').toString();

                        var total_facility_stocked_out = _.findWhere(productTrend, {'order': 1}).total_facilities_stocked_out;
                        var productCode = productTrend[0].product_code;
                        var shortname = productTrend[0].shortname;
                 /*       if(!isUndefined($scope.stockStatus[productCode])){
                            $scope.topDispensed = {'value': _.pluck($scope.stockStatus[productCode]['DISPENSED'], 'indicator_value').toString(), 'districts': _.pluck($scope.stockStatus[productCode]['DISPENSED'], 'geographiczonename').toString() } ;
                            $scope.topAMC = {'value': _.pluck($scope.stockStatus[productCode]['AMC'], 'indicator_value').toString(), 'districts': _.pluck($scope.stockStatus[productCode]['AMC'], 'geographiczonename').toString() } ;
                            $scope.topExpired = {'value': _.pluck($scope.stockStatus[productCode]['EXPIRED'], 'indicator_value').toString(), 'districts': _.pluck($scope.stockStatus[productCode]['EXPIRED'], 'geographiczonename').toString() } ;
                            $scope.topDamaged = {'value': _.pluck($scope.stockStatus[productCode]['DAMAGED'], 'indicator_value').toString(), 'districts': _.pluck($scope.stockStatus[productCode]['DAMAGED'], 'geographiczonename').toString() } ;
                            $scope.topOverstocked = {'value': _.pluck($scope.stockStatus[productCode]['OVERSTOCKED'], 'indicator_value').toString(), 'districts': _.pluck($scope.stockStatus[productCode]['OVERSTOCKED'], 'geographiczonename').toString() } ;
                            $scope.topUnderstocked = {'value': _.pluck($scope.stockStatus[productCode]['UNDERSTOCKED'], 'indicator_value').toString(), 'districts': _.pluck($scope.stockStatus[productCode]['UNDERSTOCKED'], 'geographiczonename').toString() } ;
                            $scope.topAdequatelystocked = {'value': _.pluck($scope.stockStatus[productCode]['ADEQUATELYSTOCKED'], 'indicator_value').toString(), 'districts': _.pluck($scope.stockStatus[productCode]['ADEQUATELYSTOCKED'], 'geographiczonename').toString() } ;
                            $scope.topStockedOut = {'value': _.pluck($scope.stockStatus[productCode]['STOCKEDOUT'], 'indicator_value').toString(), 'districts': _.pluck($scope.stockStatus[productCode]['STOCKEDOUT'], 'geographiczonename').toString() } ;
                            $scope.topLost = {'value': _.pluck($scope.stockStatus[productCode]['LOST'], 'indicator_value').toString(), 'districts': _.pluck($scope.stockStatus[productCode]['LOST'], 'geographiczonename').toString() } ;

                        }else{
                            $scope.topDispensed = $scope.topStockedOut = $scope.topLost = null;
                        }*/
                       // alert('code '+productCode+' '+JSON)
                        $scope.productsTrend.push({'name':productTrend[0].name,'code': productCode,
                            "sohTrend": $scope.sohValue,
                            'consumption':$scope.sohValue.toString(),
                            'amc': $scope.amcValues.toString(),
                            'overStocked': $scope.overStockedValues.toString(),
                            'adequetlyStocked' : $scope.adequetlyStockedValues.toString(),
                            'understocked': $scope.understockedValues.toString(),
                            'stockedout': $scope.stockedout.toString(),
                            'quantityLost': $scope.quantityLost.toString(),
                            'quantityDamaged': $scope.quantityDamaged.toString(),
                            'quantityExpired': $scope.quantityExpired.toString(),
                            'periods': $scope.periods.toString(),

                            'topDispensed':  getDistrictStatusByCodeAndIndicator(productCode, 'DISPENSED'),
                            'topAmc':  getDistrictStatusByCodeAndIndicator(productCode, 'AMC'),
                            'topExpired': getDistrictStatusByCodeAndIndicator(productCode, 'EXPIRED'),
                            'topDamaged':  getDistrictStatusByCodeAndIndicator(productCode, 'DAMAGED'),
                            'topOverstocked':  getDistrictStatusByCodeAndIndicator(productCode, 'OVERSTOCKED'),
                            'topUnderstocked': getDistrictStatusByCodeAndIndicator(productCode, 'UNDERSTOCKED'),
                            'topAdequatelystocked':  getDistrictStatusByCodeAndIndicator(productCode, 'ADEQUATELYSTOCKED'),
                            'topStockedOut':  getDistrictStatusByCodeAndIndicator(productCode, 'STOCKEDOUT'),
                            'topLost':  getDistrictStatusByCodeAndIndicator(productCode, 'LOST'),

                            'topFacilityDispensed':  getFacilityStatusByCodeAndIndicator(productCode, 'DISPENSED'),
                            'topFacilityAmc':  getFacilityStatusByCodeAndIndicator(productCode, 'AMC'),
                            'topFacilityExpired': getFacilityStatusByCodeAndIndicator(productCode, 'EXPIRED'),
                            'topFacilityDamaged':  getFacilityStatusByCodeAndIndicator(productCode, 'DAMAGED'),
                            'topFacilityOverstocked':  getFacilityStatusByCodeAndIndicator(productCode, 'OVERSTOCKED'),
                            'topFacilityUnderstocked': getFacilityStatusByCodeAndIndicator(productCode, 'UNDERSTOCKED'),
                            'topFacilityAdequatelystocked':  getFacilityStatusByCodeAndIndicator(productCode, 'ADEQUATELYSTOCKED'),
                            'topFacilityOnhand':  getFacilityStatusByCodeAndIndicator(productCode, 'ONHAND'),
                            'topFacilityLost':  getFacilityStatusByCodeAndIndicator(productCode, 'LOST'),
                            'topFacilityFillrate': getFacilityStatusByCodeAndIndicator(productCode, 'FILLRATE'),
                            color: $scope.colorify(),
                            'facilityStockedOut': total_facility_stocked_out,
                            'productTrend': productTrend,
                            'consumptionChart': {openPanel:true},
                            'stockingEfficiencyChart': {openPanel:true},
                            'lossesAndAdjustmentChart': {openPanel:true},
                            'selected': count <= defaultProducts? true: false

                        });

                    });
                }

            });
        }

    }


    /**
     * A simple popover placement adjustment based on the current index position.
     *
     * @param index
     * @returns {*}
     */
    $scope.adjustPlacement = function(index){
        if(index < ($scope.productsTrend.length / 2)) {
            return "right|top";
        }
        return "right|bottom";
    };

    /**
     * Function that extracts product trends for this period and last period.
     * It uses $scope.productsTrend to get the product by code and get productTrend property which holds all trends for all periods.
     *
     * @param code
     */
    $scope.getProductSummary = function(code){

        $scope.productSummary = [];
        var product = _.findWhere($scope.productsTrend,{'code': code});
        if(!isUndefined(product)){
            /*The order of trends is in a chronological order and can easily be queried using 'order' property on 'productTrend'*/
            var summary = product.productTrend;
            var this_period_data = _.findWhere(summary, {'order': 1});
            var last_period_data = _.findWhere(summary, {'order': 2});

            $scope.productSummary.push({'label': messageService.get('label.stock.on.hand.at.facility.level'), 'this_period': this_period_data.stock_in_hand_facility, 'last_period': last_period_data.stock_in_hand_facility});
            $scope.productSummary.push({'label': messageService.get('label.stock.on.hand.at.upper.level'), 'this_period': this_period_data.stock_in_hand_upper, 'last_period': last_period_data.stock_in_hand_upper});
            $scope.productSummary.push({'label': messageService.get('label.amc.at.facility.level'), 'this_period': this_period_data.amc, 'last_period': last_period_data.amc});
            $scope.productSummary.push({'label': messageService.get('label.expired.facility.level'), 'this_period': this_period_data.quantity_expired_facility, 'last_period': last_period_data.quantity_expired_facility});
            $scope.productSummary.push({'label': messageService.get('label.expired.upper.level'), 'this_period': this_period_data.quantity_expired_upper, 'last_period': last_period_data.quantity_expired_upper});
            $scope.productSummary.push({'label': messageService.get('label.mos.facility.level'), 'this_period': this_period_data.quantity_expired_upper, 'last_period': last_period_data.quantity_expired_upper});
            $scope.productSummary.push({'label': messageService.get('label.max.facility.level'), 'this_period': this_period_data.quantity_expired_upper, 'last_period': last_period_data.quantity_expired_upper});
            $scope.productSummary.push({'label': messageService.get('label.min.facility.level'), 'this_period': this_period_data.quantity_expired_upper, 'last_period': last_period_data.quantity_expired_upper});

        }
    };

    /**
     * Holds definition of legends for the mini consumption chart
     *
     * @type {*[]}
     */

    $scope.definitions = [
        {'label': messageService.get('label.stock.on.hand.at.facility.level'), 'definition' :  messageService.get('label.stock.on.hand.at.facility.level.definition')},
        {'label': messageService.get('label.stock.on.hand.at.upper.level'), 'definition' :  messageService.get('label.stock.on.hand.at.upper.level.definition')},
        {'label': messageService.get('label.amc.at.facility.level'), 'definition' :  messageService.get('label.amc.at.facility.level.definition')},
        {'label': messageService.get('label.expired.facility.level'), 'definition' :  messageService.get('label.expired.facility.level.definition')},
        {'label': messageService.get('label.expired.upper.level'), 'definition' :  messageService.get('label.expired.upper.level.definition')},
        {'label': messageService.get('label.mos.facility.level'), 'definition' :  messageService.get('label.mos.facility.level.definition')},
        {'label': messageService.get('label.max.facility.level'), 'definition' :  messageService.get('label.max.facility.level.definition')},
        {'label': messageService.get('label.min.facility.level'), 'definition' :  messageService.get('label.min.facility.level.definition')}
    ];

    /**
     * Function used to fetch list of facilities stock out for program, period and product code selected from dashboard page.
     * It uses $scope.programId and $scope.periodId set per the dashboard page. Product code passed to this function when the user clicks on
     * product consumption mini chart.
     *
     * @param code
     */
    $scope.getFacilitiesStockedOut = function(code){
        GetStockOutFacilitiesForProgramPeriodAndProductCode.get({programId: $scope.programId, periodId: $scope.periodId, productCode: code}, function(data){
            $scope.facilities = data.facilities;
        });
    };


}
