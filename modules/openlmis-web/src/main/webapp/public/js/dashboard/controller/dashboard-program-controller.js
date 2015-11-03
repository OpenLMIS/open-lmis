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

function DashboardProgramController($scope,$routeParams,$timeout,$filter,messageService, dashboardMenuServiceNew, UserSupervisedActivePrograms, GetLastPeriods, GetProgramPeriodTracerProductsTrend, GetStockOutFacilitiesForProgramPeriodAndProductCode, DashboardReportingPerformance, DashboardDistrictStockSummary, DashboardFacilityStockSummary, SettingsByKey) {
    var dashboardMenuService = dashboardMenuServiceNew;

    SettingsByKey.get({key: 'DASHBOARD_SLIDES_TRANSITION_INTERVAL_MILLISECOND'}, function(data){
        $scope.defaultSlideTransitionInterval = data.settings.value;
        $scope.consumptionSlideInterval = $scope.stockSlideInterval = $scope.lossesSlideInterval = $scope.defaultSlideTransitionInterval;


        var carousel = function(id){
            return {id: id,
                interval: $scope.defaultSlideTransitionInterval,
                isPlaying:  function(){ return this.interval >= 0;},
                play: function(){ this.interval = $scope.defaultSlideTransitionInterval; this.isPlaying = true;},
                pause: function(){this.interval = -1; this.isPlaying = false; }};
        };

        $scope.carousels = [carousel('trend'), carousel('district'), carousel('facility')];
    });


    $scope.setInterval = function(carouselId){
        var cr = _.findWhere($scope.carousels, {id: carouselId});
        if(!isUndefined(cr)){
            return cr.interval;
        }
        return -1;
    };


    var  colors = ["bg-green", "bg-red","bg-blue"];
    /**
     * Returns Random color
     * @returns {string}
     */
    $scope.colorify = function(){
        var index = Math.floor(Math.random() * (2 - 0 + 1)) + 0;
        return colors[index];

    };

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

        if(fillRate >= 0 && fillRate <=50){
            barColor = barColors[0];
        }else if(fillRate > 50 && fillRate <= 70){
            barColor = barColors[1];
        }else{
            barColor = barColors[2];
        }

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
            getDashboardSummary();
        }, 200);

    }

    function getDistrictStockSummary(){
        //alert('called')
        $scope.districtStockStatus = {};
        $scope.productsDistrict = [];
        var defaultProducts = 4;
        var count = 0;

        if(!isUndefined($scope.programId) && !isUndefined($scope.periodId)) {
            DashboardDistrictStockSummary.get({programId: $scope.programId, periodId: $scope.periodId}, function(data){
                var stockSummary = data.stockSummary;

                var groupByProduct = _.groupBy(stockSummary, function(record){return record.productcode;});

                angular.forEach(groupByProduct, function(product){
                    var groupByStock = _.groupBy(product, function(prd){return prd.indicator;});
                    $scope.districtStockStatus[product[0].productcode] = groupByStock;
                });
                if(!isUndefined($scope.tracerProducts)){
                    angular.forEach( $scope.tracerProducts , function(productDistrict){
                        count = count + 1;
                        var productCode = productDistrict[0].product_code;
                        $scope.productsDistrict.push({'name':productDistrict[0].name,'code': productCode,
                            'topDispensed':  getDistrictStatusByCodeAndIndicator(productCode, 'DISPENSED'),
                            'topAmc':  getDistrictStatusByCodeAndIndicator(productCode, 'AMC'),
                            'topExpired': getDistrictStatusByCodeAndIndicator(productCode, 'EXPIRED'),
                            'topDamaged':  getDistrictStatusByCodeAndIndicator(productCode, 'DAMAGED'),
                            'topOverstocked':  getDistrictStatusByCodeAndIndicator(productCode, 'OVERSTOCKED'),
                            'topUnderstocked': getDistrictStatusByCodeAndIndicator(productCode, 'UNDERSTOCKED'),
                            'topAdequatelystocked':  getDistrictStatusByCodeAndIndicator(productCode, 'ADEQUATELYSTOCKED'),
                            'topStockedOut':  getDistrictStatusByCodeAndIndicator(productCode, 'STOCKEDOUT'),
                            'topLost':  getDistrictStatusByCodeAndIndicator(productCode, 'LOST'),
                            'periods': $scope.periods.toString(),
                            color: $scope.colorify(),
                            'productDistrict': productDistrict,
                            'consumptionChart': {openPanel:true},
                            'utilizationChart': {openPanel:true},
                            'stockingEfficiencyChart': {openPanel:true},
                            'lossesAndAdjustmentChart': {openPanel:true},
                            'selected': count <= defaultProducts? true: false

                        });
                        //alert(JSON.stringify($scope.productsDistrict));
                    });
                    getFacilityStockSummary();
                }//
            });
        }

    }


    function getFacilityStockSummary(){
        $scope.facilityStockStatus = {};
        $scope.productsFacility = [];
        var defaultProducts = 4;
        var count = 0;

        if(!isUndefined($scope.programId) && !isUndefined($scope.periodId)) {
            DashboardFacilityStockSummary.get({programId: $scope.programId, periodId: $scope.periodId}, function(data){
                var stockSummary = data.stockSummary;

                var groupByProduct = _.groupBy(stockSummary, function(record){return record.productcode;});

                angular.forEach(groupByProduct, function(product){
                    var groupByStock = _.groupBy(product, function(prd){return prd.indicator;});
                    $scope.facilityStockStatus[product[0].productcode] = groupByStock;
                });

                if(!isUndefined($scope.tracerProducts)){
                    angular.forEach( $scope.tracerProducts , function(productFacility){
                        count = count + 1;
                        var productCode = productFacility[0].product_code;
                        $scope.productsFacility.push({'name':productFacility[0].name,'code': productCode,
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
                            'productFacility': productFacility,
                            'consumptionChart': {openPanel:true},
                            'utilizationChart': {openPanel:true},
                            'stockingEfficiencyChart': {openPanel:true},
                            'lossesAndAdjustmentChart': {openPanel:true},
                            'selected': count <= defaultProducts? true: false
                        });
                    });
                }//
            });
        }
    }


    $scope.formatValue = function (value, ratio, id) {
        return $filter('number')(value);
    };

    function getFacilityStatusByCodeAndIndicator(code, indicator){
        if(isUndefined($scope.facilityStockStatus) || isUndefined($scope.facilityStockStatus[code])) return null;
        if(indicator == 'FILLRATE'){

            return $scope.facilityStockStatus[code][indicator];
        }
        return {'values': _.pluck($scope.facilityStockStatus[code][indicator], 'indicator_value').toString(), 'facilities': _.pluck($scope.facilityStockStatus[code][indicator], 'facilityname').toString() } ;
    }


    function getDistrictStatusByCodeAndIndicator(code, indicator){
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
                        $scope.beginningBalanceValue = _.pluck(productTrend,'beginning_balance');
                        $scope.quantityReceivedValue = _.pluck(productTrend,'quantity_received');
                        $scope.quantityDispensedValue = _.pluck(productTrend,'quantity_dispensed');
                        $scope.amcValues = _.pluck(productTrend, 'amc');
                        $scope.sohValue = _.pluck(productTrend,'stock_in_hand_facility');
                        $scope.overStockedValues = _.pluck(productTrend, 'number_of_facilities_overstocked');
                        $scope.adequetlyStockedValues = _.pluck(productTrend, 'number_of_facilities_adquatelystocked');
                        $scope.understockedValues = _.pluck(productTrend, 'number_of_facilities_understocked');
                        $scope.stockedout = _.pluck(productTrend, 'total_facilities_stocked_out');
                        $scope.quantityLost = _.pluck(productTrend, 'total_quantity_lost');
                        $scope.quantityDamaged = _.pluck(productTrend, 'total_quantity_damaged');
                        $scope.quantityExpired = _.pluck(productTrend, 'total_quantity_expired');
                        $scope.periods = _.pluck(productTrend, 'period_name').toString();

                        var total_facility_stocked_out = _.findWhere(productTrend, {'order': 1}).total_facilities_stocked_out;
                        var productCode = productTrend[0].product_code;

                        $scope.productsTrend.push({'name':productTrend[0].name,'code': productCode,
                            "sohTrend": $scope.sohValue,
                            'beginningBalance':$scope.beginningBalanceValue.toString(),
                            'received':$scope.quantityReceivedValue.toString(),
                            'consumption':$scope.quantityDispensedValue.toString(),
                            'amc': $scope.amcValues.toString(),
                            'soh': $scope.sohValue.toString(),
                            'overStocked': $scope.overStockedValues.toString(),
                            'adequetlyStocked' : $scope.adequetlyStockedValues.toString(),
                            'understocked': $scope.understockedValues.toString(),
                            'stockedout': $scope.stockedout.toString(),
                            'quantityLost': $scope.quantityLost.toString(),
                            'quantityDamaged': $scope.quantityDamaged.toString(),
                            'quantityExpired': $scope.quantityExpired.toString(),
                            'periods': $scope.periods.toString(),
                            color: $scope.colorify(),
                            'facilityStockedOut': total_facility_stocked_out,
                            'productTrend': productTrend,
                            'consumptionChart': {openPanel:true},
                            'utilizationChart': {openPanel:true},
                            'stockingEfficiencyChart': {openPanel:true},
                            'lossesAndAdjustmentChart': {openPanel:true},
                            'selected': count <= defaultProducts? true: false
                        });
                    });
                }
                getDistrictStockSummary();
            });
        }

    }

    $scope.isProductSelected = function(code){

        var product = _.findWhere($scope.productSelections, {'code': code});
          return isUndefined(product) ? false : product.selected;
    };


    $scope.setProductSelected = function(code,value){
        var productDistrict = _.findWhere($scope.productsDistrict, {'code': code});
        productDistrict.selected = value;

        var productFacility = _.findWhere($scope.productsFacility, {'code': code});
        productFacility.selected = value;

        //alert(JSON.stringify(code + "***" + value + "***" + product.selected));
    };


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
