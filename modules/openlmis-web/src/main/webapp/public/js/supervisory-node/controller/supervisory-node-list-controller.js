function SupervisoryNodeListController($scope, $location, navigateBackService, SupervisoryNodeCompleteList) {
    $scope.$on('$viewContentLoaded', function () {
        $scope.$apply($scope.query = navigateBackService.query);
        $scope.showSupervisoryNodesList('txtFilterSupervisoryNodes');
    });
    $scope.previousQuery = '';

    $scope.showSupervisoryNodesList = function (id) {

        SupervisoryNodeCompleteList.get(function(data){
            $scope.filteredSupervisoryNodes = data.supervisoryNodes;
            $scope.supervisoryNodesList = $scope.filteredSupervisoryNodes;
        });

        var query = document.getElementById(id).value;
        $scope.query = query;

        filterSupervisoryNodesByName(query);
        return true;
    };

    $scope.editSupervisoryNode = function (id) {
        var data = {query: $scope.query};
        navigateBackService.setData(data);
        $location.path('edit/' + id);
    };


    $scope.clearSearch = function () {
        $scope.query = "";
        $scope.resultCount = 0;
        angular.element("#txtFilterSupervisoryNodes").focus();
    };

    var filterSupervisoryNodesByName = function (query) {
        query = query || "";

        if (query.length == 0) {
            $scope.filteredSupervisoryNodes = $scope.supervisoryNodesList;
        }
        else {
            $scope.filteredSupervisoryNodes = [];
            angular.forEach($scope.supervisoryNodesList, function (supervisoryNode) {

                if (supervisoryNode.name.toLowerCase().indexOf(query.trim().toLowerCase()) >= 0) {
                    $scope.filteredSupervisoryNodes.push(supervisoryNode);
                }
            });
            $scope.resultCount = $scope.filteredSupervisoryNodes.length;
        }
    };

    $scope.filterSupervisoryNodes = function (id) {
        var query = document.getElementById(id).value;
        $scope.query = query;
        filterSupervisoryNodesByName(query);
    };
}