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
function LabEquipmentStatusByLocationController($scope, $window, leafletData, $filter, GetFacilitiesByEquipmentStatus, ngTableParams , GetFacilitiesEquipmentStatusSummary) {

    $scope.imagePath = '';

    // since we don't know the version number for the folder under /public
    // we need to pass the right versioned directory path from the html side. This is
    //a hacky solution for a temporary fix.
    $scope.setImageFilePath = function(imagePath){
        $scope.imagePath = imagePath;
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
        }
    });


    $scope.geojson = {};

    function interpolate(value, count) {
        var val = parseFloat(value) / parseFloat(count);
        var interpolator = chroma.interpolate.bezier(['red', 'yellow', 'green']);
        return interpolator(val).hex();
    }

    $scope.style = function (feature) {
        if ($scope.filter !== undefined && $scope.filter.indicator_type !== undefined) {
            $scope.indicator_type = $scope.filter.indicator_type;
        }
        else {
            $scope.indicator_type = $scope.default_indicator;
        }
        var color = ($scope.indicator_type == 'ever_over_total') ? interpolate(feature.ever, feature.total) : ($scope.indicator_type == 'ever_over_expected') ? interpolate(feature.ever, feature.expected) : interpolate(feature.period, feature.expected);

        return {
            fillColor: color,
            weight: 1,
            opacity: 1,
            color: 'white',
            dashArray: '1',
            fillOpacity: 0.7
        };
    };

    $scope.centerJSON = function () {

        leafletData.getMap().then(function (map) {
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
                        if (latlng.lat < 0 && latlng.lng > 0) {
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


    $scope.drawMap = function (json) {

        angular.extend($scope, {
            geojson: {
                data: json,
                style: $scope.style,

                resetStyleOnMouseout: true
            }
        });

        $scope.$apply();
    };



    function popupFormat(facility, equipmentStatus) {

        var facilityEquipmentStatus = [];

        var popUpContent = "";

        equipmentStatus.forEach(function (itm) {

            if (_.isEqual(facility.facility_id, itm.facility_id)) {
                popUpContent += '<tr><td>' + itm.serial_number + '</td><td>' + itm.equipment_name + '</td><td>' + itm.equipment_status + '</td></tr>';

            }

        });

        return '<h4>' + facility.facility_name + ' ( ' + facility.facility_code + ' ) </b><br/><br/>View service contract (<a href="#">View contract</a>)</h4>' +
            '<table class="table table-bordered" style="width: 450px">' +
            '<tr><th>Serial No.</th><th>Equipment Name</th><th>Status</th></tr>' + popUpContent +
            '</table>';
    }

    $scope.OnFilterChanged = function () {

        $.getJSON('/gis/geo-zone-geometry.json', $scope.filter, function (data) {
            $scope.features = data.geoZone;

            angular.forEach($scope.features, function (feature) {
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

        addressPointsToMarkers2();
        $scope.urlParams = $.param($scope.filter);


    };

    //===== GIS =============
    var addressPointsToMarkers2 = function () {

        $.getJSON('/gis/facilitiesEquipmentsStatusGeo.json', $scope.filter, function (data) {

            plotMarkers(data);

        });
    };

    var marker_icons = {

        defaultIcon: {},
        AllNotFunctioningIcon: {
            iconUrl: "not-functioning-marker.png"
        },
        AllFunctioningIcon: {
            iconUrl: "all-functioning-marker.png"
        },
        SomeFunctioningIcon: {
            iconUrl: "some-functioning-marker.png"
        }
    };

    var plotMarkers = function(data){

        $scope.facilityEquipmentStatuses = data === null ? [] : data.equipmentsStatus;
        $scope.showSummary = false;

        $.getJSON('/gis/facilitiesEquipments.json', $scope.filter, function (data) {

                angular.extend($scope, {
                    markers: $scope.facilityEquipmentStatuses.map(function (facility) {
                        return {
                            lat: parseFloat(facility.latitude),
                            lng: parseFloat(facility.longitude),
                            message: popupFormat(facility, data.equipmentsStatus),
                            icon:  resolveMarkerIcon(facility)
                        };
                    })
                });
        });

        if($scope.facilityEquipmentStatuses.length > 0 ){

            getEquipmentStatusSummary();

            $scope.showSummary = true;
        }

    };

    var resolveMarkerIcon = function(status, imagePath){

           //A hacky way for temporary build-versioning compatibility of the file path under /public
            if(status.total_partially_operational +  status.total_fully_operational === 0 && status.total_not_operational > 0)
                return {iconUrl: $scope.imagePath+"not-functioning-marker.png"}; // marker_icons.AllNotFunctioningIcon;

            else if (status.total_partially_operational + status.total_not_operational === 0 &&  status.total_fully_operational > 0 )
                return {iconUrl: $scope.imagePath+"all-functioning-marker.png"};//marker_icons.AllFunctioningIcon;

             else
                return {iconUrl: $scope.imagePath+"some-functioning-marker.png"};//marker_icons.SomeFunctioningIcon;
        };

    // ====== Pie Chart ==========================
    var getEquipmentStatusSummary = function(){

        GetFacilitiesEquipmentStatusSummary.get($scope.filter, function(data){

            $scope.FacilityEquipStatusPieChartData = [];

            if (!(angular.isUndefined(data) || data === null)) {

                $scope.datarows = data.equipmentsStatusSummary;

                for (var i = 0; i < data.equipmentsStatusSummary.length; i++) {

                    $scope.FacilityEquipStatusPieChartData[i] = {
                        label: $scope.datarows[i].equipment_status,
                        data: $scope.datarows[i].total,
                        color: $scope.datarows[i].equipment_status === 'Fully Operational' ? '#A3CC29' : $scope.datarows[i].equipment_status === 'Partially Operational' ? '#FFB445' : '#8F0000'
                    };
                }

               bindChartEvent("#equipment-status-summary","plothover",flotChartHoverCursorHandler);

               bindChartEvent("#equipment-status-summary","plotclick", function (event, pos, item) {

                   if(item!==null) {

                       var status = $scope.FacilityEquipStatusPieChartData[item.seriesIndex].label;

                       popFacilitiesByEquipmentStatus(status);
                   }
               });
                $scope.pieChartSummary = $scope.FacilityEquipStatusPieChartData;
            }
        });
    };


    $scope.equipmentStatusSummaryPieChartOption = {
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
            container:$("#facilityLabEquipmentStatusSummary"),
            noColumns: 0,
            labelBoxBorderColor: "none",
            sorted:"descending",
            backgroundOpacity:1,
            labelFormatter: function(label, series) {
                var percent= Math.round(series.percent);
                var number= series.data[0][1];
                return('<b>'+label+'</b>');
            }
        },
        grid:{
            hoverable: true,
            clickable: true,
            borderWidth: 1,
            borderColor: "#000",
            backgroundColor: {
                colors: ["red", "green", "yellow"]
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

    // Format the content of the piechart pop-up
    var popFacilitiesByEquipmentStatus = function(status){

        $scope.filter.status =  status;

        //The filter object needs to be cloned in such a way. Since the ff operation
        // might mess up the scope.filter global object
        var urlParams = $.extend({}, $scope.filter);

        GetFacilitiesByEquipmentStatus.get($scope.filter,

            function(data){


                $scope.title = status === 'Not Operational' ?
                    'Facilities with Non functioning equipment' :
                        status === 'Partially Operational' ? 'Facilities with Some functioning equipment' :
                                                    'Facilities with All functioning equipment';

               //delete urlParams.facility;

                var url = $.param(urlParams);

                $scope.facilitiesEquipmentStatus = data.equipmentsStatus;

                $.each(data.equipmentsStatus, function(index, value) {

                    urlParams.facility = value.facility_id;
                    value.url = '/public/pages/reports/lab-equipment-list/index.html#/list?'+$.param(urlParams);
                });

                $scope.facilitySummaryModal = true;

            });
    };

    function flotChartHoverCursorHandler(event,pos,item){

        if (item && !isUndefined(item.dataIndex)) {
            $(event.target).css('cursor','pointer');
        } else {
            $(event.target).css('cursor','auto');
        }
    }

    function bindChartEvent(elementSelector, eventType, callback){
        $(elementSelector).bind(eventType, callback);
    }

}
