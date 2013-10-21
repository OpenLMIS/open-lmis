/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

function DistributionListController($scope, SharedDistributions, SyncFacilityDistributionData, $q, messageService, distributionService, $dialog) {
  var COMPLETE = 'is-complete';
  var SYNCED = 'is-synced';
  var DUPLICATE = 'is-duplicate';

  SharedDistributions.update();

  $scope.sharedDistributions = SharedDistributions;

  $scope.message = null;

  $scope.syncDistribution = function (distributionId) {
    var promises = [];
    var facilitySelected;
    var distributionData = _.findWhere(SharedDistributions.distributionList, {id: distributionId});
    var facilities;

    distributionService.getReferenceData(distributionId, function (referenceData) {
      facilities = referenceData.facilities;
      syncFacilities();
    });

    function syncFacilities() {
      $.each(distributionData.facilityDistributionData, function (facilityId, facilityDistributionData) {
        facilityId = utils.parseIntWithBaseTen(facilityId);
        var computedStatus = facilityDistributionData.computeStatus();
        if (computedStatus !== COMPLETE)  return;

        var defer = $q.defer();
        promises.push(defer.promise);

        SyncFacilityDistributionData.update({id: distributionId, facilityId: facilityId}, facilityDistributionData,
          function () {
            facilityDistributionData.status = SYNCED;
            facilitySelected = _.findWhere(facilities, {id: facilityId});
            defer.resolve({facility: facilitySelected, facilityDistributionData: facilityDistributionData});
          }, function () {
            facilityDistributionData.status = DUPLICATE;
            facilitySelected = _.findWhere(facilities, {id: facilityId});
            defer.resolve({facility: facilitySelected, facilityDistributionData: facilityDistributionData});
          });
      });

      $q.all(promises).then(function (resolves) {
        distributionService.save(distributionData);

        if (!promises.length) {
          $scope.message = 'message.no.facility.synced';
          return;
        }

        var syncedFacilities = _.filter(resolves, function (resolve) {
          return resolve.facilityDistributionData.status === SYNCED;
        });

        if (!syncedFacilities.length) {
          $scope.message = 'error.facility.data.already.synced';
          return;
        }

        var facilities = [];
        $(syncedFacilities).each(function (index, resolve) {
          facilities.push(resolve.facility.code + ' - ' + resolve.facility.name);
        });

        $scope.message = messageService.get("message.facility.synced.successfully", facilities.join(', '));
      });
    }
  };

  $scope.deleteDistribution = function (id) {
    $scope.message = '';
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
}
