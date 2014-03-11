/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

function RequisitionGroupController($scope,sharedSpace, ReportFacilityTypes, $routeParams, $location, SupervisoryNodes, SaveRequisitionGroup, GetRequisitionGroup, FacilityCompleteListInRequisitionGroup, GeographicZoneCompleteList, GetFacilityCompleteList, SaveRequisitionGroupMember, RemoveRequisitionGroupMember, $dialog, messageService, RemoveRequisitionGroup) {
    $scope.geographicZoneNameInvalid = false;
    $scope.requisitionGroup = {};
    $scope.associatedFacilities = {};
    $scope.geographicZones = {};
    $scope.message={};
    $scope.facilitiesLoaded = false;
    $scope.associatedFacilitiesLoaded = false;

    $scope.loadMemberFacilities = function(){
        $scope.associatedFacilitiesLoaded = false;
        FacilityCompleteListInRequisitionGroup.get({id:$routeParams.requisitionGroupId},function(data){
            $scope.associatedFacilities = data.facilities;
            $scope.associatedFacilitiesLoaded = true;
            $scope.filterAssociatedFacilityList();
        });
    };

    if ($routeParams.requisitionGroupId) {
        GetRequisitionGroup.get({id: $routeParams.requisitionGroupId}, function (data) {
            $scope.requisitionGroup = data.requisitionGroup;
        }, {});

        $scope.loadMemberFacilities();
    }


    SupervisoryNodes.get(function (data) {
        $scope.supervisoryNodes = data.supervisoryNodes;
    });

    GeographicZoneCompleteList.get(function(data){
        $scope.geographicZones = data.geographicZones;
    });

    GetFacilityCompleteList.get(function(data){
        $scope.allFacilities = $scope.allFacilitiesFiltered = data.allFacilities;
        $scope.facilitiesLoaded = true;
    });

    $scope.facilityTypes = ReportFacilityTypes.get(function(data){
        $scope.facilityTypes = data.facilityTypes;
    });

    $scope.filterAssociatedFacilityList = function(){
        if(!$scope.associatedFacilitiesLoaded){
            return;
        }
        $scope.associatedfacilitiesFiltered = [];
        if(!$scope.facilityFilterToken){
            $scope.associatedfacilitiesFiltered = $scope.associatedFacilities;
        }
        else{
            angular.forEach($scope.associatedFacilities,function(facility){

                if(facility.name.toLowerCase().indexOf($scope.facilityFilterToken.trim().toLowerCase()) !== -1 ||
                    facility.code.toLowerCase().indexOf($scope.facilityFilterToken.trim().toLowerCase()) !== -1){
                    $scope.associatedfacilitiesFiltered.push(facility);
                }
            });
        }
    };

    $scope.saveRequisitionGroup = function () {
        var successHandler = function (response) {
            $scope.requisitionGroup = response.requisitionGroup;
            $scope.showError = false;
            $scope.error = "";
            $scope.$parent.message = response.success;
            $scope.$parent.requisitionGroupId = $scope.requisitionGroup.id;
            $location.path('');
        };

        var errorHandler = function (response) {
            $scope.showError = true;
            $scope.error = messageService.get(response.data.error);
        };

        SaveRequisitionGroup.save($scope.requisitionGroup,successHandler,errorHandler);

        return true;
    };

    $scope.saveRequisitionGroupMember = function(){

        var successHandler = function (response) {
          $scope.requisitionGroupMember = response.requisitionGroupMember;
          $scope.showError = false;
          $scope.error = "";
          $scope.message = response.success;
          $scope.loadMemberFacilities();
        };

        var errorHandler = function (response) {
            $scope.showError = true;
            $scope.error = messageService.get(response.data.error);
        };

        $scope.requisitionGroupMember.requisitionGroup = $scope.requisitionGroup;

        SaveRequisitionGroupMember.save($scope.requisitionGroupMember,successHandler,errorHandler);
        $scope.closeModal();
        return true;
    };

    $scope.validateRequisitionGroupName = function () {
        $scope.requisitionGroupNameInvalid = $scope.requisitionGroup.name === null || $scope.requisitionGroup.name === "undefined";
    };

    $scope.addNewMemberFacility = function(){
        $scope.requisitionGroupMemberModal = true;
        $scope.requisitionGroupMember.facility = null;
    };

    $scope.closeModal=function(){
        $scope.requisitionGroupMemberModal = false;
    };

    $scope.filterFacilityList = function(){
        $scope.allFacilitiesFiltered = [];
        if(!$scope.facilityType && !$scope.geographicZone){
            $scope.allFacilitiesFiltered = $scope.allFacilities;
        }
        else{
            angular.forEach($scope.allFacilities,function(facility){
                //if both facility type and geographic zone are selected
                if($scope.facilityType && $scope.geographicZone ){
                  if(facility.typeId == $scope.facilityType.id && facility.geographicZoneId == $scope.geographicZone.id){
                    $scope.allFacilitiesFiltered.push(facility);
                  }
                }
                else if($scope.facilityType){
                    if(facility.typeId == $scope.facilityType.id){
                        $scope.allFacilitiesFiltered.push(facility);
                    }
                }
                else if($scope.geographicZone){
                    if(facility.geographicZoneId == $scope.geographicZone.id){
                        $scope.allFacilitiesFiltered.push(facility);
                    }
                }
            });
        }
    };

    $scope.showRemoveRequisitionGroupMemberConfirmDialog = function (index) {
        var memberFacility = $scope.associatedfacilitiesFiltered[index];
        $scope.index = index;
        $scope.selectedFacility = memberFacility;
        var options = {
            id: "removeRequisitionGroupMemberConfirmDialog",
            header: "Confirmation",
            body: "Please confirm that you want to remove the facility: " + $scope.selectedFacility.code + " - " + $scope.selectedFacility.name
        };
        OpenLmisDialog.newDialog(options, $scope.removeRequisitionGroupMemberConfirm, $dialog, messageService);
    };

    $scope.removeRequisitionGroupMemberConfirm = function (result) {
        if (result) {
            $scope.removeMemberFacility();

        }
        else{
            $scope.selectedFacility = undefined;
        }
    };

    $scope.removeMemberFacility = function(){
        RemoveRequisitionGroupMember.get({rgId: $scope.requisitionGroup.id, facId: $scope.selectedFacility.id},function(data){
            $scope.associatedFacilitiesLoaded = false;
            $scope.associatedfacilitiesFiltered.splice($scope.index,1);
            $scope.selectedFacility = undefined;
        },{});
    };


    $scope.showRemoveRequisitionGroupConfirmDialog = function () {
        if($scope.facilities.length > 0) {
            $scope.showError = true;
            $scope.error = "Requisition group is associated with facilities.  Please remove the associated facilities first!";
            return false;
        }

        if(sharedSpace.getCountOfPrograms() > 0){
            $scope.showError = true;
            $scope.error = "Requisition group is associated with programs.  Please remove the associated programs first!";
            return false;
        }

        $scope.selectedRequisitionGroup = $scope.requisitionGroup;
        var options = {
            id: "removeRequisitionGroupMemberConfirmDialog",
            header: "Confirmation",
            body: "Are you sure you want to remove the requisition group: " + $scope.selectedRequisitionGroup.name
        };
        OpenLmisDialog.newDialog(options, $scope.removeRequisitionGroupConfirm, $dialog, messageService);
    };

    $scope.removeRequisitionGroupConfirm = function (result) {
        if (result) {
            $scope.removeRequisitionGroup($scope.selectedRequisitionGroup.id);
            sharedSpace.setShouldReloadTheList(true);
            $scope.$parent.message = "Requisition group: " + $scope.selectedRequisitionGroup.name + " has been successfully removed. ";
            $location.path('#/list');
        }
        $scope.selectedRequisitionGroup = undefined;
    };

    $scope.removeRequisitionGroup = function(id){
        RemoveRequisitionGroup.get({id: id});
    };

}

