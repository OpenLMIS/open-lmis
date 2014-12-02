/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 *   Copyright © 2013 VillageReach
 *
 *   This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *    
 *   This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *   You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */
function VaccineReceiveSearchController($scope,ReceiveVaccines,UserFacilityList,navigateBackService,$location,messageService,UserSupervisedFacilities){
    var isNavigatedBack;

    $scope.distributionBatch = {};

    $scope.origins = [{id:0,name:'France'},{id:1,name:'USA'}];

    $scope.convertStringToCorrectDateFormat = function(stringDate) {
        if (stringDate) {
            return stringDate.split("-").reverse().join("-");
        }
        return null;
    };

    $scope.editReceivedVaccines = function (id) {
        var data = {query: $scope.query};
        navigateBackService.setData(data);
        $location.path('/edit-receive-vaccine/' + id);
    };

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

    $scope.$on('$viewContentLoaded', function () {
        $scope.selectedType = navigateBackService.selectedType || "0";
        $scope.selectedFacilityId = navigateBackService.selectedFacilityId;
        isNavigatedBack = navigateBackService.isNavigatedBack;
        $scope.loadFacilityData($scope.selectedType);

        $scope.$watch('facilities', function () {
            if ($scope.facilities && isNavigatedBack) {
                $scope.selectedFacilityId = navigateBackService.selectedFacilityId;

                isNavigatedBack = false;
            }
        });
    });


    $scope.$watch('selectedFacilityId', function(){
        if(!isUndefined($scope.selectedFacilityId)){
            ReceiveVaccines.get({facilityId:$scope.selectedFacilityId}, function(data){
                $scope.inventoryTransactions = data.receivedVaccines;
            });
        }

    });
    var resetData = function () {
        $scope.selectedFacilityId = null;
        $scope.inventoryTransactions = null;
        $scope.myFacility = null;
        $scope.facilities = null;
        $scope.error = null;
    };


    $scope.loadFacilityData = function (selectedType) {
        isNavigatedBack = isNavigatedBack ? selectedType !== "0" : resetData();

        if (selectedType === "0") { //My facility
            UserFacilityList.get({}, function (data) {
                $scope.facilities = data.facilityList;
                $scope.myFacility = data.facilityList[0];
                if ($scope.myFacility) {
                    $scope.facilityDisplayName = $scope.myFacility.code + '-' + $scope.myFacility.name;
                    $scope.selectedFacilityId = $scope.myFacility.id;
                } else {
                    $scope.facilityDisplayName = messageService.get("label.none.assigned");
                    $scope.programs = null;
                    $scope.selectedProgram = null;
                }
            }, {});
        } else if (selectedType === "1") { // Supervised facility
            UserSupervisedFacilities.get({}, function(data){
                $scope.facilities = data.facilities;
                $scope.selectedFacilityId = null;
                $scope.error = null;
            });
        }
    };
}
