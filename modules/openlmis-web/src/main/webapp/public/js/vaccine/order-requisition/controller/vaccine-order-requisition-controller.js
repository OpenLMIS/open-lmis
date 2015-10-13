function VaccineOrderRequisitionController($scope,VaccineReport,VaccineOrderRequisitionColumns,VaccineOrderRequisitionSubmit,$location,$dialog,StockCardsForProgramByCategory, LoggedInUserDetails, $routeParams, ProgramForUserHomeFacility, UserFacilityList, $q) {

    var loadStockCards = function(programId, facilityId){

        StockCardsForProgramByCategory.get(programId ,facilityId).then(function(data){

            $scope.report = data;

            var log  = $scope.report[0];
            $scope.report2 = new VaccineOrderRequisition(log);

            console.log($scope.report2);
            if( $scope.report[0] !== undefined){
                $scope.data = {"stockcards": $scope.report[0].stockCards};

            }

        });



    };

    $scope.isCategoryDifferentFromPreviousLineItem = function (index) {
        return !((index > 0 ) && ($scope.report2[index].productCategory == $scope.report2[index - 1].productCategory));
    };


    loadStockCards(parseInt(82,10),parseInt(19077,10));


    $scope.selectedType = 0;

    $scope.loggedInUser = {};
    $scope.userFacilities = $scope.users = [];
    $scope.pageLineItems = [];

    /* $scope.columns = [
        {label: "label.product", name: "product"},
        {label: "header.order.requisition.maximum.stock", name: "maximumStock"},
        {label: "header.order.requisition.reorder.level", name: "reorderLevel"},
        {label: "header.order.requisition.stock.available", name: "stockAvailable"},
        {label: "header.order.requisition.amount.required", name: "amountRequired"}

    ];*/


    VaccineOrderRequisitionColumns.get({},function(data){
        $scope.columns = data.columns;
    });

    LoggedInUserDetails.get({}, function (data) {
        $scope.loggedInUser = data.userDetails;
    });

    UserFacilityList.get({}, function (data) {

        if ($routeParams.id !== undefined || $routeParams.facility !== undefined) {

            $scope.userFacilities = $routeParams.facility;
            $scope.pageLineItems = _.findWhere(orderRequisitionLineItems, {facilityId: parseInt($routeParams.id,10)});

        } else {
            var userFacility = {};
            _.each(data.facilityList, function (data) {
                userFacility = data;
            });
            $scope.pageLineItems = _.findWhere($scope.report, {facilityId: parseInt(userFacility.id,10)});
            $scope.userFacilities.push(userFacility.name);
        }
    });

    ProgramForUserHomeFacility.get({}, function (data) {
        $scope.programs = data.programs;
    });

    $scope.submit = function () {

        var callBack = function (result) {
            if (result) {
                VaccineOrderRequisitionSubmit.update($scope.report2, function () {
                    $scope.message = "msg.ivd.submitted.successfully";
                    $location.path('/');
                });
            }
        };
        var options = {
            id: "confirmDialog",
            header: "label.confirm.submit.action",
            body: "msg.question.submit.ivd.confirmation"
        };
        OpenLmisDialog.newDialog(options, callBack, $dialog);

    };


    $scope.productFormChange = function () {
        $scope.selectedType = 1;

    };
    $scope.date = new Date();


    $scope.cancel = function (){
        $location.path(' ');
    };
}

VaccineOrderRequisitionController.resolve = {

    users: function ($q, LoggedInUserDetails, $route, $timeout) {

        var deferred = $q.defer();
        $timeout(function () {
            LoggedInUserDetails.get({}, function (data) {
                deferred.resolve(data.userDetails);
            }, function () {
            });
        }, 100);
        return deferred.promise;
    },

    programs: function ($q, $timeout, VaccineHomeFacilityPrograms) {
        var deferred = $q.defer();

        $timeout(function () {
            VaccineHomeFacilityPrograms.get({}, function (data) {
                deferred.resolve(data.programs);
            });
        }, 100);

        return deferred.promise;
    },

    VaccineReport:function($q, $timeout,VaccineOrderRequisitionReport){
        var deferred =$q.defer();
        $timeout(function () {
            VaccineOrderRequisitionReport.get({facilityId: 19075, programId: 82}, function (data) {
                var stockCards = data.stockCards;
                deferred.resolve(stockCards);
            });
        },100);

        return deferred.promise;
    }


};