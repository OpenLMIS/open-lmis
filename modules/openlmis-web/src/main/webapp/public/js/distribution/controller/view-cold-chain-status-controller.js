/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

function ViewColdChainStatusController($scope, facilities, period, deliveryZone) {
  if (!isUndefined(facilities) && facilities.length > 0) {
    $scope.message = "";
    $scope.program = facilities[0].supportedPrograms[0].program;
    $scope.period = period;
    $scope.deliveryZone = deliveryZone;
  } else {
    $scope.message = "msg.delivery.zone.no.record";
  }
}

ViewColdChainStatusController.resolve = {
  facilities: function (DeliveryZoneFacilities, $route, $timeout, $q) {
    var deferred = $q.defer();
    $timeout(function () {
      DeliveryZoneFacilities.get({deliveryZoneId: $route.current.params.deliveryZoneId, programId: $route.current.params.programId}, function (data) {
        deferred.resolve(data.facilities);
      }, {});
    }, 100);

    return deferred.promise;
  },

  period: function (Period, $route, $timeout, $q) {
    var deferred = $q.defer();
    $timeout(function () {
      Period.get({id: $route.current.params.periodId}, function (data) {
        deferred.resolve(data.period);
      }, {});
    }, 100);

    return deferred.promise;
  },

  deliveryZone: function (DeliveryZone, $route, $timeout, $q) {
    var deferred = $q.defer();
    $timeout(function () {
      DeliveryZone.get({id: $route.current.params.deliveryZoneId}, function (data) {
        deferred.resolve(data.zone);
      }, {});
    }, 100);

    return deferred.promise;
  }

};
