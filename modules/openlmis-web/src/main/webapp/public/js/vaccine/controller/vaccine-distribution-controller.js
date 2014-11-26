/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */
function VaccineDistributionController($scope,distributionBatch,VaccineDistributionBatches,$location,messageService,GetDonors,Products,GetFacilityCompleteList,Manufacturers,DistributionTypes){

    $scope.message = "";
    $scope.convertStringToCorrectDateFormat = function(stringDate) {
        if (stringDate) {
            return stringDate.split("-").reverse().join("-");
        }
        return null;
    };

    $scope.getDistributionBatchWithDateObjects = function(distributionBatch) {
        if(!isUndefined(distributionBatch)){
            distributionBatch.productionDate = $scope.convertStringToCorrectDateFormat(distributionBatch.stringProductionDate);
            distributionBatch.expiryDate = $scope.convertStringToCorrectDateFormat(distributionBatch.stringExpiryDate);
            distributionBatch.receiveDate = $scope.convertStringToCorrectDateFormat(distributionBatch.stringReceiveDate);
            distributionBatch.recallDate = $scope.convertStringToCorrectDateFormat(distributionBatch.stringRecallDate);
        }

        return distributionBatch;
    };

    $scope.distributionBatch = $scope.getDistributionBatchWithDateObjects(distributionBatch);


    $scope.origins = [{id:0,name:'France'},{id:1,name:'USA'}];

    DistributionTypes.get({}, function(data){
        $scope.distributionTypes = data.distributionTypes;
    });
    Manufacturers.get({}, function (data) {
        $scope.manufacturers = data.manufacturers;
    });
    GetDonors.get({},function(data){
        $scope.donors = data.donors;
    });

    Products.get({}, function(data){
        $scope.products = data.productList;
    });

    $scope.cancelDistributionBatchSave = function () {
        $location.path('#/distribution-batch');
    };
    GetFacilityCompleteList.get({},function(data){
        $scope.allFacilities = data.allFacilities;
        $scope.allFacilities.unshift({name:'-- Select Facility --'});
    });


    $scope.saveDistributionBatch = function(){

        if ($scope.distributionBatchForm.$error.required) {
            $scope.error = messageService.get("form.error");
            $scope.showError = true;
            return false;
        }

        var successHandler = function (msgKey) {
            $scope.showError = false;
            $scope.error = "";
            $scope.$parent.message = messageService.get(msgKey, $scope.distributionBatch.batchId);
            $scope.$parent.distributionBatchId = $scope.distributionBatch.id;
            $location.path('');
        };

        var saveSuccessHandler = function (response) {
            $scope.distributionBatch = response.distributionBatch;
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

        if ($scope.distributionBatch.id) {
            VaccineDistributionBatches.update({id:$scope.distributionBatch.id}, $scope.distributionBatch, updateSuccessHandler, errorHandler);
        } else {
            VaccineDistributionBatches.save({}, $scope.distributionBatch, saveSuccessHandler, errorHandler);
        }
    };
}

VaccineDistributionController.resolve = {

    distributionBatch: function ($q, VaccineDistributionBatches, $route, $timeout) {
        var distributionBatchId = $route.current.params.distributionBatchId;
        if (!distributionBatchId) return undefined;
        var deferred = $q.defer();
        $timeout(function () {
            VaccineDistributionBatches.get({id: distributionBatchId}, function (data) {
                deferred.resolve(data.distributionBatch);
            }, function () {
            });
        }, 100);
        return deferred.promise;
    }
};
