/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 *  If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

function ViewLoadAmountController($scope, facilities, period, deliveryZone) {

  $scope.program = facilities[0].supportedPrograms[0].program;
  $scope.period = period;
  $scope.deliveryZone = deliveryZone;
  $scope.geoZoneLevelName = facilities[0].geographicZone.level.name;


  $(facilities).each(function (i, facility) {
    $(facility.supportedPrograms[0].programProducts).each(function (j, product) {
      product.programProductIsa = new ProgramProductISA(product.programProductIsa);
      product.isaAmount = product.overriddenIsa ? product.overriddenIsa : product.programProductIsa.calculate(facility.catchmentPopulation);
      product.isaAmount = product.isaAmount ? product.isaAmount * period.numberOfMonths : 0;
    });

    facility.supportedPrograms[0].programProductMap = _.groupBy(facility.supportedPrograms[0].programProducts, function (programProduct) {
      return programProduct.product.productGroup.name;
    });

    facility.supportedPrograms[0].sortedProductGroup = _.sortBy(_.keys(facility.supportedPrograms[0].programProductMap), function (key) {
      return key;
    });
  });

  $scope.facilityMap = _.groupBy(facilities, function (facility) {
    return facility.geographicZone.name;
  });
  $scope.sortedGeoZoneKeys = _.sortBy(_.keys($scope.facilityMap), function (key) {
    return key;
  });

  console.log(deliveryZone, period, $scope.facilityMap, $scope.sortedGeoZoneKeys)

}

ViewLoadAmountController.resolve = {
  facilities:function (DeliveryZoneFacilities, $route, $timeout, $q) {
    var deferred = $q.defer();
    $timeout(function () {
      DeliveryZoneFacilities.get({deliveryZoneId:$route.current.params.deliveryZoneId, programId:$route.current.params.programId}, function (data) {
        deferred.resolve(data.facilities);
      }, {});
    }, 100);

    return deferred.promise;
  },

  period:function (Period, $route, $timeout, $q) {
    var deferred = $q.defer();
    $timeout(function () {
      Period.get({id:$route.current.params.periodId}, function (data) {
        deferred.resolve(data.period);
      }, {});
    }, 100);

    return deferred.promise;
  },

  deliveryZone:function (DeliveryZone, $route, $timeout, $q) {
    var deferred = $q.defer();
    $timeout(function () {
      DeliveryZone.get({id:$route.current.params.deliveryZoneId}, function (data) {
        deferred.resolve(data.zone);
      }, {});
    }, 100);

    return deferred.promise;
  }

};
