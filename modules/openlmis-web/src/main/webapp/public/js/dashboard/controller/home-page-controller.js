function homePageController($scope, FeatureToggleService) {
    $scope.showDashboard = false;
    $scope.showHomepage = false;
    var viewToggleKey = {key: "homepage.view"};
    FeatureToggleService.get(viewToggleKey, function (result) {
        if (!result.key) {
            $scope.showDashboard = true;
        } else {
            $scope.showHomepage = true;
        }
    });
}
