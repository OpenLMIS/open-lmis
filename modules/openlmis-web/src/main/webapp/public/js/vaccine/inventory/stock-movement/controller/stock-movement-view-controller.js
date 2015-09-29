/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */


function StockMovementViewController($scope,$window,UpdateOrderRequisitionStatus, StockCardsByCategoryAndRequisition,StockCardsForProgramByCategory, $dialog,homeFacility, programs, $routeParams, $location,VaccineIssueStock) {

    var orderId = parseInt($routeParams.id,10);
    var programId = parseInt($routeParams.programId,10);
    var periodId = parseInt($routeParams.periodId,10);

    var homeFacilityId = parseInt(homeFacility.id,10);
    var toFacilityId = parseInt($routeParams.facilityId,10);
     $scope.toFacilityName = $routeParams.facilityName;

    var program = programs;

    $scope.line = [];
    $scope.facilities = [];
    $scope.date = new Date();

 /*   $scope.check = function (c, index) {
        if (index != null) {
            var sum = 0;
            for (var x = 0; x < index; x++) {
                sum = sum + c.lots[x].quantityRemain;
            }
            if (c.dosesRequested > sum) {

                return true;

            }
            else {

                return false;
            }

        }
        else {
            var rowspan = 1;
            var sum = 0;

            //if(c.lots !== undefined)
            //{
            for (var x = 0; x < c.lots.length; x++) {
                sum = sum + c.lots[x].quantityRemain;
                console.log(x + " =" + sum);

                if (c.dosesRequested > sum) {

                    rowspan++;
                    console.log(rowspan);
                }
                else {
                    return rowspan;
                }
            }
            //}

        }


    };*/


    $scope.pageSize = parseInt(10,10);
    var pageLineItems = [];

    $scope.homeFacility = $routeParams.facility;


    var refreshPageLineItems = function () {

        if (isUndefined(homeFacilityId) && isUndefined(programs[0].id &&
                isUndefined(periodId) && isUndefined(toFacilityId))) {

            return 'No pending Requisition';

        }else {

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
        c.lotsOnHand.forEach(function(l){
             var x = ((l.quantity==='' || l.quantity=== undefined)?0:parseInt(l.quantity,10));
            total = total + x;

        });

            $scope.total = total;


        c.sum = parseInt(c.quantityRequested,10) - total;

    };

    $scope.save = function () {
        $scope.message = 'Saved Successfully';
        return $scope.message;
    };




    $scope.submit = function () {

        if ($scope.orderRequisitionForm.$error.required) {
            $scope.showError = true;
            $scope.error = "form.error";
            return;
        }

        var transaction={};
        transaction.transactionList=[];

        var callBack = function (result) {
            if (result) {


                $scope.stockCardsByCategory.forEach(function(st){

                    st.stockCards.forEach(function(s){
                        var list={};
                        list.productId=s.product.id;
                        list.quantity=s.quantity;
                        list.lots=[];
                        s.lotsOnHand.forEach(function(l){
                            var lot={};
                            lot.lotId=l.lot.id;
                            lot.quantity=parseInt(l.quantity,10);

                            list.lots.push(lot);
                        });
                        transaction.transactionList.push(list);
                    });

                });


                VaccineIssueStock.update(transaction, function () {

                    $scope.message = "label.form.Submitted.Successfully";

                });

                UpdateOrderRequisitionStatus.update({orderId:orderId},function(){

                    $window.location = '/public/pages/vaccine/order-requisition/index.html#/view';

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


    var successFunc = function (data) {
        $scope.showError = "true";
        $scope.error = "";
        $scope.message = data.success;
        $scope.originalFacilityCode = data.facility.code;
        $scope.originalFacilityName = data.facility.name;
    };

    var errorFunc = function (data) {
        $scope.showError = "true";
        $scope.message = "";
        $scope.error = data.data.error;
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
