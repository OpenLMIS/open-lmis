function datePickerContainer(){
    return {
        restrict: 'E',
        controller: 'DatePickerContainerController',
        scope: {
            getTimeRange: '&',
            pickerType: '@',
            tagOptions: '@'
        },
        templateUrl:'date-picker-container.html'
    };
}