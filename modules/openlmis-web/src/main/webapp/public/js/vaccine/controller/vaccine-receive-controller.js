/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */
function VaccineReceiveController($scope,$route,$location,messageService,GetDonors,PushProgramProducts,Manufacturers,VaccineDistributionStatus,VaccineStorageByFacility,GeographicZones,ReceiveVaccines,Countries){

    $scope.message = "";

    $scope.selectedStorages = [];
    $scope.batches = [];

    if(!isUndefined($route.current.params.facilityId)){
        $scope.selectedFacilityId = $route.current.params.facilityId;
    }else{
        $scope.selectedFacilityId = $scope.$parent.selectedFacilityId;
    }
    if(!isUndefined($route.current.params.transactionId)){

        $scope.showStatus = true;

        ReceiveVaccines.get({id:$route.current.params.transactionId},function(data){
            $scope.inventoryTransaction = data.receivedVaccine;
            $scope.inventoryTransaction.arrivalDate = $scope.convertStringToCorrectDateFormat($scope.inventoryTransaction.stringArrivalDate);
            $scope.inventoryTransaction.today = $scope.convertStringToCorrectDateFormat($scope.inventoryTransaction.stringTodayDate);

        });

    }else{
        $scope.showStatus = false;
    }

    $scope.convertStringToCorrectDateFormat = function(stringDate) {
        if (stringDate) {
            return stringDate.split("-").reverse().join("-");
        }
        return null;
    };

    Countries.get({param:''}, function(data){
        $scope.origins = data.countriesList;
    });

    VaccineDistributionStatus.get({}, function(data){
       $scope.status = data.status;
        $scope.receivedStatus = [];
       angular.forEach($scope.status, function(status){
           if(!isUndefined(status.transactionType) && status.transactionType.name === "Received"){
               $scope.receivedStatus.push(status);
           }
       });
    });

    Manufacturers.get({}, function (data) {
        $scope.manufacturers = data.manufacturers;
    });
    GetDonors.get({},function(data){
        $scope.donors = data.donors;
    });

    PushProgramProducts.get({}, function(data){
        $scope.products = data.products;

    });

    $scope.zones = [];
    GeographicZones.get({},function(data){
        angular.forEach(data.zones, function(zone){
           if(zone.levelId == 2){
               $scope.zones.push(zone);
           }
        });

        $scope.zones.unshift({id:0, name:'MSD HQ'});
    });

    VaccineStorageByFacility.get({facilityId:$scope.selectedFacilityId}, function(data){
        $scope.storages = data.vaccineStorageList;
        $scope.groupedByStorageTypes = [];
        angular.forEach($scope.storages, function(storage){
            var groupedByTypes =_.filter($scope.storages,function(location){if(location.storageType.id == storage.storageType.id){return location;}});
            if(isUndefined(_.findWhere($scope.groupedByStorageTypes,{storageTypeName:storage.storageType.name}))){
                $scope.groupedByStorageTypes.push({storageTypeName:storage.storageType.name, locations:groupedByTypes});
            }
        });
    });

    $scope.cancelDistributionBatchSave = function () {
        $location.path('#/receive');
    };


    $scope.addBatches = function () {
        $scope.resetAddBatchesModal();
        $scope.addBatchesModal = true;
    };
    $scope.resetAddBatchesModal = function () {
        $scope.error = '';
        $scope.showError = false;
        $scope.addBatchesModal = false;
    };

    $scope.saveDistributionBatch = function(){

        if ($scope.inventoryTransactionForm.$error.required) {
            $scope.error = messageService.get("form.error");
            $scope.showError = true;
            return false;
        }

        var successHandler = function (msgKey) {
            $scope.showError = false;
            $scope.error = "";
            $scope.$parent.message = messageService.get(msgKey);
           // $scope.$parent.inventoryTransactionId = $scope.inventoryTransaction.id;
            $location.path('/');
        };

        var saveSuccessHandler = function (response) {
            $scope.inventoryTransaction = response.receiveVaccine;
            successHandler(response.success);
        };

        var updateSuccessHandler = function () {
            successHandler("Received vaccine updated successfully");
        };

        var errorHandler = function (response) {
            $scope.showError = true;
            $scope.message = "";
            $scope.error = response.data.error;
        };
        $scope.inventoryTransaction.fromFacility = {id:$scope.selectedFacilityId};
        $scope.inventoryTransaction.toFacility = {id:$scope.selectedFacilityId};

        if ($scope.inventoryTransaction.id) {
            ReceiveVaccines.update({id:$scope.inventoryTransaction.id}, $scope.inventoryTransaction, updateSuccessHandler, errorHandler);
        } else {
            ReceiveVaccines.save({}, $scope.inventoryTransaction, saveSuccessHandler, errorHandler);
        }
    };

    $scope.saveBatches = function(){

        if ($scope.addBatchForm.$error.required) {
            $scope.error = messageService.get("form.error");
            $scope.showError = true;
            return false;
        }else{
            $scope.showError = false;
            $scope.error = '';
        }
        if(isUndefined($scope.inventoryTransaction.inventoryBatches)){
            $scope.inventoryTransaction.inventoryBatches = [];
        }
        if(!isUndefined($scope.batch.id)){
            var rejected = _.reject($scope.inventoryTransaction.inventoryBatches, function(batch){
                return batch.id == $scope.batch.id;

            });
            $scope.inventoryTransaction.inventoryBatches = rejected;
            $scope.inventoryTransaction.inventoryBatches.push(getDataFormattedInventoryBatch($scope.batch));
        }else{
            $scope.inventoryTransaction.inventoryBatches.push(getDataFormattedInventoryBatch($scope.batch));
        }
        $scope.batch = undefined;
        $scope.addBatchesModal = undefined;
    };

    var getDataFormattedInventoryBatch = function(batch){
        if(!isUndefined(batch)){
            batch.stringExpiryDate = batch.expiryDate;
            batch.stringProductionDate = batch.productionDate;
        }
        return batch;
    };

    $scope.editBatch = function(batch){
        $scope.batch = batch;
        if(!isUndefined($scope.batch)){
            $scope.batch.expiryDate = $scope.convertStringToCorrectDateFormat($scope.batch.stringExpiryDate);
            $scope.batch.productionDate = $scope.convertStringToCorrectDateFormat($scope.batch.stringProductionDate);
        }
        $scope.addBatches();
    };

    $scope.deleteBatch = function(batch){

    };
}
