/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

function ViewColdChainStatusController($scope, facilities, period, deliveryZone, fridges) {
  if (!isUndefined(facilities) && facilities.length > 0) {
    $scope.message = "";
    $scope.program = facilities[0].supportedPrograms[0].program;
    $scope.period = period;
    $scope.deliveryZone = deliveryZone;

    if (isUndefined(fridges)) {
      $scope.apimessage = "message.api.error";
      $scope.apiError = true;
    } else {
        if (!isUndefined(fridges.fridges) && fridges.fridges.length > 0) {
            $scope.data = fridges.fridges;
        } else {
            $scope.message = "label.no.cold.chain.status.information";
        }
    }
    $scope.facilities = facilities;
  } else {
    $scope.message = "msg.delivery.zone.no.record";
  }

    $scope.getCommonTableHeaders = function () {
      return ['label.district', 'label.facility', 'label.fridge.id'];
    };

    $scope.splitFridgesByStatus = function() {
      $scope.noDataRefrigerators = [];
      $scope.followUpRefrigerators = [];
      $scope.failedRefrigerators = [];
      $scope.workingRefrigerators = [];

       for (var i = 0; i < $scope.data.length; i++) {
          var fridge = $scope.data[i];
          switch (fridge.Status) {
            case 1:
                $scope.failedRefrigerators.push(fridge);
                break;
            case 2:
                $scope.followUpRefrigerators.push(fridge);
                break;
            case 3:
                $scope.workingRefrigerators.push(fridge);
                break;
            case 4:
                $scope.noDataRefrigerators.push(fridge);
                break;
          }
       }
     };

    if (!isUndefined($scope.data)) {
        $scope.splitFridgesByStatus();
    }

    $scope.getDaysFromMinutes = function(minutes) {
        return Math.round(minutes * 2 / 60 / 24) / 2;
    };

    $scope.getFacilityById = function(id) {
        if (!isUndefined(id)) {
            for (var i = 0; i < $scope.facilities.length; i++) {
                var facility = $scope.facilities[i];
                if (facility.id == id) {
                    return facility;
                }
            }
        }
        return null;
    };

    $scope.getFacilityNameById = function(id) {
        if (!isUndefined(id)) {
            var facility = $scope.getFacilityById(id);
            if (!isUndefined(facility)) {
                return facility.name;
            }
        }
        return "No facitility ID";
    };

    $scope.getDistrictNameByFacilityId = function(id) {
        if (!isUndefined(id)) {
            var facility = $scope.getFacilityById(id);
            if (!isUndefined(facility)) {
                return facility.geographicZone.name;
            }
        }
        return "No facitility ID";
    };

    $scope.getProvinceNameByFacilityId = function(id) {
        if (!isUndefined(id)) {
            var facility = $scope.getFacilityById(id);
            if (!isUndefined(facility)) {
                return facility.geographicZone.parent.name;
            }
        }
        return "No facitility ID";
    };
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
  },

  fridges: function (Fridges, $route, $timeout, $q) {
      var deferred = $q.defer();
      $timeout(function () {
        Fridges.get({deliveryZoneId: $route.current.params.deliveryZoneId}, function (data) {
          deferred.resolve(data.coldTraceData);
        }, {});
      }, 100);

      return deferred.promise;
    }

};
