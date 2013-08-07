function RequisitionGroupListController($scope, $location, navigateBackService, RequisitionGroupCompleteList, RemoveRequisitionGroup,$dialog, messageService) {
    $scope.$on('$viewContentLoaded', function () {
        $scope.$apply($scope.query = navigateBackService.query);
        $scope.showRequisitionGroupsList('txtFilterRequisitionGroups');
    });
    $scope.previousQuery = '';

    $scope.showRequisitionGroupsList = function (id) {

        RequisitionGroupCompleteList.get(function (data) {
            $scope.filteredRequisitionGroups = data.requisitionGroups;
            $scope.requisitionGroupsList = $scope.filteredRequisitionGroups;
        });

        var query = document.getElementById(id).value;
        $scope.query = query;

        filterRequisitionGroupsByName(query);
        return true;
    };

    $scope.editRequisitionGroup = function (id) {
        var data = {query: $scope.query};
        navigateBackService.setData(data);
        $location.path('edit/' + id);
    };


    $scope.clearSearch = function () {
        $scope.query = "";
        $scope.resultCount = 0;
        angular.element("#txtFilterRequisitionGroups").focus();
    };

    var filterRequisitionGroupsByName = function (query) {
        query = query || "";

        if (query.length == 0) {
            $scope.filteredRequisitionGroups = $scope.requisitionGroupsList;
        }
        else {
            $scope.filteredRequisitionGroups = [];
            angular.forEach($scope.requisitionGroupsList, function (geographicZone) {

                if (geographicZone.name.toLowerCase().indexOf(query.trim().toLowerCase()) >= 0) {
                    $scope.filteredRequisitionGroups.push(geographicZone);
                }
            });
            $scope.resultCount = $scope.filteredRequisitionGroups.length;
        }
    };

    $scope.filterRequisitionGroups = function (id) {
        var query = document.getElementById(id).value;
        $scope.query = query;
        filterRequisitionGroupsByName(query);
    };


    $scope.showRemoveRequisitionGroupMemberConfirmDialog = function (index) {
        var requisitionGroup = $scope.filteredRequisitionGroups[index];
        $scope.index = index;
        $scope.selectedRequisitionGroup = requisitionGroup;
        var options = {
            id: "removeRequisitionGroupMemberConfirmDialog",
            header: "Confirmation",
            body: "Are you sure you want to remove the requisition group: " + $scope.selectedRequisitionGroup.name
        };
        OpenLmisDialog.newDialog(options, $scope.removeRequisitionGroupMemberConfirm, $dialog, messageService);
    };

    $scope.removeRequisitionGroupMemberConfirm = function (result) {
        if (result) {
            $scope.filteredRequisitionGroups.splice($scope.index,1);
            $scope.removeRequisitionGroup($scope.selectedRequisitionGroup.id);
            $scope.showRequisitionGroupsList('txtFilterRequisitionGroups');
        }
        $scope.selectedRequisitionGroup = undefined;
    };

    $scope.removeRequisitionGroup = function(id){
        RemoveRequisitionGroup.get({id: id});
    };
    
    
    
    
}