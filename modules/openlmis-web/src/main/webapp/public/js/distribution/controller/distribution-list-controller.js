/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */
function DistributionListController($scope, SharedDistributions, IndexedDB, SyncFacilityDistributionData) {
  var updateSharedDistribution = function () {
    SharedDistributions.update();
    $scope.sharedDistributions = SharedDistributions;
  }
  updateSharedDistribution();
  var COMPLETE = 'is-complete';
  var SYNCED = 'is-synced';


  $scope.message = null;
  $scope.syncDistribution = function (distributionId) {

    var facilitiesSynced = [];
    var facilitySelected;
    var distributionData = _.findWhere(SharedDistributions.distributionList, {id: distributionId});
    var facilities;

    var syncFacility = function () {
      var index = 0;
      $.each(distributionData.facilityDistributionData, function (key, facilityDistributionData) {
        index++;
        var computedStatus = new FacilityDistributionData(facilityDistributionData).computeStatus();
        if (computedStatus === COMPLETE) {
          facilityDistributionData.facilityId = key;
          SyncFacilityDistributionData.update({distributionId: distributionId }, facilityDistributionData,
            function () {
              facilitySelected = _.findWhere(facilities, {id: utils.parseIntWithBaseTen(key)});
              facilitiesSynced.push(facilitySelected.name + "-" + facilitySelected.code);
              facilityDistributionData.status = SYNCED;
              updateStatus(index);
            }, function () {
              updateStatus(index);
            });
        }
      });
      updateStatus(index);
    }

    var updateStatus = function (index) {
      if (index != _.size(distributionData.facilityDistributionData))
        return;
      if (facilitiesSynced.length == 0) {
        $scope.message = "No Facility for the chosen zone program and period is ready to be synced";
      } else {
        var message = "";
        $.each(facilitiesSynced, function (index, facility) {
          message = message + facility + ","
        });
        $scope.message = message + " have been successfully synced";
        IndexedDB.put('distributions', distributionData, null, null, updateSharedDistribution);
      }
    }

    IndexedDB.get('distributionReferenceData', utils.parseIntWithBaseTen(distributionId), function (event) {
      facilities = event.target.result.facilities;
      syncFacility();
    }, {});
  }
};


