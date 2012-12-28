function ScheduleController($scope, AllSchedules) {
    AllSchedules.get({}, function (data) {
        $scope.schedules = data.schedules;
    }, function () {
        $location.path($scope.$parent.sourceUrl);
    });
}