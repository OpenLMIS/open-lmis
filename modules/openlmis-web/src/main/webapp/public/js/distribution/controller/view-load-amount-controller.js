/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 *  If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

function ViewLoadAmountController($scope, facilities, period, deliveryZone) {
  if (facilities.length > 0) {
    $scope.message = "";
    $scope.program = facilities[0].supportedPrograms[0].program;
    $scope.period = period;
    $scope.deliveryZone = deliveryZone;
    var otherGroupName = "";
    $scope.geoZoneLevelName = facilities[0].geographicZone.level.name;

    $scope.aggregateMap = {};

    $(facilities).each(function (i, facility) {
      var totalForGeoZone = $scope.aggregateMap[facility.geographicZone.name];
      if (isUndefined(totalForGeoZone)) {
        totalForGeoZone = {'totalPopulation':"--"}
        $scope.aggregateMap[facility.geographicZone.name] = totalForGeoZone;
      }
      var totalPopulation = totalForGeoZone['totalPopulation'];
      if (!isNaN(parseInt(facility.catchmentPopulation))) {
        totalPopulation = calculateTotalForPopulation(facility.catchmentPopulation, totalPopulation);
      } else {
        facility.catchmentPopulation = "--";
      }
      totalForGeoZone['totalPopulation'] = totalPopulation;
      $(facility.supportedPrograms[0].programProducts).each(function (j, product) {
        if (isUndefined(product.programProductIsa) && isUndefined(product.overriddenIsa)) {
          product.isaAmount = "--";
        } else {
          product.programProductIsa = new ProgramProductISA(product.programProductIsa);
          product.isaAmount = product.overriddenIsa ? product.overriddenIsa : product.programProductIsa.calculate(facility.catchmentPopulation);
          product.isaAmount = product.isaAmount ? product.isaAmount * period.numberOfMonths : 0;
        }
      });
      facility.supportedPrograms[0].programProductMap = _.groupBy(facility.supportedPrograms[0].programProducts, function (programProduct) {
        return programProduct.product.productGroup ? programProduct.product.productGroup.name : otherGroupName;
      });

      facility.supportedPrograms[0].sortedProductGroup = _.sortBy(_.keys(facility.supportedPrograms[0].programProductMap), function (key) {
        return key;
      });

      var totalForProducts = $scope.aggregateMap[facility.geographicZone.name]['totalProgramProductsMap'];
      if (isUndefined(totalForProducts)) {
        totalForProducts = {};
        $scope.aggregateMap[facility.geographicZone.name]['totalProgramProductsMap'] = totalForProducts;
      }

      $(facility.supportedPrograms[0].sortedProductGroup).each(function (index, productGroup) {
        calculateTotalIsaForEachFacilityGroupedByProductGroup(totalForProducts, productGroup, facility);
      });

      $scope.aggregateMap[facility.geographicZone.name]['totalProgramProductsMap'] = totalForProducts;
      pushBlankProductGroupToLast(facility);

      $scope.aggregateMap[facility.geographicZone.name].sortedProductGroup = facility.supportedPrograms[0].sortedProductGroup;

    });


    $scope.facilityMap = _.groupBy(facilities, function (facility) {
      return facility.geographicZone.name;
    });

    $scope.sortedGeoZoneKeys = _.sortBy(_.keys($scope.facilityMap), function (key) {
      return key;
    });


    $($scope.sortedGeoZoneKeys).each(function (i, geoZoneKey) {
      var totalPopulation = 0;
      var facilities = $scope.facilityMap[geoZoneKey];
      $(facilities).each(function (j, facility) {
        totalPopulation += facility.catchmentPopulation;
      });
    });

    calculateTotalForGeoZoneParent();
  } else {
    $scope.message = "msg.delivery.zone.no.record";
  }

  $scope.getProgramProducts = function (facility) {
    if (!isUndefined(facility)) {
      var programProducts = [];
      $(facility.supportedPrograms[0].sortedProductGroup).each(function (index, sortedProductGroupKey) {
        programProducts = programProducts.concat(facility.supportedPrograms[0].programProductMap[sortedProductGroupKey]);
      });
      return programProducts;
    }
  }

  $scope.getProgramProductsForAggregateRow = function (geoZoneName, zonesTotal) {
    var programProducts = [];
    if (!zonesTotal) {
      $($scope.aggregateMap[geoZoneName].sortedProductGroup).each(function (index, sortedProductGroupKey) {
        programProducts = programProducts.concat($scope.aggregateMap[geoZoneName]['totalProgramProductsMap'][sortedProductGroupKey]);
      });
    } else {
      $($scope.aggregateMap[$scope.sortedGeoZoneKeys[0]].sortedProductGroup).each(function (index, sortedProductGroupKey) {
        programProducts = programProducts.concat($scope.zonesTotal['totalProgramProductsMap'][sortedProductGroupKey]);
      });
    }
    return programProducts;
  }


  function calculateProductIsaTotal(aggregateProduct, productTotal) {
    if (!isNaN(parseInt(aggregateProduct.isaAmount))) {
      if (productTotal.isaAmount == "--") {
        productTotal.isaAmount = aggregateProduct.isaAmount;
      } else {
        productTotal.isaAmount = productTotal.isaAmount + aggregateProduct.isaAmount;
      }
    }
  }

  function calculateTotalForPopulation(population, presentTotalPopulation) {
    if (presentTotalPopulation == "--") {
      return  population;
    } else {
      return presentTotalPopulation + population;
    }
  }

  function calculateTotalForGeoZoneParent() {
    $scope.zonesTotal = {totalPopulation:"--", totalProgramProductsMap:{}};
    $($scope.sortedGeoZoneKeys).each(function (i, geoZoneKey) {
      if (!isNaN(parseInt($scope.aggregateMap[geoZoneKey]['totalPopulation']))) {
        var population = calculateTotalForPopulation($scope.aggregateMap[geoZoneKey]['totalPopulation'], $scope.zonesTotal['totalPopulation']);
        $scope.zonesTotal['totalPopulation'] = population;
      }
      $($scope.aggregateMap[geoZoneKey].sortedProductGroup).each(function (index, sortedProductGroupKey) {
        var totalForGroup = $scope.zonesTotal['totalProgramProductsMap'][sortedProductGroupKey];
        if (isUndefined(totalForGroup)) {
          totalForGroup = [];
        }
        $($scope.aggregateMap[geoZoneKey]['totalProgramProductsMap'][sortedProductGroupKey]).each(function (index, aggregateProduct) {
          var productTotal = _.find(totalForGroup, function (totalProduct) {
            return totalProduct.code == aggregateProduct.product.code;
          });
          if (productTotal) {
            calculateProductIsaTotal(aggregateProduct, productTotal);

          } else {
            totalForGroup.push({code:aggregateProduct.product.code, isaAmount:aggregateProduct.isaAmount});
            $scope.zonesTotal['totalProgramProductsMap'][sortedProductGroupKey] = totalForGroup;
          }
        });
      });
    });
  }

  function calculateTotalIsaForEachFacilityGroupedByProductGroup(totalForProducts, productGroup, facility) {
    var total = totalForProducts[productGroup] || [];
    var products = facility.supportedPrograms[0].programProductMap[productGroup];
    $(products).each(function (index, programProduct) {
      var existingTotal = _.find(total, function (totalProduct) {
        return totalProduct.product.code == programProduct.product.code;
      });

      if (existingTotal) {
        calculateProductIsaTotal(programProduct, existingTotal);
      } else {
        total.push({product:{code:programProduct.product.code}, isaAmount:programProduct.isaAmount});
      }

    });
    totalForProducts[productGroup] = total;
  }


  function pushBlankProductGroupToLast(facility) {
    if (_.indexOf(facility.supportedPrograms[0].sortedProductGroup, otherGroupName) > -1) {
      facility.supportedPrograms[0].sortedProductGroup = _.without(facility.supportedPrograms[0].sortedProductGroup, otherGroupName);
      facility.supportedPrograms[0].sortedProductGroup.push(otherGroupName);
    }
  }

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
