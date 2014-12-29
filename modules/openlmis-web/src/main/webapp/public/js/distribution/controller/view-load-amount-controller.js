/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

function ViewLoadAmountController($scope, facilities, period, deliveryZone) {
  if (!isUndefined(facilities) && facilities.length > 0) {
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
        totalForGeoZone = {totalPopulation: "--"};
        $scope.aggregateMap[facility.geographicZone.name] = totalForGeoZone;
      }
      var totalPopulation = totalForGeoZone.totalPopulation;
      if (!isNaN(utils.parseIntWithBaseTen(facility.catchmentPopulation))) {
        totalPopulation = calculateTotalForPopulation(facility.catchmentPopulation, totalPopulation);
      } else {
        facility.catchmentPopulation = "--";
      }
      totalForGeoZone.totalPopulation = totalPopulation;

      var programProductsWithISA = [];
      $(facility.supportedPrograms[0].programProducts).each(function (j, programProduct) {
        var programProductWithISA = new ProgramProduct(programProduct);
        programProductWithISA.calculateISA(facility, period);
        programProductsWithISA.push(programProductWithISA);
      });

      facility.supportedPrograms[0].programProducts = programProductsWithISA;

      facility.supportedPrograms[0].programProductMap = ProgramProduct.groupProductsMapByName(facility, otherGroupName);

      facility.supportedPrograms[0].sortedProductGroup = _.sortBy(_.keys(facility.supportedPrograms[0].programProductMap), function (key) {
        return key;
      });

      var totalForProducts = $scope.aggregateMap[facility.geographicZone.name].totalProgramProductsMap;
      if (isUndefined(totalForProducts)) {
        totalForProducts = {};
        $scope.aggregateMap[facility.geographicZone.name].totalProgramProductsMap = totalForProducts;
      }

      $(facility.supportedPrograms[0].sortedProductGroup).each(function (index, productGroup) {
        calculateTotalIsaForEachFacilityGroupedByProductGroup(totalForProducts, productGroup, facility);
      });

      $scope.aggregateMap[facility.geographicZone.name].totalProgramProductsMap = totalForProducts;
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
    $scope.message = "msg.no.records.found";
  }

  $scope.getProgramProducts = function (facility) {
    if (!isUndefined(facility)) {
      var programProducts = [];
      $(facility.supportedPrograms[0].sortedProductGroup).each(function (index, sortedProductGroupKey) {
        programProducts = programProducts.concat(facility.supportedPrograms[0].programProductMap[sortedProductGroupKey]);
      });
      return programProducts;
    }
  };

  $scope.getProgramProductsForAggregateRow = function (geoZoneName, zonesTotal) {
    var programProducts = [];
    if (!zonesTotal) {
      $($scope.aggregateMap[geoZoneName].sortedProductGroup).each(function (index, sortedProductGroupKey) {
        programProducts = programProducts.concat($scope.aggregateMap[geoZoneName].totalProgramProductsMap[sortedProductGroupKey]);
      });
    } else {
      $($scope.aggregateMap[$scope.sortedGeoZoneKeys[0]].sortedProductGroup).each(function (index, sortedProductGroupKey) {
        programProducts = programProducts.concat($scope.zonesTotal.totalProgramProductsMap[sortedProductGroupKey]);
      });
    }
    return programProducts;
  };

  function calculateTotalForPopulation(population, presentTotalPopulation) {
    if (presentTotalPopulation == "--") {
      return  population;
    } else {
      return presentTotalPopulation + population;
    }
  }

  function calculateTotalForGeoZoneParent() {
    $scope.zonesTotal = {totalPopulation: "--", totalProgramProductsMap: {}};
    $($scope.sortedGeoZoneKeys).each(function (i, geoZoneKey) {
      if (!isNaN(utils.parseIntWithBaseTen($scope.aggregateMap[geoZoneKey].totalPopulation))) {
        var population = calculateTotalForPopulation($scope.aggregateMap[geoZoneKey].totalPopulation,
            $scope.zonesTotal.totalPopulation);
        $scope.zonesTotal.totalPopulation = population;
      }
      $($scope.aggregateMap[geoZoneKey].sortedProductGroup).each(function (index, sortedProductGroupKey) {
        var totalForGroup = $scope.zonesTotal.totalProgramProductsMap[sortedProductGroupKey];
        if (isUndefined(totalForGroup)) {
          totalForGroup = [];
        }
        $($scope.aggregateMap[geoZoneKey].totalProgramProductsMap[sortedProductGroupKey]).each(function (index, aggregateProduct) {
          var productTotal = _.find(totalForGroup, function (totalProduct) {
            return totalProduct.code == aggregateProduct.product.code;
          });
          if (productTotal) {
            ProgramProduct.calculateProductIsaTotal(aggregateProduct, productTotal);

          } else {
            totalForGroup.push({code: aggregateProduct.product.code, isaAmount: aggregateProduct.isaAmount});
            $scope.zonesTotal.totalProgramProductsMap[sortedProductGroupKey] = totalForGroup;
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
        ProgramProduct.calculateProductIsaTotal(programProduct, existingTotal);
      } else {
        total.push({product: {code: programProduct.product.code}, isaAmount: programProduct.isaAmount});
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
