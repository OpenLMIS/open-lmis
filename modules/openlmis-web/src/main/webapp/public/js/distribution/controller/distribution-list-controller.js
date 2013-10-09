/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */
function DistributionListController($scope, SharedDistributions, IndexedDB, SyncFacilityDistributionData, $q, messageService) {
  var COMPLETE = 'is-complete';
  var SYNCED = 'is-synced';

  SharedDistributions.update();

  $scope.sharedDistributions = SharedDistributions;

  $scope.message = null;

  $scope.syncDistribution = function (distributionId) {
    var promises = [];
    var facilitySelected;
    var distributionData = _.findWhere(SharedDistributions.distributionList, {id: distributionId});
    var facilities;

    IndexedDB.get('distributionReferenceData', distributionId, function (event) {
      facilities = event.target.result.facilities;
      syncFacilities();
    });

    function syncFacilities() {
      $.each(distributionData.facilityDistributionData, function (facilityId, facilityDistributionData) {
        var computedStatus = facilityDistributionData.computeStatus();
        if (computedStatus != COMPLETE)  return;

        var defer = $q.defer();
        promises.push(defer.promise);

        facilitySelected = _.findWhere(facilities, {id: utils.parseIntWithBaseTen(facilityId)});
        SyncFacilityDistributionData.update({id: distributionId, facilityId: facilityId}, facilityDistributionData,
          function (data) {
            facilityDistributionData.status = SYNCED;
            defer.resolve({facility: facilitySelected, facilityDistributionData: facilityDistributionData});
          }, function (data) {
            defer.resolve({facility: facilitySelected, status: 'error'});
          });
      });

      $q.all(promises).then(function (resolves) {
        if (!promises.length) {
          $scope.message = messageService.get("message.no.facility.synced");
          return;
        }
        $scope.message = '';
        $(resolves).each(function (index, resolve) {
          if (resolve.facilityDistributionData.status != SYNCED) return;
          $scope.message += resolve.facility.name + '-' + resolve.facility.code;
          if (index < resolves.length - 1) $scope.message += ', ';
        });
        $scope.message = messageService.get("message.facility.synced.successfully", $scope.message);
        IndexedDB.put('distributions', distributionData, null, null, $scope.sharedDistributions.update);
      });
    }
  }
};


