/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */


function DistributionController(DeliveryZoneFacilities, Refrigerators, deliveryZones, DeliveryZoneActivePrograms, messageService, DeliveryZoneProgramPeriods, IndexedDB, navigateBackService, $http, $dialog, $scope, $location, SharedDistributions, $q) {
  $scope.deliveryZones = deliveryZones;
  var DELIVERY_ZONE_LABEL = messageService.get('label.select.deliveryZone');
  var NONE_ASSIGNED_LABEL = messageService.get('label.noneAssigned');
  var DEFAULT_PROGRAM_MESSAGE = messageService.get('label.select.program');
  var DEFAULT_PERIOD_MESSAGE = messageService.get('label.select.period');

  $scope.zonePlaceholder = !!$scope.deliveryZones.length ? DELIVERY_ZONE_LABEL : NONE_ASSIGNED_LABEL;

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

  $scope.initiateDistribution = function () {

    function isCached() {
      return !!_.find(SharedDistributions.distributionList, function (distribution) {
        return distribution.deliveryZone.id == $scope.selectedZone.id &&
          distribution.program.id == $scope.selectedProgram.id &&
          distribution.period.id == $scope.selectedPeriod.id;
      });
    }

    if (isCached()) {
      $scope.message = messageService.get("message.distribution.already.cached",
        $scope.selectedZone.name, $scope.selectedProgram.name, $scope.selectedPeriod.name);
      return;
    }

    var distributionDefer = $q.defer();

    cacheDistribution();

    function cacheDistribution() {

      $scope.distributionInitiatedCallback = function (result) {
        if (result) {
          distributionDefer.resolve(distribution);
        } else {
          distributionDefer.reject();
        }
      };

      var distribution = new Distribution($scope.selectedZone, $scope.selectedProgram, $scope.selectedPeriod);

      $http.post('/distributions.json', distribution).success(onInitSuccess);

      function onInitSuccess(data, status) {
        if (status == 201) {
          $scope.message = data.success;
          distributionDefer.resolve(data.distribution);
        } else {
          distribution = data.distribution;
          var dialogOpts = {
            id: "distributionInitiated",
            header: messageService.get('label.distribution.initiated'),
            body: data.success
          };
          OpenLmisDialog.newDialog(dialogOpts, $scope.distributionInitiatedCallback, $dialog, messageService);
        }
      }
    }

    var referenceDataDefer = $q.defer();

    DeliveryZoneFacilities.get({"programId": $scope.selectedProgram.id, "deliveryZoneId": $scope.selectedZone.id }, onDeliveryZoneFacilitiesGetSuccess, {});

    function onDeliveryZoneFacilitiesGetSuccess(data) {
      if (data.facilities.length > 0) {
        var referenceData = {facilities: data.facilities};
        Refrigerators.get({"deliveryZoneId": $scope.selectedZone.id, "programId": $scope.selectedProgram.id}, function (data) {
          referenceData["refrigerators"] = data.refrigerators;
          referenceDataDefer.resolve(referenceData);
        }, {});
      } else {
        referenceDataDefer.reject();
        $scope.message = messageService.get("message.no.facility.available", $scope.selectedProgram.name,
          $scope.selectedZone.name);
      }
    }

    function prepareDistribution(distribution, referenceData) {
      distribution.facilityDistributionData = {};
      $(referenceData.facilities).each(function (index, facility) {
        var refrigeratorReadings = [];
        $(_.where(referenceData.refrigerators, {facilityId: facility.id})).each(function (i, refrigerator) {
          refrigeratorReadings.push({'refrigerator': refrigerator});
        });
        distribution.facilityDistributionData[facility.id] = {refrigeratorReadings: refrigeratorReadings};
      });

      return distribution;
    }

    $q.all([distributionDefer.promise, referenceDataDefer.promise]).then(function (resolved) {
      var distribution = resolved[0];
      var referenceData = resolved[1];

      distribution = prepareDistribution(distribution, referenceData);

      IndexedDB.put('distributions', distribution, function () {
      }, {}, function () {
        SharedDistributions.update();
      });

      referenceData.distributionId = distribution.id;
      IndexedDB.put('distributionReferenceData', referenceData, function () {
      }, {});

    });
  };


  var optionMessage = function (entity, defaultMessage) {
    return entity == null || entity.length == 0 ? NONE_ASSIGNED_LABEL : defaultMessage;
  };

  $scope.viewLoadAmount = function () {
    var data = {
      deliveryZone: $scope.selectedZone,
      program: $scope.selectedProgram,
      period: $scope.selectedPeriod
    };
    navigateBackService.setData(data);
    $location.path("/view-load-amounts/" + $scope.selectedZone.id + "/" + +$scope.selectedProgram.id + "/" + $scope.selectedPeriod.id);
  }

}

DistributionController.resolve = {
  deliveryZones: function (UserDeliveryZones, $timeout, $q, $window) {

    if (!navigator.onLine) $window.location = '/public/pages/logistics/distribution/offline.html#/list';

    var deferred = $q.defer();
    $timeout(function () {
      UserDeliveryZones.get({}, function (data) {
        deferred.resolve(data.deliveryZones);
      }, {});
    }, 100);
    return deferred.promise;
  }
};

