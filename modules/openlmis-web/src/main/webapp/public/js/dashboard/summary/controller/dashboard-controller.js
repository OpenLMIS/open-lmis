
/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

function AdminDashboardController($scope,$timeout,$filter,$window, userGeographicZoneList, ReportPrograms, ReportSchedules, ReportPeriods, RequisitionGroupsByProgram,RequisitionGroupsByProgramSchedule, ReportProductsByProgram, OperationYears, ReportPeriodsByScheduleAndYear, FacilitiesByGeographicZoneAndProgramParams, OrderFillRate, ItemFillRate, StockEfficiency,ngTableParams) {

    $scope.filterObject = {};

    $scope.formFilter = {
    };

    $scope.startYears = [];

    initialize();

    function initialize() {
        if(isUndefined($scope.filterObject.geographicZoneId)){
            $scope.filterObject.geographicZoneId = 0;
            if(!isUndefined(userGeographicZoneList) ){
                $scope.filterObject.geographicZoneId = userGeographicZoneList[0] !== null ? userGeographicZoneList[0].id : undefined;
            }
        }
    }

    var itemFillRateColors = [{'minRange': -100, 'maxRange': 0, 'color' : '#E23E3E', 'description' : 'Red color for product with a fill rate <= 0 '},
        {'minRange': 1, 'maxRange': 50, 'color' : '#FEBA50', 'description' : 'Yellow color for product with a fill rate > 0 and <= 50 '},
        {'minRange': 51, 'maxRange': 100, 'color' : '#38AB49', 'description' : 'Green color for product with a fill rate > 50 '}];
    var $scaleColor = '#D7D5D5';
    var defaultBarColor = '#FEBA50';
    var $lineWidth = 5;
    var barColor = defaultBarColor;

   OperationYears.get(function (data) {
        $scope.startYears = data.years;
        $scope.startYears.unshift('-- All Years --');
    });

    ReportPrograms.get(function (data) {
        $scope.programs = data.programs;
        $scope.programs.unshift({'name': '-- Select Programs --'});
    });

    ReportSchedules.get(function(data){
        $scope.schedules = data.schedules;
        $scope.schedules.unshift({'name':'-- Select a Schedule --', 'id':'0'}) ;

    });
    $scope.$watch('formFilter.facilityId', function (selection) {
        if (selection == "All") {
            $scope.filterObject.facilityId = -1;
        } else if (selection !== undefined || selection === "") {
            $scope.filterObject.facilityId = selection;
            $.each($scope.allFacilities, function (item, idx) {
                if (idx.id == selection) {
                    $scope.filterObject.facility = idx.name;
                }
            });

        } else {
            $scope.filterObject.facilityId = 0;
            $scope.filterObject.facility = "";
        }
        if(!isUndefined($scope.filterObject.facilityId) && $scope.filterObject.facilityId !== 0){

            $scope.loadFillRates();
        }
    });


    $scope.filterProductsByProgram = function (){
        if(isUndefined($scope.formFilter.programId)){
           return;
        }
        $scope.filterObject.programId = $scope.formFilter.programId;
        $.each($scope.programs, function (item, idx) {
            if (idx.id == $scope.formFilter.programId) {
                $scope.filterObject.program = idx.name;
            }
        });

        ReportProductsByProgram.get({programId:  $scope.filterObject.programId}, function(data){
            $scope.products = data.productList;
        });

        RequisitionGroupsByProgram.get({program: $scope.filterObject.programId }, function(data){
            $scope.requisitionGroups = data.requisitionGroupList;
            $scope.requisitionGroups.unshift({'name':'-- All Requisition Groups --'});
        });

        $scope.loadFacilities();
        $scope.loadStockingData();

    };

    $scope.processProductsFilter = function (){

        $scope.filterObject.productIdList = $scope.formFilter.productIdList;
        $scope.loadFillRates();
        $scope.loadStockingData();

    };

    $scope.fillRates = {openPanel:true};
    $scope.loadFillRates = function(){
        //For managing visibility of chart rendering container. All most all javascript chart rendering tools needs the container to be visible before the render process starts.
        $scope.showItemFill = false;
        $scope.showOrderFill = false;


       //Facility are required for Order and Item Fill Rates

       if(!isUndefined($scope.filterObject.facilityId) && $scope.filterObject.facilityId !== 0 ){

           if(!isUndefined($scope.filterObject.productIdList)){
               //Item Fill Rate
               ItemFillRate.get({
                   geographicZoneId: $scope.filterObject.geographicZoneId ,
                   periodId: $scope.filterObject.periodId,
                   facilityId: $scope.filterObject.facilityId,
                   programId: $scope.filterObject.programId,
                   productListId: $scope.filterObject.productIdList
               },function (data){
                   if(!isUndefined(data)){//set visibility of item fill chart container to true if there is data to be rendered

                       $scope.showItemFill = true;
                       $scope.fillRates.openPanel = true;
                   }
                   $scope.itemFills = data.itemFillRate;
                   $scope.productItemFillRates = null;
                   if(!isUndefined($scope.itemFills)){
                       $scope.productItemFillRates = [];
                       $.each($scope.itemFills, function (item, idx) {
                           $.each(itemFillRateColors, function(index, item){
                               if(idx.fillRate <= item.maxRange && idx.fillRate >= item.minRange){
                                   barColor = item.color;
                               }
                           });
                           $scope.productItemFillRates.push({'option': {animate:3000, barColor: barColor, scaleColor: $scaleColor, lineWidth: $lineWidth}, 'percent': $filter('number')( idx.fillRate, 0), 'name': idx.product});
                       });
                   }
               });

           }
           //Order Fill Rate
           OrderFillRate.get({geographicZoneId: $scope.filterObject.geographicZoneId,
               periodId: $scope.filterObject.periodId,
               facilityId:$scope.filterObject.facilityId,
               programId: $scope.filterObject.programId},function(data){
               $scope.orderFillChart = null; //reset
               $scope.orderFill = data.orderFillRate;
               var fillRate = [];
               if($scope.orderFill !== undefined ){

                   if(!isUndefined($scope.orderFill.fillRate)){ //set visibility of order fill rate chart container to true
                       $scope.showOrderFill = true;
                       $scope.fillRates.openPanel = true;

                   }
                   fillRate.push([$filter('number')($scope.orderFill.fillRate, 0)]);

                   $timeout(function (){ //wait until the container fully visible

                       $scope.orderFillChart = {
                           'option':{
                               title:'Order Fill Rate',
                               seriesDefaults:{
                                   renderer:$.jqplot.MeterGaugeRenderer,
                                   rendererOptions: {
                                       label: 'Order Sub/App',
                                       labelPosition: 'bottom',
                                       labelHeightAdjust: -5,
                                       min: 0,
                                       max: 100,
                                       intervals:[25, 50, 75, 100],
                                       intervalColors:['#66cc66', '#93b75f', '#E7E658', '#cc6666']
                                   }
                               }
                           },'data': fillRate
                       };

                   },100);

               }
           });
       }
    };

    $scope.loadFacilities = function(){

       // load facilities
        FacilitiesByGeographicZoneAndProgramParams.get({
                geographicZoneId: $scope.filterObject.geographicZoneId ,
                rgroupId: isUndefined($scope.filterObject.rgroupId) ? 0 : $scope.filterObject.rgroupId ,
                programId: isUndefined($scope.filterObject.programId)? 0 : $scope.filterObject.programId ,
                scheduleId: isUndefined($scope.filterObject.scheduleId) ? 0 : $scope.filterObject.scheduleId
            }, function(data){
                $scope.allFacilities = data.facilities;
                $scope.allFacilities.unshift({code:'-- Select a Facility --',id:''});

            }
        );
    };

    $scope.changeSchedule = function(){

        if ($scope.formFilter.scheduleId == "All") {
            $scope.filterObject.scheduleId = -1;
        } else if ($scope.formFilter.scheduleId !== undefined || $scope.formFilter.scheduleId === "") {
            $scope.filterObject.scheduleId = $scope.formFilter.scheduleId;
        } else {
            $scope.filterObject.scheduleId = 0;
        }
        if(!isUndefined($scope.filterObject.scheduleId)){
            ReportPeriods.get({ scheduleId : $scope.filterObject.scheduleId },function(data) {
                $scope.periods = data.periods;
                $scope.periods.unshift({'name':'-- Select a Period --','id':'0'});

            });

            if(!isUndefined($scope.filterObject.programId)){
                RequisitionGroupsByProgramSchedule.get({program: $scope.filterObject.programId, schedule:$scope.filterObject.scheduleId}, function(data){
                    $scope.requisitionGroups = data.requisitionGroupList;
                    $scope.requisitionGroups.unshift({'name':'-- All Requisition Groups --','id':'0'});
                });
            }
        }
        $scope.loadFacilities();
        $scope.loadStockingData();
    };

    $scope.loadFacilitiesByRequisition = function(){
        if ($scope.formFilter.rgroupId == "All") {
            $scope.filterObject.rgroupId = -1;
        } else if ($scope.formFilter.rgroupId !== undefined || $scope.formFilter.rgroupId === "") {
            $scope.filterObject.rgroupId = $scope.formFilter.rgroupId;
            $.each($scope.requisitionGroups, function (item, idx) {
                if (idx.id == $scope.formFilter.rgroupId) {
                    $scope.filterObject.rgroup = idx.name;
                }
            });
        } else {
            $scope.filterObject.rgroupId = 0;
        }

        $scope.loadFacilities();

    };
    $scope.processPeriodFilter = function (){
        if ( $scope.formFilter.periodId == "All") {
            $scope.filterObject.periodId = -1;
        } else if ($scope.formFilter.periodId !== undefined || $scope.formFilter.periodId === "") {
            $scope.filterObject.periodId = $scope.formFilter.periodId;
            $.each($scope.periods, function (item, idx) {
                if (idx.id == $scope.formFilter.periodId) {
                    $scope.filterObject.period = idx.name;
                }
            });

        } else {
            $scope.filterObject.periodId = 0;
        }
        $scope.loadFillRates();
        $scope.loadStockingData();
    };

    $scope.changeScheduleByYear = function (){

        if ($scope.formFilter.year == "-- All Years --") {
            $scope.filterObject.year = -1;
        } else if ($scope.formFilter.year !== undefined || $scope.formFilter.year === "") {
            $scope.filterObject.year = $scope.formFilter.year;

        } else {
            $scope.filterObject.year = 0;
        }

        if($scope.filterObject.year === -1 || $scope.filterObject.year === 0){
            $scope.changeSchedule();

        }else{
            if(!isUndefined($scope.filterObject.scheduleId) && !isUndefined($scope.filterObject.year)){
                ReportPeriodsByScheduleAndYear.get({scheduleId: $scope.filterObject.scheduleId, year: $scope.filterObject.year}, function(data){
                    $scope.periods = data.periods;
                    $scope.periods.unshift({'name':'-- Select a Period --','id':'0'});
                });
            }
            if(!isUndefined($scope.filterObject.scheduleId) && !isUndefined($scope.filterObject.programId)){
                RequisitionGroupsByProgramSchedule.get({program: $scope.filterObject.programId, schedule:$scope.filterObject.scheduleId}, function(data){
                    $scope.requisitionGroups = data.requisitionGroupList;
                    $scope.requisitionGroups.unshift({'name':'-- All Requisition Groups --','id':'0'});
                });
            }
            $scope.loadFacilities();
            $scope.loadStockingData();
        }

    };
    /* Bar Chart */
    var barChartTicks = [[1, "Tab1"], [2, "Tab2"], [3, "Tab3"],[4, "Tab4"],[5, "Tab5"]];

    function GenerateSeries(added){
        var data = [];
        var start = 0 + added;
        var end = 100 + added;

        for(i=1;i<=5;i++){
            var d = Math.floor(Math.random() * (end - start + 1) + start);
            data.push([i, d]);
            start++;
            end++;
        }

        return data;
    }

    $scope.barChartData =  [{ label: "Random Tabs Data Size", data:  GenerateSeries(0), color: "#5482FF" }];
    $scope.barChartOption = {
        series: {
            bars: {show: true,
                align: "center",
                lineWidth:0,
                fill: 0.6,
                barWidth: 0.3
            }
        },
        xaxis: {
            axisLabel: "Sample tabs",
            axisLabelUseCanvas: false,
            ticks: barChartTicks
        },
        yaxis: {
            axisLabel: "Data Size",
            axisLabelUseCanvas: false,
            tickFormatter: function (v, axis) {
                return v + "kb";
            }
        },
        legend: {
            container:$("#barChartLegend"),
            noColumns: 0
        },
        grid:{
            clickable:true,
            hoverable: true,
            //autoHighlight: true,
            borderWidth: 1,
            borderColor: "#d6d6d6",
            minBorderMargin: 20,
            labelMargin: 10,
            backgroundColor: {
                colors: ["#FFF", "#CCC"]
            }
        },tooltip: true,
        tooltipOpts: {
            content: "%s of %x.0 is %y",
            shifts: {
                x: 20,
                y: 0
            },
            defaultTheme: false
        }
    };

    $("#afloat1").bind("plotclick", function (event, pos, item) {

        if(item) {
            var barIndex =  item.datapoint[0];
            if (item.datapoint[0]) {
                $("#afloat1").css('cursor','crosshair');
            } else {
                $("#afloat1").css('cursor','auto');
            }
            if(++barIndex <=4){
                var tabId ='dashboard-tab-'+ barIndex;

                createTab(tabId);
            }
        }
    });

    function flotChartHoverCursorHandler(event,pos,item){
        //alert('hover')
        if(!isUndefined(item)) {
            if (item.datapoint[0]) {
                $(event.target).css('cursor','pointer');
            } else {
                $(event.target).css('cursor','auto');
            }
        }else{
            $(event.target).css('cursor','auto');
        }

    }

    $("#afloat1").bind("plothover", function (event, pos, item) {
        flotChartHoverCursorHandler(event, pos, item);
    });
    /* End Bar Chart */

    /* Pie Chart */
    $scope.districtReporting = {};
    $scope.districtReporting.openPanel = true;

    var pieChartSeries = 3;
    var pieChartColors = ["#05BC57","#CC0505", "#FFFF05"];
    var pieChartLabels = ["Reported on time","Did not report","Reported late"];
    $timeout(function(){

        $scope.pieChartData = [];
        var series = 3;
        var colors = ["#05BC57","#CC0505", "#FFFF05"];
        var labels = ["Reported on time","Did not report","Reported late"];

        for (var i = 0; i < series; i++) {
            $scope.pieChartData[i] = {
                label: labels[i],
                data: Math.floor(Math.random() * 100) + 1,
                color: colors[i]
            };
        }
        $scope.pieChartOption = {
            series: {
                pie: {
                    show: true,
                    radius: 1,

                    label: {
                        show: true,
                        radius: 2 / 3,
                        formatter: function (label, series) {
                            return '<div style="font-size:8pt;text-align:center;padding:2px;color:black;">' + Math.round(series.percent) + '%</div>';
                        },
                        threshold: 0.1
                    }
                }
            },
            legend: {
                container:$("#districtReportLegend"),
                noColumns: 0,
                labelBoxBorderColor: "none"
            },
            grid:{
                hoverable: true,
                borderWidth: 1,
                borderColor: "#d6d6d6",
                //minBorderMargin: 20,
                //labelMargin: 10,
                backgroundColor: {
                    colors: ["#FFF", "#CCC"]
                }
            },
            tooltip: true,
            tooltipOpts: {
                content: "%p.0%, %s",
                shifts: {
                    x: 20,
                    y: 0
                },
                defaultTheme: false
            }
        };

    },100);


    /* End Pie Chart */

   /* Calendar  */

    var date = new Date();
    var d = date.getDate();
    var m = date.getMonth();
    var y = date.getFullYear();

    /* event source that contains custom events on the scope */
    $scope.events = [
        {title: 'All Day Event',start: new Date(y, m, 1)},
        {title: 'Long Event',start: new Date(y, m, d - 5),end: new Date(y, m, d - 2)},
        {id: 999,title: 'Repeating Event',start: new Date(y, m, d - 3, 16, 0),allDay: false},
        {id: 999,title: 'Repeating Event',start: new Date(y, m, d + 4, 16, 0),allDay: false},
        {title: 'Birthday Party',start: new Date(y, m, d + 1, 19, 0),end: new Date(y, m, d + 1, 22, 30),allDay: false},
        {title: 'Click for Google',start: new Date(y, m, 28),end: new Date(y, m, 29),url: 'http://google.com/'}
    ];

    /* alert on eventClick */
    $scope.alertOnEventClick = function( event, allDay, jsEvent, view ){
        alert(event.title + 'was clicked');

    };
    /* alert on Drop */
    $scope.alertOnDrop = function(event, dayDelta, minuteDelta, allDay, revertFunc, jsEvent, ui, view){
        alert('Event Droped to make dayDelta ' + dayDelta);

    };
    /* alert on Resize */
    $scope.alertOnResize = function(event, dayDelta, minuteDelta, revertFunc, jsEvent, ui, view ){
        alert('Event Resized to make dayDelta ' + minuteDelta);
    };

    /* add custom event*/
    $scope.addEvent = function() {
        $scope.events.push({
            title: 'eLMIS V2 kickoff meeting',
            start: new Date(y, m, 28),
            end: new Date(y, m, 29)
        });
    };
    /* remove event */
    $scope.remove = function(index) {
        $scope.events.splice(index,1);
    };
    /* Change View */
    $scope.changeView = function(view,calendar) {
        calendar.fullCalendar('changeView',view);
    };

    /* config object */
    $scope.uiConfig = {
        calendar:{
            height: 450,
            editable: true,
            header:{
                left: 'title',
                center: '',
                right: 'today prev,next'
            },
            eventClick: $scope.alertOnEventClick,
            eventDrop: $scope.alertOnDrop,
            eventResize: $scope.alertOnResize
        }
    };


    /* event sources array*/
    $scope.eventSources = [$scope.events];

    /* End Calendar  */
   // $scope.itemFillRates = [55,45,-60];
    $scope.itemFillRates = [{option:{animate:3000, barColor:'#FEBA50', scaleColor:'#D7D5D5', lineWidth: 5},percent:55},{option: {animate:2500, barColor:'#E23E3E', scaleColor:'#D7D5D5', lineWidth: 5 }, percent:45}];

    /* Easy pie chart */
    $scaleColor = '#D7D5D5';
    $lineWidth = 5;
    $scope.option1  = {animate:3000, barColor:'#FEBA50', scaleColor:$scaleColor, lineWidth: $lineWidth};
    $scope.percent1 = 55;
    $scope.option2  = {animate:2500, barColor:'#E23E3E', scaleColor:$scaleColor, lineWidth: $lineWidth };
    $scope.percent2 = -46;
    $scope.option3  = {animate:2000, barColor:'#38AB49', scaleColor:$scaleColor, lineWidth: $lineWidth};
    $scope.percent3 = 85;
    $scope.option4  = { animate:1500, barColor:'#FFB848', scaleColor:$scaleColor, lineWidth: $lineWidth};
    $scope.percent4 = 55;

    /* End of Easy pie chart */


    $scope.loadStockingData = function(){

        if(!isUndefined($scope.filterObject.productIdList)){
            StockEfficiency.get({
                geographicZoneId: $scope.filterObject.geographicZoneId,
                periodId: $scope.filterObject.periodId,
                programId: $scope.filterObject.programId,
                productListId: $scope.filterObject.productIdList
            },function (data){
                var stockingData =  data.stocking;
                adjustStockingEfficiencyDataForChart(stockingData);
              });
        }else{
            $scope.resetStockingChartData();
        }
    };

    $scope.resetStockingChartData = function(){
        $scope.multiBarsRenderedData = undefined;
        $scope.multiBarsData = undefined;
        $scope.multipleBarsOption = undefined;

        $scope.stocking.openPanel =  !$scope.stocking.openPanel;

    };

    var adjustStockingEfficiencyDataForChart = function(stockingData){
        if(isUndefined(stockingData) || stockingData.length === 0){
            $scope.resetStockingChartData();
            return;
        }
        $scope.multiBarsRenderedData = {
            products : _.pairs(_.object(_.range(stockingData.length), _.pluck(stockingData,'productId'))),
            filterParams : _.pick(stockingData[0],'geographicZoneId','programId', 'periodId')
        };
        var multiBarsColors = ["#4F81BD","#C0504D","#9BBB59","#8064A2"];
        var adequatelyStockedSeries =  _.pairs(_.object(_.range(stockingData.length), _.map(_.pluck(stockingData,'adequatelyStocked'),function(stat){ return _.isNull(stat) ? 0 : stat;})));
        var stockedOutSeries =  _.pairs(_.object(_.range(stockingData.length), _.map(_.pluck(stockingData,'stockedOut'),function(stat){ return _.isNull(stat) ? 0 : stat;})));
        var overstockedSeries =  _.pairs(_.object(_.range(stockingData.length), _.map(_.pluck(stockingData,'overStocked'),function(stat){ return _.isNull(stat) ? 0 : stat;})));
        var understockedSeries =  _.pairs(_.object(_.range(stockingData.length), _.map(_.pluck(stockingData,'understocked'),function(stat){ return _.isNull(stat) ? 0 : stat;})));
        var seriesLabel = ["Stocked out","Understocked","Overstocked","Adequately Stocked"];
        var dataSeries = [stockedOutSeries,understockedSeries,overstockedSeries,adequatelyStockedSeries];

        $scope.stocking.openPanel = true;

        $scope.multiBarsData = _.map(dataSeries, function(series, key){
            return {
            label: seriesLabel[key],
            data: series,
            bars: {
                order: key + 1
            },
            color: multiBarsColors[key]
        }; });

        var multiBarsTicks = _.pairs(_.object(_.range(stockingData.length), _.pluck(stockingData,'product')));
        $scope.multipleBarsOption = generateMultipleBarsOption(multiBarsTicks);

        //bind event
        bindChartEvent("#stocking-efficiency","plotclick",$scope.stockBarClickHandler);
        bindChartEvent("#stocking-efficiency","plothover", flotChartHoverCursorHandler);
    };

    function generateMultipleBarsOption(ticksLabel){
        return {
            series: {
                bars: {
                    show: true,
                    fill: 0.5,
                    lineWidth: 0,
                    barWidth: 0.2
                }
            },
            xaxis: {
                //tickLength: 0, // hide gridlines
                axisLabel: 'Product',
                axisLabelUseCanvas: false,
                ticks: ticksLabel

            } ,
            yaxis: {
               // min:0,
                tickSize:1,
                axisLabel: '# of Facilities',
                axisLabelUseCanvas: false//,
                /*tickFormatter: function (v, axis) {
                    return v ;
                }*/
            },
            grid: {
                hoverable: true,
                clickable: true,
                borderWidth: 1,
                borderColor: "#d6d6d6",
                backgroundColor: {
                    colors: ["#FFF", "#CCC"]
                }
            },
            legend: {
                container:$("#multiBarsLegend"),
                noColumns: 0,
                labelBoxBorderColor: "none"
            },
            tooltip: true,
            tooltipOpts: {
                content: getTooltip,
                shifts: {
                    x: 10,
                    y: 20
                },
                defaultTheme: true
            }
        };

    }
    $scope.multipleBarsOption = {};
    $scope.multiBarsData =[];

     function getTooltip(label, xval, yval, flotItem){

         return flotItem.series.xaxis.ticks[xval].label+' '+yval+' '+'facilities'+' ' +label;
     }
    $scope.stockBarClickHandler = function (event, pos, item){

        $scope.$parent.userGeographicZones = userGeographicZoneList;
        var stockData = {};
        if(item && !isUndefined(item.dataIndex)){
            var productIndex = item.dataIndex;
            if($scope.multiBarsRenderedData){
                $scope.multiBarsRenderedData.filterParams.productId = $scope.multiBarsRenderedData.products[productIndex][1];
                stockData = $scope.multiBarsRenderedData.filterParams;
                stockData.year = $scope.filterObject.year;
                stockData.scheduleId = $scope.filterObject.scheduleId;
                stockData.status = item.seriesIndex;

                var viewStockPath;
                viewStockPath = '/public/pages/dashboard/stock-efficiency/index.html#view-stock-detail/'+
                    stockData.geographicZoneId  + '/' + stockData.programId+'/'+stockData.periodId+'/'+
                    stockData.productId+"?year="+stockData.year+"&scheduleId="+stockData.scheduleId+"&status="+stockData.status;
                $window.location = viewStockPath;
                $scope.$apply();

            }
        }
    };

    function bindChartEvent(elementSelector, eventType, callback){
        $(elementSelector).bind(eventType, callback);
    }

    /* End Custom Bar Chart */

    /* Bootstrap Dynamic Tab Utility  */
    function createTab(tabId){
        var tabNum = tabId.substr(tabId.length - 1);
        var contentId = tabId +'-'+ tabNum;

        if($('#'+tabId).length === 0){ //tab does not exist
            $('.nav-tabs').prepend('<li id="'+tabId+'"><a href="#' + contentId + '" data-toggle="tab"'+'><button class="close closeTab" type="button" >×</button>Tab '+tabNum +'</a></li>');
            showTab(tabId);

            registerCloseEvent();

        }else{
            showTab(tabId);
        }
    }

    function registerCloseEvent() {
        $('#dashboard-tabs').on('click', ' li a .close', function(e) {
            e.preventDefault();
            $(this).parents('li').remove('li');
            $('#dashboard-tabs a:first').tab('show');
        });
    }
    function showTab(tabId) {

        $('#dashboard-tabs #' + tabId + ' a').tab('show');
    }

    $scope.oneAtATime = true;

    $scope.stocking = {};
    $scope.stocking.openPanel = false;
    //$scope.$watch()
    $scope.groups = [
        {
            title: "Dynamic Group Header - 1",
            content: "Dynamic Group Body - 1"
        },
        {
            title: "Dynamic Group Header - 2",
            content: "Dynamic Group Body - 2"
        }
    ];

    $scope.items = ['Item 1', 'Item 2', 'Item 3'];

    $scope.addItem = function() {
        var newItemNo = $scope.items.length + 1;
        $scope.items.push('Item ' + newItemNo);
    };

}
