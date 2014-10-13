/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

function StockStatusController( $scope, leafletData, StockStatusProductList, StockedOutFacilityByProductList, UnderStockedFacilityByProductList, OverStockedFacilityByProductList, AdequatelyStockedFacilityByProductList, StockedOutFacilityList, UnderStockedFacilityList, OverStockedFacilityList, AdequatelyStockedFacilityList, SettingsByKey, ContactList, SendMessages, $filter, $dialog, messageService) {

    $scope.default_indicator = "stocked_out";
    $scope.district_title = "All Geographic Zones";


    // get configurations

/*
    SettingsByKey.get({key: 'STOCKED_OUT_SMS_TEMPLATE'}, function (data){
        $scope.sms_template           = data.settings.value;
    });

    SettingsByKey.get({key: 'UNDER_STOCKED_SMS_TEMPLATE'}, function (data){
        $scope.sms_template           = data.settings.value;
    });

    SettingsByKey.get({key: 'OVER_STOCKED_SMS_TEMPLATE'}, function (data){
        $scope.sms_template           = data.settings.value;
    });
*/
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


  /*
    $scope.ReportingFacilities = function(feature, element) {
        ReportingFacilityList.get({
            program: $scope.filter.program,
            period: $scope.filter.period,
            geo_zone: feature.id
        }, function(data) {
            $scope.facilities = data.facilities;
            $scope.successModal = true;
            $scope.show_email = $scope.show_sms = false;
            $scope.title = 'Properly Reporting Facilities in ' + feature.name;
        });
        $scope.zoomToSelectedFeature(feature);
    };
*/
/*
    $scope.NonReportingFacilities = function(feature, element) {
        NonReportingFacilityList.get({
            program: $scope.filter.program,
            period: $scope.filter.period,
            geo_zone: feature.id
        }, function(data) {
            $scope.facilities = data.facilities;
            $scope.successModal = true;
            $scope.show_email = $scope.show_sms = false;
            $scope.title = 'Non Reporting Facilities in ' + feature.name;
        });


    };

*/

    $scope.StockedOutFacilities = function(feature, element) {
        StockedOutFacilityList.get({
            program: $scope.filter.program,
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
        });
        //alert("fail:" +  JSON.stringify($scope.filter));
        $scope.zoomToSelectedFeature(feature);
    };

    /*
    $scope.StockStatusProducts = function(feature, element) {
        StockStatusProductList.get({
            program: $scope.filter.program,
            period: $scope.filter.period
        }, function(data) {
            $scope.products = data.products;
            $scope.successModal = true;
            alert("fail:" +  JSON.stringify($scope.products));

        });
    };

*/
    $scope.UnderStockedFacilities = function(feature, element) {
        UnderStockedFacilityList.get({
            program: $scope.filter.program,
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
        OverStockedFacilityList.get({
            program: $scope.filter.program,
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
        AdequatelyStockedFacilityList.get({
            program: $scope.filter.program,
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


  $scope.AdequatelyStockedProducts = function(feature, element) {
        AdequatelyStockedFacilityByProductList.get({
            program: $scope.filter.program,
            period: $scope.filter.period,
            product: feature.id,
            geo_zone: $scope.filter.zone
        }, function(data) {
            $scope.products = data.products;
            $scope.successModal2 = true;
            $scope.show_email = $scope.show_sms = false;
            $scope.title = 'Adequately Stocked Facilities in ' + feature.primayname;
            // alert("fail:" + JSON.stringify($scope.facilities));
        });

        $scope.zoomToSelectedFeature(feature);
    };

    $scope.StockedOutProducts = function(feature, element) {
        StockedOutFacilityByProductList.get({
            program: $scope.filter.program,
            period: $scope.filter.period,
            product: feature.id,
            geo_zone: $scope.filter.zone
        }, function(data) {
            $scope.products = data.products;
            $scope.successModal2 = true;
            $scope.show_email = $scope.show_sms = false;
            $scope.title = 'Stocked Out Facilities for ' + feature.primaryname;
            //alert("fail2:" + JSON.stringify($scope.filter));
        });

        $scope.zoomToSelectedFeature(feature);
    };

    $scope.UnderStockedProducts = function(feature, element) {
        UnderStockedFacilityByProductList.get({
            program: $scope.filter.program,
            period: $scope.filter.period,
            product: feature.id,
            geo_zone: $scope.filter.zone
        }, function(data) {
            $scope.products = data.products;
            $scope.successModal2 = true;
            $scope.show_email = $scope.show_sms = false;
            $scope.title = 'UnderStocked Out Facilities in ' + feature.primayname;
            // alert("fail:" + JSON.stringify($scope.facilities));
        });

        $scope.zoomToSelectedFeature(feature);
    };

    $scope.OverStockedProducts = function(feature, element) {
        OverStockedFacilityByProductList.get({
            program: $scope.filter.program,
            period: $scope.filter.period,
            product: feature.id,
            geo_zone: $scope.filter.zone
        }, function(data) {
            $scope.products = data.products;
            $scope.successModal2 = true;
            $scope.show_email = $scope.show_sms = false;
            $scope.title = 'Over Stocked Facilities in ' + feature.primayname;
            //alert("fail:" + JSON.stringify(feature));
        });

        $scope.zoomToSelectedFeature(feature);
    };

 ///////////


    $scope.expectedFilter = function(item) {
        return item.period > 0;
    };

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
        //var color = ($scope.indicator_type == 'ever_over_total') ? interpolate(feature.ever, feature.total) : ($scope.indicator_type == 'ever_over_expected') ? interpolate(feature.ever, feature.expected) : interpolate(feature.period, feature.expected);
        //var color = ($scope.indicator_type == 'stocked_out') ? interpolate(feature.stockedout, feature.period) : ($scope.indicator_type == 'under_stocked') ? interpolate(feature.understocked, feature.period) : ($scope.indicator_type == 'over_stocked') ? interpolate(feature.overstocked, feature.period) : interpolate(feature.adquatelystocked, feature.period);

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

    // <a href="my.html" onclick="return getValue()">Go</a>
    function zoomToFeature(e) {
        //todo: complete this
    }

    getStockStatusByProduct = function() {
        $.getJSON('/gis/stock-status-products.json', $scope.filter, function(data) {
            $scope.products = data.products;
        });
    };

    $scope.OnFilterChanged = function() {

        $.getJSON('/gis/stock-status-facilities.json', $scope.filter, function(data) {
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


        $.getJSON('/gis/stock-status-products.json', $scope.filter, function(data) {
            $scope.products = data.products;
        });

    };

}