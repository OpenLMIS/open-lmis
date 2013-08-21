function SupervisoryNodeController($scope, ReportFacilityTypes, $routeParams, $location, SupervisoryNodeCompleteList, SaveSupervisoryNode, GetSupervisoryNode, GeographicZoneCompleteList, GetFacilityCompleteList) {
    $scope.geographicZoneNameInvalid = false;
    $scope.supervisoryNode = {};
    $scope.facilities = {};
    $scope.geographicZones = {};
    $scope.message={};

    if ($routeParams.supervisoryNodeId) {
        GetSupervisoryNode.get({id: $routeParams.supervisoryNodeId}, function (data) {
            $scope.supervisoryNode = data.supervisoryNode;
        }, {});
    }


    SupervisoryNodeCompleteList.get(function (data) {
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

    $scope.saveSupervisoryNode = function () {
        var successHandler = function (response) {
            $scope.supervisoryNode = response.supervisoryNode;
            $scope.showError = false;
            $scope.error = "";
            $scope.$parent.message = response.success;
            $scope.$parent.supervisoryNodeId = $scope.supervisoryNode.id;
            $location.path('');
        };

        var errorHandler = function (response) {
            $scope.showError = true;
            $scope.error = response.data.error;
        };

        SaveSupervisoryNode.save($scope.supervisoryNode,successHandler,errorHandler);

        return true;
    };

    $scope.saveSupervisoryNodeMember=function(){
        $scope.closeModal();
        return true;
    };

    $scope.validateSupervisoryNodeName = function () {
        $scope.supervisoryNodeNameInvalid = $scope.supervisoryNode.name == null;
    };

    $scope.associateFacility=function(){
        $scope.allFacilitiesFiltered = $scope.allFacilities;
        $scope.supervisoryNodeMemberModal = true;
    };

    $scope.closeModal=function(){
        $scope.geographicZone = null;
        $scope.supervisoryNodeMember = null;
        $scope.facilityType = null;
        $scope.allFacilitiesFiltered = null;
        $scope.supervisoryNodeMemberModal = false;
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
}

