/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 *   Copyright © 2013 VillageReach
 *
 *   This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *    
 *   This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *   You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */
function VaccineDistributionSearchController($scope,VaccineDistributionBatches,VaccineDistributionLineItems,navigateBackService,$location,messageService){

    $scope.distributionBatch = {};

    $scope.origins = [{id:0,name:'France'},{id:1,name:'USA'}];

    $scope.vvmStages = [{id:0,name:'Stage1'},{id:1,name:'Stage2'},{id:2,name:'Stage3'}];

    $scope.convertStringToCorrectDateFormat = function(stringDate) {
        if (stringDate) {
            return stringDate.split("-").reverse().join("-");
        }
        return null;
    };

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

    VaccineDistributionLineItems.get({}, function(data){
        $scope.distributionLineItems = data.distributionLineItems;
    });

    $scope.showDistributionBatchSearchResults = function () {
        var query = $scope.query;
        var len = (query === undefined) ? 0 : query.length;
        if (len >= 3) {
            if ($scope.previousQuery.substr(0, 3) === query.substr(0, 3)) {
                $scope.previousQuery = query;
                filterDistributionBatchByDispatchId(query);
                return true;
            }
            $scope.previousQuery = query;
            VaccineDistributionBatches.get({param:$scope.query.substr(0,3)}, function(data){
                $scope.distributionBatches = data.distributionBatches;
                filterDistributionBatchByDispatchId(query);
            },{});
            return true;

        } else {

            VaccineDistributionBatches.get({param:''},function(data){
                $scope.distributionBatches  = data.distributionBatches;
            });
            return false;
        }
    };
    $scope.previousQuery = '';
    $scope.query = navigateBackService.query;
    $scope.showDistributionBatchSearchResults();

    $scope.addReceive = function (distribution) {
        $scope.distribution = distribution;
            $scope.addReceiveModal = true;
    };
    $scope.resetAddReceiveModal = function () {
        $scope.addReceiveModal = false;
        $scope.error = undefined;
        $scope.distribution = undefined;
    };
    $scope.filteredQuantityReceived = [];

    $scope.rowClickedEvent = function(distributionBatch){
        $scope.filteredQuantityReceived = [];
        $scope.quantityReceivedFor = distributionBatch.batchId;
        angular.forEach($scope.distributionLineItems, function(lineItem){
           if(lineItem.distributionBatch.id === distributionBatch.id){
               $scope.filteredQuantityReceived.push(lineItem);
           }
        });
    };

    $scope.saveDistributionLineItem = function(){

        if ($scope.distributionLineItemForm.$error.required) {
            $scope.error = messageService.get("form.error");
            $scope.showError = true;
            return false;
        }

        var successHandler = function (msgKey) {
            $scope.showError = false;
            $scope.error = "";
            $scope.$parent.message = messageService.get(msgKey, $scope.distributionBatch.dispatchId);
            $scope.$parent.quantityReceivedId = $scope.distributionLineItem.id;

            $location.path('');
        };

        var saveSuccessHandler = function (response) {
            $scope.distributionLineItem = response.distributionLineItem;
            successHandler(response.success);
        };

        var updateSuccessHandler = function () {
            successHandler("message.distribution.batch.updated.success");
        };

        var errorHandler = function (response) {
            $scope.showError = true;
            $scope.message = "";
            $scope.error = response.data.error;
        };

        if(!isUndefined($scope.distribution)){
            $scope.qReceived.distributionBatch = $scope.distribution;
        }

        if ($scope.qReceived.id) {
            VaccineDistributionLineItems.update({id:$scope.qReceived.id}, $scope.qReceived, updateSuccessHandler, errorHandler);
        } else {
            VaccineDistributionLineItems.save({}, $scope.qReceived, saveSuccessHandler, errorHandler);
        }

    };
}
