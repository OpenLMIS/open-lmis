/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

function GeographicZonesJsonController($scope, leafletData, FlatGeographicZoneList, GeographicLevels, SaveGeographicInfo) {
  $scope.features = [];

  $scope.importFile = function (event) {

    var reader = new FileReader();
    var fi = document.getElementById('jsonfile');
    reader.onload = function (fi) {
      $scope.json = JSON.parse(fi.target.result);
      theJson = $scope.json;

      $scope.json.features.forEach(function (feature) {
        $scope.features.push(feature);
      });

      $scope.drawMap($scope.json);
      $scope.centerJSON();
    };
    reader.readAsText(fi.files[0]);
  };

  function interpolate(value, count){
    var val =  parseFloat( value ) / parseFloat(count);
    var interpolator = chroma.interpolate.bezier(['green', 'yellow', 'red' ]);
    return interpolator(val).hex();
  }

  $scope.style = function(feature){
    return {
      fillColor: (feature.properties.mapped)?"red":interpolate(feature.properties.ID, $scope.features.length),
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
        resetStyleOnMouseout: true
      }
    });
    $scope.$apply();
  };

  $scope.centerJSON = function () {
    leafletData.getMap().then(function (map) {
      var latlngs = [];
      for (var c = 0; c < $scope.geojson.data.features.length; c++)
        for (var i = 0; i < $scope.geojson.data.features[c].geometry.coordinates.length; i++) {
          var coord = $scope.geojson.data.features[c].geometry.coordinates[i];
          for (var j in coord) {
            var points = coord[j];
            latlngs.push(L.GeoJSON.coordsToLatLng(points));
          }
        }
      map.fitBounds(latlngs);
    });
  };

  GeographicLevels.get(function (data) {
    $scope.geographicLevels = data.geographicLevels;
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
        if (zone.levelId == $scope.level) {
          $scope.geographicZones.push(zone);
        }
      });
    }
  };

  angular.extend($scope, {
    center: {
      lat: 0.0,
      lng: 0.0,
      zoom: 2
    },
    defaults: {
      scrollWheelZoom: false
    }
  });


  $scope.dropSuccessHandler = function ($event, dragged) {
    dragged.properties.mapped = true;
    $scope.drawMap( {
      "type": "FeatureCollection",
      "features": $scope.features
    } );
  };

  $scope.onDrop = function ($event, $data, zone) {
    zone.mapped = true;
    $data.properties.mapped = true;
    zone.newId = $data.properties.ID;
    zone.geometry = $data.geometry;
  };

  $scope.save = function () {

    // convert the format of the submission to the DTO format
    var features = [];
    angular.forEach($scope.geographicZones, function(obj, index){
        if(obj.mapped){
          features.push({zoneId: obj.id, geoJsonId : obj.newId, geometry: JSON.stringify(obj.geometry) });
        }
    });

    SaveGeographicInfo.post({features: features},function(){
        $scope.message = "Your GIS Mappings have been saved!";
    });
  };


}

