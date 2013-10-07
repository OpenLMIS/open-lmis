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
  SharedDistributions.update();
  $scope.sharedDistributions = SharedDistributions;
  var COMPLETE = 'is-complete';
  var SYNCED = 'is-synced';


  $scope.syncDistribution = function (distributionId) {
    var facilitiesSynced = [];
    var facilitySelected;
    var distributionData;
    var facilities;

    var syncFacility = function () {
      $.each(distributionData, function (index, facilityDistributionData) {
        var computedStatus = new FacilityDistributionData(facilityDistributionData).computeStatus();
        if (computedStatus === COMPLETE) {

          SyncFacilityDistributionData.update({distributionId: distributionId },{facilityDistributionData: facilityDistributionData},
            function () {
              facilitySelected = _.findWhere(facilities, {id: utils.parseIntWithBaseTen(facilityDistributionData.key)});
              facilitiesSynced.push(facilitySelected.name + "-" + facilitySelected.code);
              facilityDistributionData.status = SYNCED;
            }, {});
        }
      });
      if (facilitiesSynced.length == 0) {
        $scope.message = "No Facility for the chosen zone program and period is ready to be synced";
      } else {
        var message = "";
        $.each(facilitiesSynced, function (facility) {
          message = message + facility + ","
        });
        $scope.message = message + "have been successfully synced";
      }
    }

    IndexedDB.get('distributionReferenceData', utils.parseIntWithBaseTen(distributionId), function (event) {
      facilities = event.target.result.facilities;
    }, {});

    IndexedDB.get('distributions', utils.parseIntWithBaseTen(distributionId), function (event) {
      distributionData = event.target.result.facilityDistributionData;
      syncFacility();
    }, {});


  }
};


