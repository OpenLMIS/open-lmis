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

function MNCHStatusController( $scope, leafletData, RmnchStockedOutFacilityByProductList, RmnchUnderStockedFacilityByProductList, RmnchOverStockedFacilityByProductList, RmnchAdequatelyStockedFacilityByProductList, RmnchStockedOutFacilityList, RmnchUnderStockedFacilityList, RmnchOverStockedFacilityList, RmnchAdequatelyStockedFacilityList, RmnchStockStatusProductConsumptionGraph, SettingsByKey, ContactList, SendMessages, $filter, $dialog, messageService) {

    $scope.default_indicator = "stocked_out";
    $scope.district_title = "All Geographic Zones";
    $scope.showConsumed = true;
    $scope.showAMC = true;
    $scope.showSOH = true;


    // get configurations
    SettingsByKey.get({key: 'LATE_RNR_NOTIFICATION_SMS_TEMPLATE'}, function (data){
        $scope.sms_template           = data.settings.value;
    });

    SettingsByKey.get({key: 'LATE_RNR_NOTIFICATION_EMAIL_TEMPLATE'}, function (data){
        $scope.email_template         = data.settings.value;
    });

    SettingsByKey.get({key: 'SMS_ENABLED'},function (data){
        $scope.sms_enabled            = data.settings.value;
    });
    // end of configurations

    // show dialog box contents


    var barColors = [{'minRange': -100, 'maxRange': 0, 'color' : '#E23E3E', 'description' : 'Red color for product with a fill rate <= 0 '},
        {'minRange': 1, 'maxRange': 50, 'color' : '#FEBA50', 'description' : 'Yellow color for product with a fill rate > 0 and <= 50 '},
        {'minRange': 51, 'maxRange': 100, 'color' : '#38AB49', 'description' : 'Green color for product with a fill rate > 50 '}];
    var $scaleColor = '#D7D5D5';
    var defaultBarColor = '#FEBA50';
    var $lineWidth = 5;
    var barColor = defaultBarColor;


    $scope.showSendEmail = function(facility){
        $scope.selected_facility = facility;
        ContactList.get({type:'email', facilityId: facility.id}, function(data){
            $scope.contacts = data.contacts;
        });
        $scope.show_email = !$scope.show_email;
    };

    $scope.showSendSms = function(facility){
        $scope.selected_facility = facility;
        ContactList.get({type:'sms', facilityId: facility.id}, function(data){
            $scope.contacts = data.contacts;
        });
        $scope.show_sms = !$scope.show_sms;
    };
    // end of dialog box contents

    // start send actions

    $scope.doSend = function(){

        if($scope.show_sms){
            $scope.sendSms();
            $scope.show_sms=false;
        }
        else{
            $scope.sendEmail();
            $scope.show_email=false;
        }
        $scope.selected_facility.sent=true;

    };


    $scope.sendEmail = function(){
        // construct the messges here
        var messages  = [];
        for(var i = 0; i < $scope.contacts.length; i++){
            var template = $scope.email_template;
            var contact = $scope.contacts[i];

            template = template.replace( '{name}' , contact.name);
            template = template.replace( '{facility_name}', $scope.selected_facility.name );
            template = template.replace( '{period}', $scope.selected_facility.name );

            messages.push({type: 'email', facility_id: $scope.selected_facility.id, contact: contact.contact, message: template });
        }

        SendMessages.post({messages: messages}, function(data){
            $scope.sent_confirmation = true;
        });

    };

    $scope.sendSms = function(){



        // construct the messages that go out
        var messages  = [];
        for(var i = 0; i < $scope.contacts.length; i++){
            var template = $scope.sms_template;
            var contact = $scope.contacts[i];

            template = template.replace( '{name}' , contact.name);
            template = template.replace( '{facility_name}', $scope.selected_facility.name );
            template = template.replace( '{period}', $scope.selected_facility.name );

            messages.push({type: 'sms', facility_id: $scope.selected_facility.id, contact: contact.contact, message: template });

        }

        SendMessages.post({messages: messages}, function(data){
            $scope.sent_confirmation = true;
        });

    };

    // end send actions

    $scope.StockedOutFacilities = function(feature, element) {
        RmnchStockedOutFacilityList.get({
            period: $scope.filter.period,
            product: $scope.filter.product,
            geo_zone: feature.id
        }, function(data) {
            $scope.facilities = data.facilities;
            $scope.successModal = true;
            $scope.filter.zone = feature.id;
            $scope.show_email = $scope.show_sms = false;
            $scope.title = 'Stocked Out Facilities in ' + feature.name;
            $scope.district_title = feature.name;
            getStockStatusByProduct();
            //getStockStatusProductConsumption();
            loadStockStatusConsumptionData();

        });
        //alert("fail:" +  JSON.stringify($scope.filter));
        $scope.zoomToSelectedFeature(feature);
    };

    $scope.UnderStockedFacilities = function(feature, element) {
        RmnchUnderStockedFacilityList.get({
            period: $scope.filter.period,
            product: $scope.filter.product,
            geo_zone: feature.id
        }, function(data) {
            $scope.facilities = data.facilities;
            $scope.successModal = true;
            $scope.filter.zone = feature.id;
            $scope.show_email = $scope.show_sms = false;
            $scope.title = 'Under Stocked Facilities in ' + feature.name;
            $scope.district_title = feature.name;
            getStockStatusByProduct();
        });
        $scope.zoomToSelectedFeature(feature);
    };

    $scope.OverStockedFacilities = function(feature, element) {
        RmnchOverStockedFacilityList.get({
            period: $scope.filter.period,
            product: $scope.filter.product,
            geo_zone: feature.id
        }, function(data) {
            $scope.facilities = data.facilities;
            $scope.successModal = true;
            $scope.filter.zone = feature.id;
            $scope.show_email = $scope.show_sms = false;
            $scope.title = 'Over Stocked Facilities in ' + feature.name;
            $scope.district_title = feature.name;
            getStockStatusByProduct();
        });
        $scope.zoomToSelectedFeature(feature);
    };

    $scope.AdequatelyStockedFacilities = function(feature, element) {
        RmnchAdequatelyStockedFacilityList.get({
            period: $scope.filter.period,
            product: $scope.filter.product,
            geo_zone: feature.id
        }, function(data) {
            $scope.facilities = data.facilities;
            $scope.successModal = true;
            $scope.filter.zone = feature.id;
            $scope.show_email = $scope.show_sms = false;
            $scope.title = 'Adequately Stocked Facilities in ' + feature.name;
            $scope.district_title = feature.name;
            getStockStatusByProduct();
            // alert("fail:" + JSON.stringify($scope.facilities));
        });

        $scope.zoomToSelectedFeature(feature);
    };

    // stock status by product


     $scope.StockedOutProducts = function(feature, element) {
        RmnchStockedOutFacilityByProductList.get({
            period: $scope.filter.period,
            product: feature.id,
            geo_zone: $scope.filter.zone
        }, function(data) {
            $scope.productPopup = data.products;
            //alert("fail:" + JSON.stringify($scope.productPopup));
            $scope.successModal2 = true;
            $scope.show_email = $scope.show_sms = false;
            $scope.title = 'Stocked Out Facilities for ' + feature.primaryname + ' in ' + $scope.district_title;

        });
        $scope.zoomToSelectedFeature(feature);
    };

    $scope.UnderStockedProducts = function(feature, element) {
        RmnchUnderStockedFacilityByProductList.get({
            period: $scope.filter.period,
            product: feature.id,
            geo_zone: $scope.filter.zone
        }, function(data) {
            $scope.productPopup = data.products;
            $scope.successModal2 = true;
            $scope.show_email = $scope.show_sms = false;
            $scope.title = 'UnderStocked Out Facilities for ' + feature.primaryname + ' in ' + $scope.district_title;
            // alert("fail:" + JSON.stringify($scope.facilities));
        });

        $scope.zoomToSelectedFeature(feature);
    };

    $scope.OverStockedProducts = function(feature, element) {
        RmnchOverStockedFacilityByProductList.get({
            period: $scope.filter.period,
            product: feature.id,
            geo_zone: $scope.filter.zone
        }, function(data) {
            $scope.productPopup = data.products;
            $scope.successModal2 = true;
            $scope.show_email = $scope.show_sms = false;
            $scope.title = 'Over Stocked Facilities for ' + feature.primaryname + ' in ' + $scope.district_title;
            //alert("fail:" + JSON.stringify(feature));
        });

        $scope.zoomToSelectedFeature(feature);
    };

    $scope.AdequatelyStockedProducts = function(feature, element) {
        RmnchAdequatelyStockedFacilityByProductList.get({
            period: $scope.filter.period,
            product: feature.id,
            geo_zone: $scope.filter.zone
        }, function(data) {
            $scope.productPopup = data.products;
            $scope.successModal2 = true;
            $scope.show_email = $scope.show_sms = false;
            $scope.title = 'Adequately Stocked Facilities for '+ feature.primaryname + ' in ' + $scope.district_title;
            // alert("fail:" + JSON.stringify($scope.facilities));
        });

        $scope.zoomToSelectedFeature(feature);
    };


    ///////////
    loadStockStatusConsumptionData = function(){
        $scope.productsSelected = [];
        angular.forEach($scope.products, function(itm,idx){
            if(itm.selected){
                $scope.productsSelected.push(itm.id);
            }
        });

        RmnchStockStatusProductConsumptionGraph.get({
            program: $scope.filter.program,
            product: $scope.productsSelected,
            period: $scope.filter.period,
            geo_zone: $scope.filter.zone
        },function (data){
            $scope.consumptionData =  data.consumption;
            //alert("fail5:" +  JSON.stringify($scope.consumptionData));
            adjustDataForChart($scope.consumptionData);


        });
    };

    $scope.resetConsumptionChartData = function(){

        $scope.productstocks = null;
        $scope.multiBarsRenderedData = undefined;
        $scope.multiBarsData = undefined;
        $scope.multipleBarsOption = undefined;
    };
    $scope.productstocks = null;

    var adjustDataForChart = function(consumptionData){
        if(isUndefined(consumptionData) || consumptionData.length === 0){
            $scope.resetConsumptionChartData();
            return;
        }

        var groupedByProduct = _.chain(consumptionData).groupBy('productId').map(function(value, key) { return {productId: key, product: _.first(value).productName, productData: value };}).value();

        $scope.productstocks = [];

        angular.forEach(groupedByProduct,function(productStock){
            var productId = _.first(productStock.productData).productId;
            var productName = _.first(productStock.productData).productName;
            var quantityOnHandSeries =  _.pairs(_.object(_.range(productStock.productData.length), _.map(_.pluck(productStock.productData,'quantityOnHand'),function(stat){ return stat;})));
            var quantityConsumedSeries =  _.pairs(_.object(_.range(productStock.productData.length), _.map(_.pluck(productStock.productData,'quantityConsumed'),function(stat){ return  stat;})));
            var amcSeries =  _.pairs(_.object(_.range(productStock.productData.length), _.map(_.pluck(productStock.productData,'amc'),function(stat){ return  stat;})));

            var periodSeries = _.pairs(_.object(_.range(productStock.productData.length), _.map(productStock.productData ,function(stat){ return  stat.periodName +' '+stat.periodYear;})));

            var barsOption = generateBarsOption(productId, periodSeries, productName);

            $scope.productstocks.push({productId: productId,options: barsOption, dataSeries: [{
                label: "SOH",
                data: quantityOnHandSeries,
                color: "#5eb95e",
                bars: {
                    show: true,
                    align: "center",
                    barWidth: 0.5,
                    fill: 0.9,
                    lineWidth:1
                }
            },{
                label: "Consumed",
                data: quantityConsumedSeries,
                 yaxis: 3,
                color: "#4bb1cf",
                points: { fillColor: "#4bb1cf", show: $scope.showConsumed },
                lines: {show:$scope.showConsumed}
            },{
                label:"AMC",
                data: amcSeries,
                 yaxis: 3,
                color: "#faa732",
                points: { fillColor: "#faa732", show: true },
                lines: {show:true}
            }]});

        });
    };

    function generateBarsOption(id, tickLabel, xaxisLabel){
               return {
            legend: {
                position:"nw",
                noColumns: 1,
                labelBoxBorderColor: "none"
           },
            xaxis: {
                tickLength: 0, // hide gridlines
                axisLabel: xaxisLabel,
                axisLabelUseCanvas: false,
                ticks: tickLabel,
                labelWidth: 10,
                reserveSpace: true

            } ,
            yaxes: [
                //yaxis:1
                {
                    position: "left",
                    //max: 1070,
                    color: "#5eb95e",
                    axisLabel: "SOH",
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 3
                },
                //yaxis:2
                {
                    position: "right",
                    color: "#dd514c",
                    axisLabel: "Consumption",
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 3
                },
                //yaxis:3
                {
                    position: "right",
                    //tickSize: 50,
                    color: "#faa732",
                    axisLabel: "AMC/Consumption",
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 3
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
    $scope.barsOption = {};
    $scope.barsData =[];

    $scope.productSelectChange = function(selected, product){
        loadStockStatusConsumptionData();
    };

//////////////////////



    $scope.expectedFilter = function(item) {
        return (item.period > 0 && item.stockedout >= 0 && item.understocked >= 0 && item.overstocked >= 0 && item.adequatelystocked >= 0);
    };

    $scope.expectedProductFilter = function(item) {
        return (item.reported > 0 && item.stockedout >= 0 && item.understocked >= 0 && item.overstocked >= 0 && item.adequatelystocked >= 0);
    };



    ////////////////////////////////////////////////////
    angular.extend($scope, {
        layers: {
            baselayers: {
                googleTerrain: {
                    name: 'Terrain',
                    layerType: 'TERRAIN',
                    type: 'google'
                },
                googleHybrid: {
                    name: 'Hybrid',
                    layerType: 'HYBRID',
                    type: 'google'
                },
                googleRoadmap: {
                    name: 'Streets',
                    layerType: 'ROADMAP',
                    type: 'google'
                }
            }
        },
        legend: {
            position: 'bottomleft',
            colors: [ '#FF0000', '#FFFF00', '#5eb95e',"#000000" ],
            labels: [ 'All Stockouts', 'Some Stockedouts','No Stockouts','Not expected to Report']
        }
    });

    $scope.indicator_types = [{
        code: 'adequately_stocked',
        name: 'Adequately Stocked'
    }, {
        code: 'over_stocked',
        name: 'Over Stocked'
    }, {
        code: 'under_stocked',
        name: 'Under Stocked'
    }, {
        code: 'stocked_out',
        name: 'Stocked Out'
    }];


    $scope.geojson = {};

    function interpolate(value, count) {
        var val = parseFloat(value) / parseFloat(count);
        var interpolator = chroma.interpolate.bezier(['green', 'yellow', 'red']);
        return interpolator(val).hex();
    }

    $scope.style = function(feature) {
        if($scope.filter !== undefined && $scope.filter.indicator_type !== undefined){
            $scope.indicator_type = $scope.filter.indicator_type;
        }
        else{
            $scope.indicator_type = $scope.default_indicator;
        }

        var color = interpolate(feature.stockedout, feature.period);
        return {
            fillColor:  color,
            weight:     1,
            opacity:    1,
            color:      'white',
            dashArray:  '1',
            fillOpacity: 0.7
        };
    };

    $scope.centerJSON = function() {

        leafletData.getMap().then(function(map) {
            var latlngs = [];
            for (var c = 0; c < $scope.features.length; c++) {
                if ($scope.features[c].geometry === null || angular.isUndefined($scope.features[c].geometry))
                    continue;
                if ($scope.features[c].geometry.coordinates === null || angular.isUndefined($scope.features[c].geometry.coordinates))
                    continue;
                for (var i = 0; i < $scope.features[c].geometry.coordinates.length; i++) {
                    var coord = $scope.features[c].geometry.coordinates[i];
                    for (var j in coord) {
                        var points = coord[j];
                        var latlng = L.GeoJSON.coordsToLatLng(points);

                        //this is a hack to make the tz shape files to work
                        //sadly the shapefiles for tz and zm have some areas that are in europe,
                        //which indicates that the quality of the shapes is not good,
                        //however the zoom neeeds to show the correct country boundaries.
                        if(latlng.lat < 0 && latlng.lng > 0){
                            latlngs.push(latlng);
                        }
                    }
                }
            }

            thevar = latlngs;
            theMap = map;
            map.fitBounds(latlngs);
        });
    };


    $scope.drawMap = function(json) {
        angular.extend($scope, {
            geojson: {
                data: json,
                style: $scope.style,
                onEachFeature: onEachFeature,
                resetStyleOnMouseout: true
            }
        });

        $scope.$apply();
    };

    function onEachFeature(feature, layer) {

        layer.on({
            click: zoomToFeature
        });

        layer.bindPopup(popupFormat(feature));
    }


    function popupFormat(feature) {

        return '<table class="table table-bordered" style="width: 310px;"><tr><td><b>District</b></td><td>' + feature.properties.name + '</td><td><b>Total Facilities</b></td><td>' + feature.total + '</td></tr>' +
            '<tr><td><b>Region</b></td><td>' + feature.georegion + '</td><td><b>Expected Facilities</b></td><td>' + feature.expected + '</td></tr>' +
            '<tr><td><b>Zone</b></td><td>' + feature.geozone + '</td><td><b>Reported This Period</b></td><td>' + feature.period + '</td></tr></table>' +
            '<table class="table table-bordered" style="width: 310px;"><tr><th class="bold">Indicator</th><th class="bold">This Period</th><th class="bold">Previous Period</th></tr>' +
            '<tr bgcolor="#dd514c"><td class="bold">Stocked Out</td><td class="number">' + feature.stockedout + '</td><td class="number">' + feature.stockedoutprev + '</td></tr>' +
            '<tr bgcolor="#faa732"><td class="bold">Under Stocked</td><td class="number">&nbsp;&nbsp;' + feature.understocked + '</td><td class="number">' +feature.understockedprev +'</td></tr>' +
            '<tr bgcolor="#4bb1cf"><td>Over Stocked</td><td class="number">&nbsp;&nbsp;' + feature.overstocked + '</td><td class="number">' +feature.overstockedprev +'</td></tr>' +
            '<tr bgcolor="#5eb95e"><td>Adequately Stocked</td><td class="number">&nbsp;&nbsp;' + feature.adequatelystocked + '</td><td class="number">' +feature.adequatelystockedprev +'</td></tr>';

    }

    function zoomToFeature(e) {
        //todo: complete this
    }

    getStockStatusByProduct = function() {
        $.getJSON('/rmnch/stock-status-products.json', $scope.filter, function(data) {
            $scope.products = data.products;
        });
    };

    getStockStatusProductConsumption = function() {
        $.getJSON('/rmnch/stock-status-product-consumption.json', $scope.filter, function(data) {
            $scope.consumption = data.consumption;

        });
    };
    $scope.OnFilterChanged = function() {

        $.getJSON('/rmnch/stock-status-facilities.json', $scope.filter, function(data) {
            $scope.features = data.map;



            angular.forEach($scope.features, function(feature) {

                feature.geometry_text = feature.geometry;
                feature.geometry = JSON.parse(feature.geometry);
                feature.type = "Feature";
                feature.properties = {};
                feature.properties.name = feature.name;
                feature.properties.id = feature.id;
            });
            $scope.drawMap({
                "type": "FeatureCollection",
                "features": $scope.features
            });
            $scope.centerJSON();
        });

        $.getJSON('/rmnch/stock-status-products.json', $scope.filter, function(data) {
            var markSelectedProducts = null;

            if(!isUndefined(data.products)){

                markSelectedProducts = _.map(data.products ,function(product){

                    if($scope.filter.product == product.id){
                        product.selected = true;
                    }

                    return product;
                });
            }
            $scope.products = markSelectedProducts;
            loadStockStatusConsumptionData();
        });

    };

}
