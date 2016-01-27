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
    var color = ($scope.indicator_type === 'ever_over_total') ? interpolate(feature.ever, feature.total) : ($scope.indicator_type === 'ever_over_expected') ? interpolate(feature.ever, feature.expected) : interpolate(feature.period, feature.expected);

    return {
      fillColor: color,
      weight: 1,
      opacity: 1,
      color: 'white',
      dashArray: '1',
      fillOpacity: 0.7
    };
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
      zoomAndCenterMap(leafletData, $scope);
    });

  };

  initiateMap($scope);

  $scope.onDetailClicked = function(feature){
    $scope.currentFeature = feature;
    $scope.$broadcast('openDialogBox');
  };
}
