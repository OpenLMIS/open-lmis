/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

function ReportingRateController($scope, leafletData, ReportingFacilityList, SendMessagesReportAttachment, GetFacilitySupervisors , NonReportingFacilityList, SettingsByKey, ContactList, SendMessages, $filter , $dialog, messageService) {


  $scope.default_indicator = "period_over_expected";

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

    SettingsByKey.get({key: 'LATE_RNR_SUPERVISOR_NOTIFICATION_EMAIL_TEMPLATE'},function (data){
        $scope.email_template_supervisor           = data.settings.value;
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

   $scope.showSendEmailSupervisor = function(facility){
       $scope.selected_facility = facility;
       GetFacilitySupervisors.get({
           facilityId : facility.id
       }, function(data){
           $scope.contacts = data.supervisors;
           $scope.attachementCaption = "Attachement: Non reporting facility report for "+ $scope.zoneName+ ' district';
           var fullReportfilter = $.extend($scope.filter, {zone: $scope.zoneid});
           $scope.reportFilter = '/reports/download/non_reporting/PDF?max=10000&' + $.param(fullReportfilter);
           console.log($scope.reportFilter);
       });

       $scope.show_email_supervisor = !$scope.show_email_supervisor;

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
    else if($scope.show_email_supervisor){
        $scope.sendSupervisorEmail();
        $scope.show_email_supervisor = false;
    }
    else{
      $scope.sendFacilityEmail();
      $scope.show_email = false;
    }

    $scope.selected_facility.sent=true;

  };

    $scope.sendSupervisorEmail = function(){

        var messages = constructMessage();

        //Since originally the reporting rate report doesn't have zone parameter
        var filterParamsWithZone = $.extend($scope.filter, {zone: $scope.zoneid});
        var emailParam = {messages: messages, reportKey: 'non_reporting', subject: 'Non reporting facilities', outputOption: 'xls', reportParams: filterParamsWithZone};

        SendMessagesReportAttachment.post(emailParam, function (data) {
            $scope.sent_confirmation = true;
        });
    };

    $scope.sendFacilityEmail = function(){

        var messages = constructMessage();

        SendMessages.post({messages: messages}, function(data){
            $scope.sent_confirmation = true;
        });
    };

  var constructMessage = function(){

    // construct the messges here
    var messages  = [];

    for(var i = 0; i < $scope.contacts.length; i++){
      var template = $scope.show_email_supervisor ? $scope.email_template_supervisor : $scope.email_template;
      var contact = $scope.contacts[i];

      template = template.replace( '{name}' , contact.name);
      template = template.replace( '{facility_name}', $scope.selected_facility.name );
      template = template.replace( '{period}', $scope.selected_facility.name );

      messages.push({type: 'email', facility_id: $scope.selected_facility.id, contact: contact.contact, message: template });
    }

      return messages;
  };

  // end send actions
  $scope.ReportingFacilities = function(feature, element) {
    ReportingFacilityList.get({
      program: $scope.filter.program,
      period: $scope.filter.period,
      geo_zone: feature.id
    }, function(data) {
      $scope.facilities = data.facilities;
      $scope.successModal = true;
      $scope.show_email = $scope.show_sms = $scope.show_email_supervisor = false;
      $scope.zoneid = feature.id;
      $scope.zoneName = feature.name;
      $scope.title = 'Properly Reporting Facilities in ' + feature.name;
    });
    $scope.zoomToSelectedFeature(feature);
  };

  $scope.NonReportingFacilities = function(feature, element) {
    NonReportingFacilityList.get({
      program: $scope.filter.program,
      period: $scope.filter.period,
      geo_zone: feature.id
    }, function(data) {
      $scope.facilities = data.facilities;
      $scope.successModal = true;
      $scope.zoneid = feature.id;
      $scope.zoneName = feature.name;
      $scope.show_email = $scope.show_sms = $scope.show_email_supervisor = false;
      $scope.title = 'Non Reporting Facilities in ' + feature.name;
    });

      console.log(feature.id);
  };

    $scope.expectedFilter = function(item) {
        return item.expected > 0;
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
            labels: [ 'Non Reporting', 'Partial Reporting ', 'Fully Reporting','Not expected to Report']
        }
    });



    $scope.indicator_types = [{
        code: 'ever_over_total',
        name: 'Ever Reported / Total Facilities'
    }, {
        code: 'ever_over_expected',
        name: 'Ever Reported / Expected Facilities'
    }, {
        code: 'period_over_expected',
        name: 'Reported during period / Expected Facilities'
    }];



    $scope.geojson = {};

    function interpolate(value, count) {
        var val = parseFloat(value) / parseFloat(count);
        var interpolator = chroma.interpolate.bezier(['red', 'yellow', 'green']);
        return interpolator(val).hex();
    }

    $scope.style = function(feature) {
        if($scope.filter !== undefined && $scope.filter.indicator_type !== undefined){
            $scope.indicator_type = $scope.filter.indicator_type;
        }
        else{
            $scope.indicator_type = $scope.default_indicator;
        }
        var color = ($scope.indicator_type == 'ever_over_total') ? interpolate(feature.ever, feature.total) : ($scope.indicator_type == 'ever_over_expected') ? interpolate(feature.ever, feature.expected) : interpolate(feature.period, feature.expected);

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
        return '<table class="table table-bordered" style="width: 250px"><tr><th colspan="2"><b>' + feature.properties.name + '</b></th></tr>' +
            '<tr><td>Expected Facilities</td><td class="number">' + feature.expected + '</td></tr>' +
            '<tr><td>Reported This Period</td><td class="number">' + feature.period + '</td></tr>' +
            '<tr><td>Ever Reported</td><td class="number">' + feature.ever + '</td></tr>' +
            '<tr><td class="bold">Total Facilities</b></td><td class="number bold">' + feature.total + '</td></tr>';
    }

    function zoomToFeature(e) {
      //todo: complete this
    }

    $scope.OnFilterChanged = function() {

        $.getJSON('/gis/reporting-rate.json', $scope.filter, function(data) {
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

    };

}