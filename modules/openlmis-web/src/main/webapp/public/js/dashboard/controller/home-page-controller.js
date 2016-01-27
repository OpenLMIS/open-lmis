function homePageController($scope, FeatureToggleService) {
    $scope.h1 = "";
    $scope.showDashboard = false;
    $scope.showHomepage = false;
    var viewToggleKey = {key: "homepage.view"};
    FeatureToggleService.get(viewToggleKey, function (result) {
        console.log(result);
        if (!result.key) {
            $scope.showDashboard = true;
        } else {
            $scope.showHomepage = true;
        }
    });
}
