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

function GeographicZonesJsonController($scope, leafletData, FlatGeographicZoneList, GeographicLevels, SaveGeographicInfo) {
  $scope.features = [];

  $scope.importFile = function () {

    var reader = new FileReader();
    var fileInput = document.getElementById('jsonfile');
    reader.onload = function (fi) {
        $scope.json = JSON.parse(fi.target.result);
        $scope.json.features.forEach(function (feature) {
        $scope.features.push(feature);
      });

      $scope.drawMap($scope.json);
      zoomAndCenterMap(leafletData, $scope);
    };
    reader.readAsText(fileInput.files[0]);
  };

  $scope.style = function(feature){
    return {
      fillColor: (feature.properties.active)?"black" : (feature.properties.mapped)?"green":"gray",
      weight: 1,
      opacity: 1,
      color: 'white',
      dashArray: '1',
      fillOpacity: 0.7
    };
  };

  $scope.drawMap = function(json){
    angular.extend($scope, {
      geojson: {
        data: json,
        style: $scope.style,
        onEachFeature: function(feature){
          return (feature.properties.ADM3)?feature.properties.ADM3 : feature.properties.ADM2;
        },
        resetStyleOnMouseout: true
      }
    });
  };

  GeographicLevels.get(function (data) {
    $scope.geographicLevels = data.geographicLevelList;
  });

  FlatGeographicZoneList.get(function (data) {
    $scope.geographicZones = data.zones;
    $scope.origionalGeographicZones = data.zones;
  });

  $scope.filterByLevel = function () {
    if ($scope.level === undefined) {
      $scope.geographicZones = $scope.origionalGeographicZones;
    } else {
      $scope.geographicZones = [];
      $scope.origionalGeographicZones.forEach(function (zone) {
        if (zone.levelId === $scope.level) {
          $scope.geographicZones.push(zone);
        }
      });
    }
  };

  $scope.onDrag = function($data){
    if($scope.current_feature !== undefined){
      $scope.current_feature.properties.active = false;
    }
    $scope.current_feature = $data;
    $data.properties.active = true;
    // refresh the map here.
    $scope.drawMap( {
      "type": "FeatureCollection",
      "features": $scope.features
    } );
  };

  $scope.onDrop = function ($data, $event, zone) {

    zone.mapped = true;
    $data.properties.mapped = true;
    zone.newId = $data.properties.ID;
    zone.geometry = $data.geometry;
    $scope.drawMap( {
      "type": "FeatureCollection",
      "features": $scope.features
    } );
  };

  $scope.search = function(features, search_string){


    if(search_string === undefined || search_string === ''){
      return features;
    }
    var array = [];

    angular.forEach(features, function (feature){
      if(feature.properties.ADM1 !== undefined && feature.properties.ADM1.indexOf(search_string) >= 0)
        array.push(feature);
      if(feature.properties.ADM2 !== undefined && feature.properties.ADM2.indexOf(search_string)>= 0)
        array.push(feature);
      if(feature.properties.ADM3 !== undefined && feature.properties.ADM3.indexOf(search_string)>= 0)
        array.push(feature);
    });
    return array;

  };

  $scope.save = function () {

    // convert the format of the submission to the DTO format
    var features = [];
    angular.forEach($scope.geographicZones, function(obj){
        if(obj.mapped){
          features.push({zoneId: obj.id, geoJsonId : obj.newId, geometry: JSON.stringify(obj.geometry) });
        }
    });

    SaveGeographicInfo.post({features: features},function(){
        $scope.message = "Your GIS Mappings have been saved!";
    });
  };

  initiateMap($scope);
}

