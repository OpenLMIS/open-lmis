function SupervisoryNodeListController($scope, $location, navigateBackService, SupervisoryNodeCompleteList, $dialog, messageService, RemoveSupervisoryNode) {
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

    $scope.showRemoveSupervisoryNodeMemberConfirmDialog = function (index) {
        var supervisoryNode = $scope.filteredSupervisoryNodes[index];
        $scope.index = index;
        $scope.selectedSupervisoryNode = supervisoryNode;
        var options = {
            id: "removeSupervisoryNodeMemberConfirmDialog",
            header: "Confirmation",
            body: "Are you sure you want to remove the supervisory node: " + $scope.selectedSupervisoryNode.name
        };
        OpenLmisDialog.newDialog(options, $scope.removeSupervisoryNodeMemberConfirm, $dialog, messageService);
    };

    $scope.removeSupervisoryNodeMemberConfirm = function (result) {
        if (result) {
            $scope.filteredSupervisoryNodes.splice($scope.index,1);
            $scope.removeSupervisoryNode($scope.selectedSupervisoryNode.id);
            $scope.showSupervisoryNodesList('txtFilterSupervisoryNodes');
        }
        $scope.selectedSupervisoryNode = undefined;
    };

    $scope.removeSupervisoryNode = function(id){
        RemoveSupervisoryNode.get({id: id});
    };
}