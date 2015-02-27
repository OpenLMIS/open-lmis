/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

function ReportingRateController($scope, leafletData) {

  $scope.geojson = {};

  $scope.default_indicator = "period_over_expected";

  $scope.expectedFilter = function (item) {
    return item.expected > 0;
  };

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
      map.fitBounds(latlngs);
    });
  };

  $scope.drawMap = function (json) {

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

  $scope.OnFilterChanged = function () {

    $.getJSON('/gis/reporting-rate.json', $scope.filter, function (data) {
      $scope.features = data.map;

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

  };

  initiateMap($scope);

  $scope.onDetailClicked = function(feature){
    $scope.currentFeature = feature;
    $scope.$broadcast('openDialogBox');
  };
}