function VersionReportController($scope, VersionReportService) {
    $scope.$on('$viewContentLoaded', function () {
        $scope.loadUserSummary();
    });

    $scope.loadUserSummary = function () {
        VersionReportService.get(function (data) {
            $scope.appVersions = data.app_versions;
            $scope.sortType = 'userName';
        });
    };
}