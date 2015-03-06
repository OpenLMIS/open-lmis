/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

function interpolate(value, count) {
  var val = parseFloat(value) / parseFloat(count);
  var interpolator = chroma.interpolate.bezier(['red', 'yellow', 'green']);
  return interpolator(val).hex();
}

function initiateMap(scope) {
  angular.extend(scope, {
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
      colors: ['#FF0000', '#FFFF00', '#5eb95e', "#000000"],
      labels: ['Non Reporting', 'Partial Reporting ', 'Fully Reporting', 'Not expected to Report']
    }
  });

  scope.indicator_types = [
    {
      code: 'ever_over_total',
      name: 'Ever Reported / Total Facilities'
    },
    {
      code: 'ever_over_expected',
      name: 'Ever Reported / Expected Facilities'
    },
    {
      code: 'period_over_expected',
      name: 'Reported during period / Expected Facilities'
    }
  ];


  scope.viewOptins = [
    {id: '0', name: 'Non Reporting Only'},
    {id: '1', name: 'Reporting Only'},
    {id: '2', name: 'All'}
  ];

}

function popupFormat(feature) {
  return '<table class="table table-bordered" style="width: 250px"><tr><th colspan="2"><b>' + feature.properties.name + '</b></th></tr>' +
    '<tr><td>Expected Facilities</td><td class="number">' + feature.expected + '</td></tr>' +
    '<tr><td>Reported This Period</td><td class="number">' + feature.period + '</td></tr>' +
    '<tr><td>Ever Reported</td><td class="number">' + feature.ever + '</td></tr>' +
    '<tr><td class="bold">Total Facilities</b></td><td class="number bold">' + feature.total + '</td></tr>';
}

function onEachFeature(feature, layer) {
  layer.bindPopup(popupFormat(feature));
}

function zoomAndCenterMap (leafletData, $scope) {
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
}