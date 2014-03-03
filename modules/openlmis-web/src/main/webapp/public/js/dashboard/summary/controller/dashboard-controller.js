
/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

function AdminDashboardController($scope,$timeout,userFacilityData, ReportPrograms, ReportSchedules, ReportPeriods, RequisitionGroupsByProgram,RequisitionGroupsByProgramSchedule, ReportProductsByProgram, OperationYears, ReportPeriodsByScheduleAndYear, FacilitiesByGeographicZoneAndProgramParams, OrderFillRate, ItemFillRate, ngTableParams) {

    $scope.filterObject = {};

    $scope.formFilter = {
    };

    $scope.startYears = [];

    initialize();

    function initialize() {
        if(isUndefined($scope.filterObject.geographicZoneId)){
            $scope.filterObject.geographicZoneId = 0;
            var userFacility = userFacilityData.facilityList[0];
            if (userFacility) {
                $scope.filterObject.geographicZoneId = userFacility.geographicZone.id;
            }
        }
    }

    $scope.productSelectOption = {maximumSelectionSize : 4};

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

    $scope.$watch('fillRate.facilityId', function (selection) {
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

    };

    $scope.processProductsFilter = function (){

        $scope.filterObject.productIdList = $scope.formFilter.productIdList;
        $scope.loadFillRates();

    };


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
                           $scope.productItemFillRates.push({'option': {animate:3000, barColor: barColor, scaleColor: $scaleColor, lineWidth: $lineWidth}, 'percent': idx.fillRate, 'name': idx.product});
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

                   }
                   fillRate.push([$scope.orderFill.fillRate]);

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

    $scope.ChangeSchedule = function(){

        if ($scope.formFilter.scheduleId == "All") {
            $scope.filterObject.scheduleId = -1;
        } else if ($scope.formFilter.scheduleId !== undefined || $scope.formFilter.scheduleId === "") {
            $scope.filterObject.scheduleId = $scope.formFilter.scheduleId;
            $.each($scope.schedules , function (item, idx) {
                if (idx.id == $scope.formFilter.scheduleId) {
                    $scope.filterObject.schedule = idx.name;
                }
            });

        } else {
            $scope.filterObject.scheduleId = 0;
        }

        ReportPeriods.get({ scheduleId : $scope.filterObject.scheduleId },function(data) {
            $scope.periods = data.periods;
            $scope.periods.unshift({'name':'-- Select a Period --','id':'0'});

        });

        RequisitionGroupsByProgramSchedule.get({program: $scope.filterObject.programId, schedule:$scope.filterObject.scheduleId}, function(data){
            $scope.requisitionGroups = data.requisitionGroupList;
            $scope.requisitionGroups.unshift({'name':'-- All Requisition Groups --','id':'0'});
        });

        $scope.loadFacilities();
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
            $scope.ChangeSchedule();

        }else{

            ReportPeriodsByScheduleAndYear.get({scheduleId: $scope.filterObject.scheduleId, year: $scope.filterObject.year}, function(data){
                $scope.periods = data.periods;
                $scope.periods.unshift({'name':'-- Select a Period --','id':'0'});
            });

            RequisitionGroupsByProgramSchedule.get({program: $scope.filterObject.programId, schedule:$scope.filterObject.scheduleId}, function(data){
                $scope.requisitionGroups = data.requisitionGroupList;
                $scope.requisitionGroups.unshift({'name':'-- All Requisition Groups --','id':'0'});
            });

            $scope.loadFacilities();
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
            bars: {show: true}
        },
        bars: {
            align: "center",
            fillColor:  "#5482FF",
            barWidth: 0.3
        },
        xaxis: {
            axisLabel: "Sample tabs",
            axisLabelUseCanvas: true,
            axisLabelFontSizePixels: 12,
            axisLabelFontFamily: 'Verdana, Arial',
            axisLabelPadding: 10,
            ticks: barChartTicks
        },
        yaxis: {
            axisLabel: "Data Size",
            axisLabelUseCanvas: true,
            axisLabelFontSizePixels: 12,
            axisLabelFontFamily: 'Verdana, Arial',
            axisLabelPadding: 3,
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
            borderWidth: 2,
            backgroundColor: { colors: ["#ffffff", "#EDF5FF"] }
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
            if(++barIndex <=4){
                var tabId ='dashboard-tab-'+ barIndex;

                createTab(tabId);
            }
        }
    });

    /* End Bar Chart */

    /* Pie Chart */

    var pieChartSeries = 3;
    var pieChartColors = ["#05BC57","#CC0505", "#FFFF05"];
    var pieChartLabels = ["Reported on time","Did not report","Reported late"];

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
            show: true
        },
        grid:{
            hoverable: true
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

    /* Custom Bar Chart */
    var d1_1 = [[0, 95],[1, 70],[2, 94]];

    var d1_2 = [[0, 80],[1, 60],[2, 30]];

    var d1_3 = [[0, 65],[1, 40],[2, 45]];

    var multiBarsTicks = [[0, "District A"], [1, "District B"], [2, "District C"]];

    $scope.multipleBarsOption = {
        series: {
            shadowSize: 1
        },
        bars: {
            show: true,
            barWidth: 0.2
        },
        xaxis: {
            tickLength: 0, // hide gridlines
            axisLabel: 'District',
            axisLabelUseCanvas: true,
            axisLabelFontSizePixels: 12,
            ticks: multiBarsTicks

        } ,
        yaxis: {
            min:0,
            max:100,
            axisLabel: 'Value',
            axisLabelUseCanvas: true,
            axisLabelFontSizePixels: 12,
            axisLabelFontFamily: 'Verdana, Arial',
            axisLabelPadding: 3,

            tickFormatter: function (v, axis) {
                return v + "%";
            }
        },
        grid: {
            hoverable: true,
            clickable: false,
            borderWidth: 1
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
            defaultTheme: false
        }
    };
    $scope.multiBarsData = [
        {
            label: "Reported on time",
            data: d1_1,
            bars: {
                order: 1,
                fillColor:  "#05BC57"

            },
            color: "#05BC57"
        },
        {
            label: "Report late",
            data: d1_2,
            bars: {

                order: 2,
                fillColor:  "#FFFF05"
            },
            color: "#FFFF05"
        },
        {
            label: "Did not report",
            data: d1_3,
            bars: {

                order: 3,
                fillColor:  "#CC0505"
            },
            color: "#CC0505"
        }
    ];
     function getTooltip(label, xval, yval, flotItem){
         return flotItem.series.xaxis.ticks[xval].label+' '+label+' '+' '+yval+'%';
     }
    /* End Custom Bar Chart */


   /* Gauge Chart */

   /* $scope.gaugeChartData = [[ 66.666664]];

    $scope.gaugeChartOption = {
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
    };*//*





    /* End Gauge Chart * /










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

   /* $(function () {
        $scope.paramsChanged($scope.tableParams);

    });*/
}
/*AdminDashboardController.resolve = {
    userFacilityData :function ($q, $timeout, UserFacilityList) {
        var deferred = $q.defer();
        $timeout(function () {
            UserFacilityList.get({}, function (data) {
                deferred.resolve(data);
            }, {});
        }, 100);
        return deferred.promise;
    }
};*/
