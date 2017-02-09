function datePickerContainer(){
    return {
        restrict: 'E',
        controller: 'DatePickerContainerController',
        scope: {
            getTimeRange: '&',
            pickerType: '@',
            loadReport: '&'
        },
        templateUrl:'date-picker-container.html'
    };
}