/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

function DistributionController($scope, $rootScope, deliveryZones, DeliveryZoneActivePrograms, messageService, DeliveryZoneProgramPeriods, navigateBackService, $http, $dialog, $location, distributionService) {
  $scope.deliveryZones = deliveryZones;
  var DELIVERY_ZONE_LABEL = messageService.get('label.select.deliveryZone');
  var NONE_ASSIGNED_LABEL = messageService.get('label.noneAssigned');
  var DEFAULT_PROGRAM_MESSAGE = messageService.get('label.select.program');
  var DEFAULT_PERIOD_MESSAGE = messageService.get('label.select.period');

  $scope.zonePlaceholder = !!$scope.deliveryZones.length ? DELIVERY_ZONE_LABEL : NONE_ASSIGNED_LABEL;

  $scope.reload = function() {
    window.location.reload();
  };

  $scope.close = function() {
    $rootScope.appCacheState = undefined;
  };

  $scope.loadPrograms = function () {
    $scope.programs = $scope.periods = [];
    DeliveryZoneActivePrograms.get({zoneId: $scope.selectedZone.id}, function (data) {
      $scope.programs = data.deliveryZonePrograms;
      if ($scope.selectedProgram && $scope.fromBackNavigation) {
        $scope.selectedProgram = _.where($scope.programs, {id: $scope.selectedProgram.id})[0];
        $scope.loadPeriods();
      }
    }, function (data) {
      $scope.error = data.data.error;
    });
  };

  $scope.loadPeriods = function () {
    $scope.periods = [];
    DeliveryZoneProgramPeriods.get({zoneId: $scope.selectedZone.id, programId: $scope.selectedProgram.id}, function (data) {
      $scope.periods = data.periods.length ? data.periods.slice(0, 13) : [];
      if ($scope.selectedPeriod && $scope.fromBackNavigation) {
        $scope.fromBackNavigation = false;
        $scope.selectedPeriod = _.where($scope.periods, {id: $scope.selectedPeriod.id})[0];
      } else {
        $scope.selectedPeriod = $scope.periods.length ? $scope.periods[0] : NONE_ASSIGNED_LABEL;
      }
    }, function (data) {
      $scope.error = data.data.error;
    });
  };

  $scope.programOptionMessage = function () {
    return optionMessage($scope.programs, DEFAULT_PROGRAM_MESSAGE);
  };

  $scope.periodOptionMessage = function () {
    return optionMessage($scope.periods, DEFAULT_PERIOD_MESSAGE);
  };

  function confirmCaching(data, message) {
    var dialogOpts = {
      id: "distributionInitiated",
      header: 'label.distribution.initiated',
      body: data.message
    };
    OpenLmisDialog.newDialog(dialogOpts, callback, $dialog);

    function callback(result) {
      if (result) {
        distributionService.save(data.distribution);
        $scope.message = message;
      }
    }
  }

  $scope.initiateDistribution = function () {
    var distribution = {deliveryZone: $scope.selectedZone, program: $scope.selectedProgram, period: $scope.selectedPeriod};

    if (distributionService.isCached(distribution)) {
      $scope.message = messageService.get("message.distribution.already.cached", $scope.selectedZone.name,
          $scope.selectedProgram.name, $scope.selectedPeriod.name);
      return;
    }

    $http.post('/distributions.json', distribution).success(onInitSuccess).error(onInitFailure);

    function onInitFailure(data) {
        $scope.message = data.error;
    }

    function onInitSuccess(data, status) {
      var message = data.success;
      distribution = data.distribution;

      if (!distribution.facilityDistributions) {
        $scope.message = messageService.get("message.no.facility.available", $scope.selectedProgram.name,
            $scope.selectedZone.name);
        return;
      }
      if (status === 200) {
        confirmCaching(data, message);
        return;
      }

      distributionService.save(distribution);
      $scope.message = message;
    }
  };

  var optionMessage = function (entity, defaultMessage) {
    return utils.isEmpty(entity) ? NONE_ASSIGNED_LABEL : defaultMessage;
  };

  $scope.viewLoadAmount = function () {
    var data = {
      deliveryZone: $scope.selectedZone,
      program: $scope.selectedProgram,
      period: $scope.selectedPeriod
    };
    navigateBackService.setData(data);
    var path = "/view-load-amounts/".concat($scope.selectedZone.id).concat("/")
        .concat($scope.selectedProgram.id).concat("/").concat($scope.selectedPeriod.id);
    $location.path(path);
  };
}

DistributionController.resolve = {

  deliveryZones: function (Locales, UserDeliveryZones, $timeout, $q, $window) {
    var deferred = $q.defer();
    $timeout(function () {
      Locales.get({}, function (data) {
        if (!data.locales) {
          $window.location = "/public/pages/logistics/distribution/offline.html#/list";
          $q.reject();
          return;
        }
        UserDeliveryZones.get({}, function (data) {
          deferred.resolve(data.deliveryZones);
        });
      });
    }, 100);
    return deferred.promise;
  }
};

