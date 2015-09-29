/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */


function StockMovementController($scope, getData, facilityStockCards, $dialog, $routeParams, $location, pageSize) {

    var jsonDate = (new Date()).toJSON();

    $scope.facilities = [];
    $scope.columns = [];
    $scope.date = new Date();

    $scope.sortComment = function (comment) {
        return comment.expirationDate;
    };



    var facilityList = [
        {
            "id": 1,
            "facility": {
                "id": 1,
                "name": "Karatu DVS"
            },
            "quantityRequested": 2000,
            "submittedDate": '09-09-2015'

        },
        {
            "id": 2,
            "facility": {
                "id": 2,
                "name": "Longido DVS"
            },
            "quantityRequested": 2000,
            "submittedDate": '07-02-2015'
        }
    ];

    $scope.productCategory = [
        {"id": 0, "name": "vaccine"},

        {"id": 1, "name": "Supply"}
    ];

    var facilitiesTo = [];

    facilityList.forEach(function (data) {
        facilitiesTo = data;
        $scope.facilities.push(facilitiesTo);
    });

    var facilityName = undefined;

    $scope.issueRequisition = function (row) {

        facilityName = getData;

    };


    $scope.pageSize = pageSize;
    $scope.pageLineItems = [];
    $scope.lineItems = [];
    $scope.homeFacility = facilityStockCards.homeFacility.name;

    $scope.lineItems = facilityStockCards.stockCards;
    $scope.stock = facilityStockCards;

    var pageLineItems1 = [];
    $scope.numberOfPages = Math.ceil($scope.lineItems.length / $scope.pageSize) || 1;
    var refreshPageLineItems = function () {
        $scope.currentPage = (utils.isValidPage($routeParams.page, $scope.numberOfPages)) ? parseInt($routeParams.page, 10) : 1;
        $scope.pageLineItems = $scope.lineItems.slice($scope.pageSize * ($scope.currentPage - 1), $scope.pageSize * $scope.currentPage);

    };

    refreshPageLineItems();

    $scope.$watch('currentPage', function () {
        $location.search('page', $scope.currentPage);
    });


    $scope.$on('$routeUpdate', function () {
        refreshPageLineItems();
    });


    $scope.sumLots = function (c) {
        this.x = 0;
        this.y = 0;
        this.c.amountRequested = 0;
        var total = 0;
        c.sum = [];

        this.x = parseInt(c.lots[1].dosesIssued);
        this.y = parseInt(c.lots[0].dosesIssued);
        this.c.amountRequested = parseInt(c.dosesRequested);
        c.totalIssued = this.x + this.y;
        c.sum = this.c.amountRequested - c.totalIssued;

    };

    $scope.validateExpiryBatches = function(c){
        alert(JSON.stringify(c));


    };

    $scope.save = function () {

        $scope.message = 'Saved Successfully';
        return $scope.message;
    };

    $scope.submit = function () {

        var callBack = function (result) {
            $scope.message = "msg.issue.submitted.successfully";
            /*  if (result) {
             VaccineReportSubmit.update($scope.report, function () {
             $scope.message = "msg.ivd.submitted.successfully";
             $location.path('/');
             });
             }*/
        };
        var options = {
            id: "confirmDialog",
            header: "label.distribution.confirm.submit.action",
            body: "msg.question.submit.confirmation"
        };
        OpenLmisDialog.newDialog(options, callBack, $dialog);
    };


    $scope.cancel = function () {
        $location.path('/view-pending');
    };

    function resetFlags() {
        $scope.submitError = $scope.submitMessage = $scope.error = $scope.message = "";
    }

    $scope.columns = [

            {label: "label.product", name: "product", visibility: true},
             {label: "label.skip", name: "skip", visibility: true},
            {label: "header.batch.number", name: "batchNumber", visible: true},
            {label: "header.soh.per.batch", name: "stockInHand", visibility: true},
            {label: "label.expiryDate", name: "expiryDate", visible: true},
            {label: "header.vvm.status", name: "vvmStatus", visible: true},
            {label: "header.doses.requested", name: "dosesRequested", visibility: true},
            {label: "header.doses.issued", name: "dosesIssued", visible: true},
            {label: "header.gap", name: "gap", visible: true}

    ];





}

StockMovementController.resolve = {

    pageSize: function ($q, $timeout, LineItemsPerPage) {
        var deferred = $q.defer();
        $timeout(function () {
            LineItemsPerPage.get({}, function (data) {
                var pageSize = 4;
                deferred.resolve(pageSize);
            }, {});
        }, 100);
        return deferred.promise;
    },


    facilityStockCards: function ($q, $timeout, $route, UserFacilityList, getStockCards, getStockCardData, getLots) {

        var deferred = $q.defer();
        var stockCards = [];
        var homeFacility = {};
        var facilityStockCards = {};
        var productCategory = [];
        $timeout(function () {

            //Home Facility
            UserFacilityList.get({}, function (data) {
                homeFacility = data.facilityList[0];
            });
            getStockCardData.get({facilityId: homeFacility.id}, function (data) {
                // For each Stock card get lots on hand
                var data2 = [];
                angular.extend(data2, data.stockCards);
                // data.stockCards[0].stockCards;
                data2.forEach(function (data2) {

                    var _stockCards = [];

                    _stockCards = data2.stockCards;
                    productCategory = data2.name;
                    _stockCards.forEach(function (s) {


                        var stockCard = s;
                        getLots.query({facilityId: homeFacility.id, productId: s.product.id}, function (data) {
                            var lots = data;
                            stockCard.lots = _.sortBy(lots,'expirationDate');
                            stockCard.physicalCount = stockCard.quantityOnHand;

                        });
                        stockCards.push(stockCard);

                    })
                });
                facilityStockCards.homeFacility = homeFacility;
                facilityStockCards.stockCards = stockCards;
                facilityStockCards.productCategory = productCategory;
                deferred.resolve(facilityStockCards);
            });
        }, 100);
        return deferred.promise;
    }
};
