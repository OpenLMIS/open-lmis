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
function VaccineReportPOCReportController($scope, GeoZoneFacilityTrees){
    $scope.vaccineData = [
        {name:'Vaccine A'},{name:'Vaccine B'},{name: 'Vaccine C'}
    ];
    $scope.demographicData = [{facilityId: 138,
                                data: [
                                    {name:'Total catchment population', value: 15581227},
                                    {name:'Surviving infants 0-11 months; annual target', value: 624548},
                                    {name:'Surviving infants 0-11 months; monthly target', value: 52046},
                                    {name:'Total population of pregnant women', value: 779061},
                                    {name:'Total new born/month', value: 64922}
                                ]},
                                {facilityId: 148,
                                    data: [
                                        {name:'Total catchment population', value: 10481336},
                                        {name:'Surviving infants 0-11 months; annual target', value: 513437},
                                        {name:'Surviving infants 0-11 months; monthly target', value: 42786},
                                        {name:'Total population of pregnant women', value: 668052},
                                        {name:'Total new born/month', value: 55671}
                                    ]}
    ];
    $scope.completeness = [{facilityId: 138,
        data: [
            {name:'No. of districts', value: 60},
            {name:'No. of vaccination units', value: null},
            {name:'No. of reports received during the month', value: 60},
            {name:'No. of reports received on time during the month', value: 60},
            {name:'No. of Outreach/Mobile sessions during the month', value: null}
        ]},
        {facilityId: 148,
            data: [
                {name:'No. of districts', value: 75},
                {name:'No. of vaccination units', value: null},
                {name:'No. of reports received during the month', value: 75},
                {name:'No. of reports received on time during the month', value: 75},
                {name:'No. of Outreach/Mobile sessions during the month', value: null}
            ]}
    ];
    $scope.randomData = GenerateSeries(0);
    $scope.amcValue = $scope.sohValue = $scope.randomData[11][1];
    $scope.getDemographicData = function(){
        $scope.demogData = _.findWhere($scope.demographicData,{facilityId:138});
        $scope.completnessData = _.findWhere($scope.completeness,{facilityId:138});
        $scope.coverageData = _.findWhere($scope.coverages,{facilityId:138});
        $scope.wasteData = _.findWhere($scope.wasteManagement,{facilityId:138});
        $scope.coldChainData = _.findWhere($scope.coldChain,{facilityId:138});
        $scope.coldChainNationalData = _.findWhere($scope.coldChainNational,{facilityId:138});
        $scope.safeInjectionData = _.findWhere($scope.safeInjection,{facilityId:138});
        $scope.coveragesPregnantData = _.findWhere($scope.coveragesPregnant,{facilityId:138});
        $scope.coveragesAdolescentData = _.findWhere($scope.coveragesAdolescent,{facilityId:138});
        $scope.stocksData = _.findWhere($scope.stocks,{facilityId:138});

    };

    $scope.$watch('facilityId',function(){
       $scope.getDemographicData();
    });
    $scope.coverages =  [{facilityId: 138,
        data: [
            {doses:'BCG', fixPost:  61489, outReach:915, other: null, coverageMonthly:96, coverageCumulative: 97, dropout:null},
            {doses:'OPV-0', fixPost:  32002, outReach:null, other: null, coverageMonthly:49, coverageCumulative: 50, dropout:null},
            {doses:'OPV-1', fixPost:  52817, outReach:null, other: null, coverageMonthly:87, coverageCumulative: 80, dropout:17},
            {doses:'OPV-2', fixPost:  45088, outReach:null, other: null, coverageMonthly:96, coverageCumulative: 97, dropout:null},
            {doses:'OPV-3', fixPost:  42203, outReach:null, other: null, coverageMonthly:81, coverageCumulative: 85, dropout:12},
            {doses:'BCG', fixPost:  61489, outReach:915, other: null, coverageMonthly:96, coverageCumulative: 97, dropout:null}

        ]},
        {facilityId: 148,
            data: [
                {doses:'BCG', fixPost:  61489, outReach:915, other: null, coverageMonthly:96, coverageCumulative: 97, dropout:null},
                {doses:'OPV-0', fixPost:  32002, outReach:null, other: null, coverageMonthly:49, coverageCumulative: 50, dropout:null},
                {doses:'OPV-1', fixPost:  52817, outReach:null, other: null, coverageMonthly:87, coverageCumulative: 80, dropout:17},
                {doses:'OPV-2', fixPost:  45088, outReach:null, other: null, coverageMonthly:96, coverageCumulative: 97, dropout:null},
                {doses:'OPV-3', fixPost:  42203, outReach:null, other: null, coverageMonthly:81, coverageCumulative: 85, dropout:12},
                {doses:'BCG', fixPost:  61489, outReach:915, other: null, coverageMonthly:96, coverageCumulative: 97, dropout:null}

            ]}
    ];

    $scope.coveragesPregnant =  [{facilityId: 138,
        data: [
            {doses:'TT-1', fixPost:  13646, outReach:915, other: null, coverageMonthly:21, coverageCumulative: 48, dropout:null},
            {doses:'TT-2+', fixPost:  7681, outReach:null, other: null, coverageMonthly:12, coverageCumulative: 74, dropout:-52}

        ]}
    ];
    $scope.coveragesAdolescent =  [{facilityId: 138,
        data: [
            {doses:'HPV-1', fixPost:  null, outReach:null, other: null, coverageMonthly:null, coverageCumulative: null, dropout:null},
            {doses:'HPV-2', fixPost:  null, outReach:null, other: null, coverageMonthly:null, coverageCumulative: null, dropout:null},
            {doses:'HPV-3', fixPost:  null, outReach:null, other: null, coverageMonthly:null, coverageCumulative: null, dropout:null}

        ]}
    ];
    $scope.colorify = function(value){
        if(!isUndefined(value)){
            if(value >= 95) return 'green';
            if(value < 0) return 'blue';
            if(value <= 50) return 'red';
        }

    };
    $scope.iec = [];
    $scope.stocks = [{facilityId: 138,
                    data: [
                        {vaccine:'BCG', received:null, amc:$scope.amcValue, soh:$scope.sohValue, vvmStatus:null, freezing:null, expired:null, noOpened:401841, wastage:16},
                        {vaccine:'OPV', received:null, amc:$scope.amcValue, soh:$scope.sohValue, vvmStatus:null, freezing:null, expired:null, noOpened:1040875, wastage:2},
                        {vaccine:'Penta', received:null, amc:$scope.amcValue, soh:$scope.sohValue, vvmStatus:null, freezing:null, expired:null, noOpened:839570, wastage:-2},
                        {vaccine:'Pneumo', received:null, amc:$scope.amcValue, soh:$scope.sohValue, vvmStatus:null, freezing:null, expired:null, noOpened:423050, wastage:-19},
                        {vaccine:'Rota', received:null, amc:$scope.amcValue, soh:$scope.sohValue, vvmStatus:null, freezing:null, expired:null, noOpened:null, wastage:null},
                        {vaccine:'Measles', received:null, amc:$scope.amcValue, soh:$scope.sohValue, vvmStatus:null, freezing:null, expired:null, noOpened:296138, wastage:12},
                        {vaccine:'YF', received:null, amc:$scope.amcValue, soh:$scope.sohValue, vvmStatus:null, freezing:null, expired:null, noOpened:298364, wastage:14},
                        {vaccine:'TT', received:null, amc:$scope.amcValue, soh:$scope.sohValue, vvmStatus:null, freezing:null, expired:null, noOpened:446224, wastage:15},
                        {vaccine:'HPV', received:null, amc:$scope.amcValue, soh:$scope.sohValue, vvmStatus:null, freezing:null, expired:null, noOpened:null, wastage:null},
                        {vaccine:'DT', received:null, amc:$scope.amcValue, soh:$scope.sohValue, vvmStatus:null, freezing:null, expired:null, noOpened:null, wastage:null}

                    ]
    }];
    $scope.aefi = [];
    $scope.wasteManagement = [{facilityId: 138,
        data: [
            {name:'No. of safety boxes used during the month', value: 2370},
            {name:'No. of safety boxes disposed during the month', value: 1639}
        ]},{facilityId: 148,
        data: [
            {name:'No. of safety boxes used during the month', value: 1370},
            {name:'No. of safety boxes disposed during the month', value: 539}
        ]}];
    $scope.coldChain = [{facilityId: 138,
        data: [
            {name:'Districts that have reported temp. status', value: 56},
            {name:'Districts with temp mini < +2c', value: 2},
            {name:'Districts with temp maxi > +8c', value: null},
            {name:'Min temp recorded', value: 1},
            {name:'Max temp recorded', value: 8},
            {name:'Districts with low Temp. Alarms', value: null},
            {name:'Districts with high Temp. Alarms', value: null}
        ]},{facilityId: 148,
        data: [
            {name:'Districts that have reported temp. status', value: 76},
            {name:'Districts with temp mini < +2c', value: 2},
            {name:'Districts with temp maxi > +8c', value: 5},
            {name:'Min temp recorded', value: 1},
            {name:'Max temp recorded', value: 8},
            {name:'Districts with low Temp. Alarms', value: 2},
            {name:'Districts with high Temp. Alarms', value: 3}
        ]}];
    $scope.coldChainNational = [{facilityId: 138,
        data: [
            {name:'Min temp. recorded', value: null},
            {name:'Maxi temp. recorded', value: null},
            {name:'Low temp. Alarms', value: null},
            {name:'High temp. Alarms', value: null}
        ]},{facilityId: 148,
        data: [
            {name:'Min temp. recorded', value: 1},
            {name:'Maxi temp. recorded', value: null},
            {name:'Low temp. Alarms', value: 2},
            {name:'High temp. Alarms', value: null}
        ]}];
    $scope.safeInjection = [{facilityId: 138,
        data: [
            {equipment:'ADS_0.05ml', received: null, stock: null},
            {equipment:'ADS_0.5ml', received: null, stock: null},
            {equipment:'Sdilution_2ml', received: null, stock: null},
            {equipment:'Sdilution_5ml', received: null, stock: null},
            {equipment:'Saftey boxes', received: null, stock: null},
            {equipment:'Vitamin A', received: null, stock: null},
            {equipment:'Vit.A 100000 IU', received: null, stock: null},
            {equipment:'Vit.A 200000 IU', received: null, stock: null}
        ]},{facilityId: 148,
        data: [
            {equipment:'ADS_0.05ml', received: null, stock: null},
            {equipment:'ADS_0.5ml', received: null, stock: null},
            {equipment:'Sdilution_2ml', received: null, stock: null},
            {equipment:'Sdilution_5ml', received: null, stock: null},
            {equipment:'Saftey boxes', received: null, stock: null},
            {equipment:'Vitamin A', received: null, stock: null},
            {equipment:'Vit.A 100000 IU', received: null, stock: null},
            {equipment:'Vit.A 200000 IU', received: null, stock: null}
        ]}];
    $scope.diseaseSurveillance = [];

    GeoZoneFacilityTrees.get({}, function(data){
       $scope.facilities = data.geoZoneFacilities;
    });


    /* Bar Chart */
//    var barChartTicks = [[1, "Tab1"], [2, "Tab2"], [3, "Tab3"],[4, "Tab4"],[5, "Tab5"]];

    function GenerateSeries(added){
        var data = [];
        var start = 0 + added;
        var end = 100 + added;

        for(i=1;i<=12;i++){
            var d = Math.floor(Math.random() * (end - start + 1) + start);
            data.push([i, d]);
            start++;
            end++;
        }

        return data;
    }

    var amcTickLabel =  [[1, "Jan"], [2, "Feb"], [3, "March"],[4, "April"],[5, "May"],[6, "Jun"],[7, "Jul"],[8, "Aug"],[9, "Sep"],[10, "Oct"],[11, "Nov"],[12, "Dec"]];

    $scope.amcChartOption = generateBarsOption(1,amcTickLabel,'AMC');
    $scope.sohChartOption = generateBarsOption(1,amcTickLabel,'SOH');
    $scope.amcChart = [{
        label:"AMC",
        data:  $scope.randomData,
        color: "#faa732",
        points: { fillColor: "#faa732", show: true },
        lines: {show:true, showNumbers: true,
            numbers : {
                yAlign: function(y) { if(y!==0){ return y ; }else{return null;}}
                //show: true
            }}
    }];

    $scope.sohChart = [
        {
            label: "SOH",
            data:  $scope.randomData,
            color: "#5eb95e",
            bars: {
                show: true,
                showNumbers: true,
                numbers : {
                    yAlign: function(y) { if(y!==0){ return y ; }else{return null;}}
                    //show: true
                },

                barWidth: 0.8,
                fill: 0.9/*,
                 align: "center",
                 barWidth: 0.5,
                 fill: 0.9,
                 lineWidth:1*/
            }
        }];

    function generateBarsOption(id, tickLabel, yaxizLabel){
        return {
            legend: {
                position:"nw",
                noColumns: 1,
                labelBoxBorderColor: "none"
            },
            xaxis: {
                tickLength: 0, // hide gridlines
               // axisLabel: xaxisLabel,
                axisLabelUseCanvas: false,
                ticks: tickLabel,
                labelWidth: 10,
                reserveSpace: true

            } ,
            /*yaxis: {
                axisLabel: '# of Facilities',
                axisLabelUseCanvas: false
            },*/
            yaxes: [
                //yaxis:1
                {
                    position: "left",
                    //max: 1070,
                    color: "#5eb95e",
                    axisLabel: yaxizLabel,
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 23
                }
            ],

            grid: {
                hoverable: true,
                borderWidth: 1,
                backgroundColor: { colors: ["#ffffff", "#EDF5FF"] }
            },
            tooltip: true,
            tooltipOpts: {
                content: "%s is %y",
                shifts: {
                    x: 20,
                    y: 0
                },
                defaultTheme: false
            }
        };

    }

    $scope.renderGraph = function(index){
        //alert('index is '+index)
        $scope.renderIt = true;
    };
    $scope.renderIt = false;




}

function expandCollapseToggle(element) {
    $(element).parents('.accordion-section').siblings('.accordion-section').each(function () {
        $(this).find('.accordion-body').slideUp();
        $(this).find('.accordion-heading b').text('+');
    });
    $(element).siblings('.accordion-body').stop().slideToggle(function () {
        if ($(element).siblings('.accordion-body').is(':visible')) {
            $(element).find('b').text('-');
        } else {
            $(element).find('b').text('+');
        }
    });
    var offset = $(element).offset();
    var offsetTop = offset ? offset.top : undefined;
    $('body, html').animate({
        scrollTop: utils.parseIntWithBaseTen(offsetTop) + 'px'
    });
}
