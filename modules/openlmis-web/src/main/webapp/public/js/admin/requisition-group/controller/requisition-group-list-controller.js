function RequisitionGroupListController($scope, sharedSpace, $location, navigateBackService, RequisitionGroupCompleteList) {
    sharedSpace.setShouldReloadTheList(false);
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

    $scope.editRequisitionGroup = function (id, programCount) {
        var data = {query: $scope.query};
        navigateBackService.setData(data);
        sharedSpace.setCountOfPrograms(programCount);
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
            angular.forEach($scope.requisitionGroupsList, function (reqGroup) {

                if (reqGroup.name.toLowerCase().indexOf(query.trim().toLowerCase()) >= 0) {
                    $scope.filteredRequisitionGroups.push(reqGroup);
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

    $scope.$watch('sharedSpace.getShouldReloadTheList()',function(){
        $scope.$apply($scope.query = navigateBackService.query);
        $scope.showRequisitionGroupsList('txtFilterRequisitionGroups');
    })
}