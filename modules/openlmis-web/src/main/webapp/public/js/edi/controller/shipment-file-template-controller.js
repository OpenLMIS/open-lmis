/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

function ShipmentFileTemplateController($scope, shipmentFileTemplate, ShipmentFileTemplate, dateFormats, $location, messageService) {

  $scope.shipmentFileTemplate = shipmentFileTemplate;
  $scope.dateFormats = _.pluck(_.where(dateFormats, {"orderDate": true}), "format");

  function isDuplicatePosition() {
    var positionList = _.pluck(_.where($scope.shipmentFileTemplate.columns, {"include": true}), "position");
    var uniquePositionList = _.uniq(positionList);
    if (uniquePositionList.length != positionList.length) {
      $scope.message = "";
      $scope.error = "file.duplicate.position";
      return true;
    }
    return false;
  }

  function isInvalidPosition() {
    var emptyPosition = false;
    angular.forEach($scope.shipmentFileTemplate.columns, function (column) {

      if (column.include && (isUndefined(column.position) || parseInt(column.position, 10) === 0 )) {
        $scope.message = "";
        $scope.error = "file.invalid.position";
        emptyPosition = true;
      } else if (!isUndefined(column.position)) {
        column.position = parseInt(column.position, 10);
      }
    });

    return emptyPosition;
  }

  $scope.cancelEdiSave = function () {
    $scope.$parent.message = "";
    $location.path('configure-system-settings');
  };

  $scope.saveShipmentFileTemplate = function () {
    if (isInvalidPosition() || isDuplicatePosition()) {
      return;
    }

    ShipmentFileTemplate.save({}, $scope.shipmentFileTemplate, function (data) {
      $scope.error = "";
      $scope.$parent.message = messageService.get(data.success);
      $location.path('configure-system-settings');
    });
  };

}

ShipmentFileTemplateController.resolve = {
  shipmentFileTemplate: function ($q, $timeout, ShipmentFileTemplate) {
    var deferred = $q.defer();
    $timeout(function () {
      ShipmentFileTemplate.get({}, function (data) {
        deferred.resolve(data.shipment_template);
      }, {});
    }, 100);
    return deferred.promise;
  },

  dateFormats: function ($q, $timeout, DateFormats) {
    var deferred = $q.defer();
    $timeout(function () {
      DateFormats.get({}, function (data) {
        deferred.resolve(data.dateFormats);
      }, {});
    }, 100);
    return deferred.promise;
  }
};
