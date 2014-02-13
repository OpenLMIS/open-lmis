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

  function interpolate(value, count) {
    var val = parseFloat(value) / parseFloat(count);
    var interpolator = chroma.interpolate.bezier(['red', 'yellow', 'green']);
    return interpolator(val).hex();
  }

  $scope.style = function (feature) {
    var val = parseFloat(feature.ever) / parseFloat(feature.total);
    return {
      fillColor: interpolate(feature.ever, feature.total),
      weight: 1,
      opacity: 1,
      color: 'white',
      dashArray: '1',
      fillOpacity: ( val > 0.5 ) ? val : 0.5
    };
  };

  $scope.centerJSON = function () {
    leafletData.getMap().then(function (map) {
      var latlngs = [];
      for (var c = 0; c < $scope.features.length; c++) {
        if ($scope.features[c].geometry == null)
          continue;
        if ($scope.features[c].geometry.coordinates == null)
          continue;
        for (var i = 0; i < $scope.features[c].geometry.coordinates.length; i++) {
          var coord = $scope.features[c].geometry.coordinates[i];
          for (var j in coord) {
            var points = coord[j];
            latlngs.push(L.GeoJSON.coordsToLatLng(points));
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

  function onEachFeature(feature, layer) {
    layer.on({
      click: zoomToFeature
    });
    layer.bindPopup(popupFormat(feature));
  };

  function popupFormat(feature){
    return '<b>' + feature.properties.name + '</b><br /><div>Total Facilities: ' + feature.total + '</div><div>Reporting Facilities: ' + feature.ever + '</div>';
  }

  function zoomToFeature(e) {
    console.info(e);
  }

  $.getJSON('/gis/reporting-rate.json', function (data) {
    $scope.features = data.map;

    angular.forEach($scope.features, function (feature) {
      feature.geometry_text = feature.geometry;
      feature.geometry = JSON.parse(feature.geometry);
      feature.type = "Feature";
      feature.properties = {};
      feature.properties.total = feature.total;
      feature.properties.name = feature.name;
      feature.properties.id = feature.id;
    });

    $scope.drawMap({
      "type": "FeatureCollection",
      "features": $scope.features
    });
    $scope.centerJSON();
  });


}