/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program. If not, see http://www.gnu.org/licenses. For additional information contact info@OpenLMIS.org. 
 */

function DistributionListController($scope, SharedDistributions, SyncFacilityDistributionData, $q, messageService, distributionService, $dialog) {
  $scope.COMPLETE = 'is-complete';
  $scope.SYNCED = 'is-synced';
  $scope.DUPLICATE = 'is-duplicate';
  var SYNC_COMPLETE = 'label.distribution.synchronization.complete';
  var SYNC_IN_PROGRESS = 'label.distribution.synchronization.progress';
  var totalFacilityCount;

  $scope.synchronizationModal = false;

  SharedDistributions.update();

  $scope.sharedDistributions = SharedDistributions;

  $scope.syncDistribution = function (distributionId) {
    $scope.syncMessage = null;
    $scope.syncResult = {};
    $scope.syncProgressHeader = SYNC_IN_PROGRESS;
    $scope.requestInProgress = $scope.synchronizationModal = true;
    var promises = [];

    distributionService.getReferenceData(distributionId, function (referenceData) {
      syncFacilities(referenceData.facilities);
    });

    function syncFacilities(facilities) {
      var synchronizedFacilityCount = $scope.progressValue = totalFacilityCount = 0;

      $.each($scope.distributionData.facilityDistributions, function (facilityId, facilityDistribution) {
        if (facilityDistribution.status !== $scope.COMPLETE)  return;
        ++totalFacilityCount;

        facilityId = utils.parseIntWithBaseTen(facilityId);
        var defer = $q.defer();
        promises.push(defer.promise);

        SyncFacilityDistributionData.update({id: distributionId, facilityId: facilityId}, facilityDistribution,
            function (data) {
              if (data.syncStatus) {
                facilityDistribution.status = $scope.SYNCED;
              } else {
                facilityDistribution.status = $scope.DUPLICATE;
              }
              defer.resolve({facility: _.findWhere(facilities, {id: facilityId}), facilityDistribution: facilityDistribution});
              updateProgressBar();
            }, function () {
              defer.resolve({facility: _.findWhere(facilities, {id: facilityId}), facilityDistribution: facilityDistribution});
              updateProgressBar();
            });
      });

      function updateProgressBar() {
        ++synchronizedFacilityCount;
        $scope.progressValue = (synchronizedFacilityCount / totalFacilityCount) * 100;
      }

      $q.all(promises).then(function (resolves) {
        $scope.requestInProgress = false;
        $scope.syncProgressHeader = SYNC_COMPLETE;
        distributionService.save($scope.distributionData);

        $scope.syncResult = _.groupBy(resolves, function (resolve) {
          return resolve.facilityDistribution.status;
        });
      });
    }
  };

  $scope.deleteDistribution = function (id) {
    $scope.syncMessage = '';
    var dialogOpts = {
      id: "distributionInitiated",
      header: messageService.get('label.delete.distribution.header'),
      body: messageService.get('label.confirm.delete.distribution')
    };

    var callback = function () {
      return function (result) {
        if (!result) return;
        distributionService.deleteDistribution(id);
      };
    };

    OpenLmisDialog.newDialog(dialogOpts, callback(), $dialog, messageService);
  };


  $scope.showConfirmDistributionSync = function (distributionId) {
    $scope.distributionData = _.findWhere(SharedDistributions.distributionList, {id: distributionId});

    var facilityDataToSync = _.filter($scope.distributionData.facilityDistributions, function (facilityDistribution) {
      return facilityDistribution.computeStatus() === $scope.COMPLETE;
    });

    if (!facilityDataToSync.length) {
      $scope.syncMessage = 'message.no.facility.synced';
      return;
    }

    function syncDistributionCallBack(distributionId) {
      return function (result) {
        if (!result) return;
        $scope.syncDistribution(distributionId);
      };
    }

    var dialogOpts = {
      id: "syncDistributionDialog",
      header: messageService.get('sync.distribution.header'),
      body: messageService.get('sync.distribution.confirm')
    };
    OpenLmisDialog.newDialog(dialogOpts, syncDistributionCallBack(distributionId), $dialog, messageService);
  };

}
