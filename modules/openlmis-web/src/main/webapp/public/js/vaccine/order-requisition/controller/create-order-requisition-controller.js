function CreateVaccineOrderRequisition($scope, $dialog, $window, $routeParams, report, VaccineOrderRequisitionColumns, VaccineOrderRequisitionSubmit, $location) {

    $scope.report = new VaccineOrderRequisition(report);

    $scope.selectedType = 0;

    $scope.productFormChange = function(){
        $scope.selectedType = 0;
        $scope.report = new VaccineOrderRequisition(report);

    };

    $scope.productFormChange1 = function(){
        $scope.selectedType = 1;

        $scope.report = new VaccineOrderRequisition2(report);

    };

    VaccineOrderRequisitionColumns.get({}, function (data) {
        $scope.columns = data.columns;
    });

    $scope.print = function (type) {
        var params = jQuery.param($routeParams);

        VaccineOrderRequisitionSubmit.update($scope.report, function () {
                });
        var url = '/reports/download/order_requisition_export/' + type + '?' + params;
        $window.open(url, '_blank');

    };

    $scope.submit = function () {
        var callBack = function (result) {
            if (result) {
                VaccineOrderRequisitionSubmit.update($scope.report, function () {
                    $scope.message = "label.form.Submitted.Successfully";
                    $location.path('/initiate');
                });
            }
        };
        var options = {
            id: "confirmDialog",
            header: "label.confirm.order.submit.action",
            body: "msg.question.submit.order.confirmation"
        };
        OpenLmisDialog.newDialog(options, callBack, $dialog);
    };
}

CreateVaccineOrderRequisition.resolve = {

    report: function ($q, $timeout, $route, VaccineOrderRequisitionReport) {
        var deferred = $q.defer();
        $timeout(function () {
            VaccineOrderRequisitionReport.get({id: $route.current.params.id}, function (data) {
                deferred.resolve(data.report);
            });
        }, 100);
        return deferred.promise;
    }

};