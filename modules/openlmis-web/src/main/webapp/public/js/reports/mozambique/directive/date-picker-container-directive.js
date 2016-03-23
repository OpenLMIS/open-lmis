function datePickerContainer(){
    return {
        restrict: 'E',
        controller: 'DatePickerContainerController',
        scope: {
            showIncompleteWarning: '@',
            getTimeRange: '&'
        },
        templateUrl:'date-picker-container.html'
    }
}