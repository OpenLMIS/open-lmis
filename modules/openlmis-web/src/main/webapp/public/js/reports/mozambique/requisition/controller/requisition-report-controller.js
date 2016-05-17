function RequisitionReportController($scope, $filter, RequisitionReportService, DateFormatService, messageService) {
    $scope.$on('$viewContentLoaded', function () {
        $scope.loadRequisitions();
    });

    $scope.loadRequisitions = function () {
        var requisitionQueryParameters = {
            startTime:  '2015-09-26 00:00:00',
            endTime: $filter('date')(new Date(), 'yyyy-MM-dd HH:mm:ss')
        };

        RequisitionReportService.get(requisitionQueryParameters, function (data) {
            $scope.requisitions = data.rnr_list;
            setActualEndDateAndStubmittedStatus();
        }, function () {
        });
    };

    var setActualEndDateAndStubmittedStatus = function () {
        _.each($scope.requisitions, function (rnr) {
            if (rnr.actualPeriodEnd === null) {
                rnr.actualPeriodEnd = rnr.schedulePeriodEnd;
            }


            if (rnr.clientSubmittedTime !== null) {
                var FIVE_DAYS = 5 * 24 * 60 * 60 * 1000;
                if (rnr.clientSubmittedTime <= rnr.schedulePeriodEnd + FIVE_DAYS) {
                    rnr.submittedStatus = messageService.get("rnr.report.submitted.status.ontime");
                } else {
                    rnr.submittedStatus = messageService.get("rnr.report.submitted.status.late");
                }
            }
        });
    };
    
    $scope.formatDate = function(date) {
        return DateFormatService.formatDateWithLocale(date);
    };
    
    $scope.isLate = function(status) {
        return messageService.get("rnr.report.submitted.status.late") === status;
    };
}