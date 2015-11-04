/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */


function StockMovementViewController($scope, $window,SaveDistribution,StockEvent, UpdateOrderRequisitionStatus,VaccineLastStockMovement, StockCardsByCategoryAndRequisition, StockCardsForProgramByCategory, $dialog, homeFacility, programs, $routeParams, $location) {

    var orderId = parseInt($routeParams.id, 10);
    var programId = parseInt($routeParams.programId, 10);
    var periodId = parseInt($routeParams.periodId, 10);

    var homeFacilityId = parseInt(homeFacility.id, 10);
    var toFacilityId = parseInt($routeParams.facilityId, 10);
    $scope.toFacilityName = $routeParams.facilityName;

    var program = programs;

    $scope.line = [];
    $scope.facilities = [];
    $scope.date = new Date();

    $scope.pageSize = parseInt(10, 10);
    var pageLineItems = [];

    $scope.homeFacility = $routeParams.facility;


    var refreshPageLineItems = function () {

        if (isUndefined(homeFacilityId) && isUndefined(programs[0].id &&
                isUndefined(periodId) && isUndefined(toFacilityId))) {

            return 'No pending Requisition';

        } else {

            StockCardsForProgramByCategory.get(program[0].id, homeFacilityId, periodId, toFacilityId).then(function (data) {
                $scope.pageLineItems = data;
                pageLineItems = data;
                $scope.numberOfPages = Math.ceil(pageLineItems.length / $scope.pageSize) || 1;
                $scope.currentPage = (utils.isValidPage($routeParams.page, $scope.numberOfPages)) ? parseInt($routeParams.page, 10) : 1;
                $scope.stockCardsByCategory = $scope.pageLineItems.slice($scope.pageSize * ($scope.currentPage - 1), $scope.pageSize * $scope.currentPage);

            });
        }

    };

    refreshPageLineItems();

    $scope.$watch('currentPage', function () {
        $location.search('page', $scope.currentPage);
    });


    $scope.$on('$routeUpdate', function () {
        refreshPageLineItems();
    });


    $scope.sumLots = function (c) {

        var total = 0;
        c.lotsOnHand.forEach(function (l) {
            var x = ((l.quantity === '' || l.quantity === undefined) ? 0 : parseInt(l.quantity, 10));
            total = total + x;

        });

        $scope.total = total;


        c.sum = parseInt(c.quantityRequested, 10) - total;

    };

    $scope.save = function () {
        $scope.message = 'Saved Successfully';
        return $scope.message;
    };
    var printTest = false;

    $scope.submit = function () {
//        if ($scope.orderRequisitionForm.$error.required) {
//            $scope.showError = true;
//            $scope.error = "The form you submitted is invalid. Please revise and try again.";
//            return;
//        }

        var transaction = {};
        transaction.transactionList = [];

        var lastInsertedReport = {};
        lastInsertedReport.list = [];

        var callBack = function (result) {
            if (result) {
                var distribution = {};
                var events=[];

                distribution.fromFacilityId = homeFacility.id;
                distribution.toFacilityId= toFacilityId;
                distribution.distributionDate = $scope.stockCardsByCategory[0].issueDate;
                distribution.periodId = periodId;
                distribution.orderId = orderId;
                distribution.voucherNumber = $scope.stockCardsByCategory[0].issueVoucher;
                distribution.lineItems=[];
                distribution.distributionType="ROUTINE";
                distribution.status="PENDING";

                $scope.stockCardsByCategory.forEach(function (st) {

                    st.stockCards.forEach(function (s) {

                        var list = {};

                           list.productId = s.product.id;

                           if(s.lotsOnHand.length >0)
                           {
                           list.lots = [];
                           var lotSum = 0;
                           s.lotsOnHand.forEach(function (l) {


                               if( l.quantity >0)
                               {
                                   var lot = {};
                                   var event={};
                                   event.type="ISSUE";
                                   event.productCode= s.product.code;
                                   event.facilityId=toFacilityId;
                                   event.lotId= l.lot.id;
                                   event.quantity= l.quantity;
                                   event.customPros={"occurred":st.issueDate};
                                   events.push(event);

                                   lot.lotId = l.lot.id;
                                   lot.quantity = l.quantity;
                                   list.lots.push(lot);
                                   lotSum = lotSum + l.quantity;
                               }


                              });
                               list.quantity = lotSum;
                               distribution.lineItems.push(list);

                           }
                        else{
                               var event={};
                               event.type="ISSUE";
                               event.productCode= s.product.code;
                               event.facilityId=toFacilityId;
                               event.quantity= st.quantity;
                               event.customPros={"occurred":st.issueDate};
                               events.push(event);

                           }

                        distribution.lineItems.push(list);

                    });

                });
              StockEvent.save({facilityId:homeFacility.id},events,function(data){
                   SaveDistribution.save(distribution,function(distribution) {
                        UpdateOrderRequisitionStatus.update({orderId: orderId}, function () {
                            print(distribution.distributionId);
                            $scope.message = "label.form.Submitted.Successfully";
                         });
                   });
               });

            }
        };


        var options = {
            id: "confirmDialog",
            header: "label.confirm.issue.submit.action",
            body: "msg.question.submit.order.confirmation"
        };

        OpenLmisDialog.newDialog(options, callBack, $dialog);


    };

    var print = function(distributionId){
              var url = '/vaccine/orderRequisition/issue/print/'+distributionId;
               $window.open(url, '_blank');

               $window.location = '/public/pages/vaccine/inventory/dashboard/index.html#/stock-on-hand';

         };


    $scope.cancel = function () {
        $window.location = '/public/pages/vaccine/order-requisition/index.html#/view';
    };


}


StockMovementViewController.resolve = {


    homeFacility: function ($q, $timeout, UserFacilityList) {
        var deferred = $q.defer();
        var homeFacility = {};

        $timeout(function () {
            UserFacilityList.get({}, function (data) {
                homeFacility = data.facilityList[0];
                deferred.resolve(homeFacility);
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
    }

};
