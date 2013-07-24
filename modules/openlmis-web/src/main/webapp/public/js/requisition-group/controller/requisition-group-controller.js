function RequisitionGroupController($scope, ReportFacilityTypes, $routeParams, $location, SupervisoryNodes, SaveRequisitionGroup, GetRequisitionGroup, FacilityCompleteListInRequisitionGroup, GeographicZoneCompleteList, GetFacilityCompleteList, SaveRequisitionGroupMember, RemoveRequisitionGroupMember, $dialog, messageService) {
    $scope.geographicZoneNameInvalid = false;
    $scope.requisitionGroup = {};
    $scope.facilities = {};
    $scope.geographicZones = {};
    $scope.message={};

    var loadMemberFacilities = function(){
        FacilityCompleteListInRequisitionGroup.get({id:$routeParams.requisitionGroupId},function(data){
            $scope.facilities = data.facilities;
        })
    };


    if ($routeParams.requisitionGroupId) {
        GetRequisitionGroup.get({id: $routeParams.requisitionGroupId}, function (data) {
            $scope.requisitionGroup = data.requisitionGroup;
        }, {});

        loadMemberFacilities();
    }


    SupervisoryNodes.get(function (data) {
        $scope.supervisoryNodes = data.supervisoryNodes;
    });

    GeographicZoneCompleteList.get(function(data){
        $scope.geographicZones = data.geographicZones;
    });

    GetFacilityCompleteList.get(function(data){
        $scope.allFacilities = $scope.allFacilitiesFiltered = data.facilities;
    });

    $scope.facilityTypes = ReportFacilityTypes.get(function(data){
        $scope.facilityTypes = data.facilityTypes;
    });

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
            $scope.error = response.data.error;
        };

        SaveRequisitionGroup.save($scope.requisitionGroup,successHandler,errorHandler);

        return true;
    };

    $scope.saveRequisitionGroupMember=function(){
        var successHandler = function (response) {
            $scope.requisitionGroupMember = response.requisitionGroupMember;
            $scope.showError = false;
            $scope.error = "";
            $scope.message = response.success;
            $scope.requisitionGroupMemberId = $scope.requisitionGroupMember.id;
        };

        var errorHandler = function (response) {
            $scope.showError = true;
            $scope.error = response.data.error;
        };

        $scope.requisitionGroupMember.requisitionGroup = $scope.requisitionGroup;

        SaveRequisitionGroupMember.save($scope.requisitionGroupMember,successHandler,errorHandler);

        $scope.closeModal();
        loadMemberFacilities();
        return true;
    };

    $scope.validateRequisitionGroupName = function () {
        $scope.requisitionGroupNameInvalid = $scope.requisitionGroup.name == null;
    };

    $scope.addNewMemberFacility=function(){
        $scope.allFacilitiesFiltered = $scope.allFacilities;
        $scope.requisitionGroupMemberModal = true;
    };

    $scope.closeModal=function(){
        $scope.geographicZone = null;
        $scope.requisitionGroupMember = null;
        $scope.facilityType = null;
        $scope.allFacilitiesFiltered = null;
        $scope.requisitionGroupMemberModal = false;
    };

    $scope.filterFacilityList=function(){
        $scope.allFacilitiesFiltered=[];
        if($scope.facilityType == null && $scope.geographicZone == null){
            $scope.allFacilitiesFiltered = $scope.allFacilities;
        }
        else{
            angular.forEach($scope.allFacilities,function(facility){
                if($scope.facilityType!=null){
                    if(facility.facilityType.id == $scope.facilityType.id){
                        $scope.allFacilitiesFiltered.push(facility);
                    }
                }
                else if($scope.geographicZone != null){
                    if(facility.geographicZone.id == $scope.geographicZone.id){
                        $scope.allFacilitiesFiltered.push(facility);
                    }
                }
            });
        }
    };

    $scope.showRemoveRequisitionGroupMemberConfirmDialog = function (index) {
        var memberFacility = $scope.facilities[index];
        $scope.index = index;
        $scope.selectedFacility = memberFacility;
        var options = {
            id: "removeRequisitionGroupMemberConfirmDialog",
            header: "Confirmation",
            body: "Are you sure you want to remove the selected facility"
        };
        OpenLmisDialog.newDialog(options, $scope.removeRequisitionGroupMemberConfirm, $dialog, messageService);
    };

    $scope.removeRequisitionGroupMemberConfirm = function (result) {
        if (result) {
            $scope.facilities.splice($scope.index,1);
            $scope.removeMemberFacility();
        }
        $scope.selectedFacility = undefined;
    };

    $scope.removeMemberFacility = function(){
        /*$scope.requisitionGroupMember.facility = facility;
         $scope.requisitionGroupMember.requisitionGroup = $scope.requisitionGroup;*/
        RemoveRequisitionGroupMember.get({rgId: $scope.requisitionGroup.id, facId: $scope.selectedFacility.id});
    };

}

