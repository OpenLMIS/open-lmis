/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 *   Copyright © 2013 VillageReach
 *
 *   This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *    
 *   This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *   You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */
function VaccineDistributionSearchController($scope,VaccineDistributionBatches,navigateBackService,$location){

    $scope.distributionBatch = {};

    $scope.origins = [{id:0,name:'France'},{id:1,name:'USA'}];

    $scope.convertStringToCorrectDateFormat = function(stringDate) {
        if (stringDate) {
            return stringDate.split("-").reverse().join("-");
        }
        return null;
    };

    VaccineDistributionBatches.get({},function(data){
        $scope.distributionBatches  = data.distributionBatches;
    });

    $scope.editDistributionBatch = function (id) {
        var data = {query: $scope.query};
        navigateBackService.setData(data);
        $location.path('/edit-distribution-batch/' + id);
    };

    var filterDistributionBatchByDispatchId = function (query) {
        $scope.filteredDistributionBatches = [];
        query = query || "";

        angular.forEach($scope.distributionBatches, function (distributionBatch) {
            if (distributionBatch.dispatchId.toLowerCase().indexOf(query.trim().toLowerCase()) >= 0) {
                $scope.filteredDistributionBatches.push(distributionBatch);
            }
        });
        $scope.resultCount = $scope.filteredDistributionBatches.length;
    };

    $scope.showDistributionBatchSearchResults = function () {
        var query = $scope.query;
        var len = (query === undefined) ? 0 : query.length;
        if (len >= 3) {
                filterDistributionBatchByDispatchId(query);
            return true;
        } else {
            return false;
        }
    };


    $scope.previousQuery = '';
    $scope.query = navigateBackService.query;
    $scope.showDistributionBatchSearchResults();


}
