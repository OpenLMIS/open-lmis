function InitiateRnrController($http, $scope, Facility, FacilitySupportedPrograms, $location) {
    Facility.get({}, function (data) {
            $scope.facilities = data.facilityList;
        }, {}
    );

    $scope.loadPrograms = function () {
        if ($scope.$parent.facility) {
            FacilitySupportedPrograms.get({facilityCode:$scope.facility}, function (data) {
                $scope.$parent.programsForFacility = data.programList;
            }, {});
        } else {
            $scope.$parent.programsForFacility = null;
        }
    };

    $scope.getRnrHeader = function () {
        if (validate()) {
            $scope.error = "";
            initRnr();
        }
        else {
            $scope.error = "Please select Facility and program for facility to proceed";
        }
    };

    var validate = function () {
        return $scope.$parent.program;
    };

    var initRnr = function () {
        $http.post('/logistics/rnr/' + $scope.facility + '/' + $scope.program.code + '/init.json', {}).success(function (data) {
            $scope.error = "";
            $scope.$parent.rnr = data.rnr;
            $location.path('create-rnr');
        }).error(function () {
                $scope.error = "Rnr initialization failed!";
                $scope.message = "";
            });
    };
}

function CreateRnrController($scope, RequisitionHeader, ProgramRnRColumnList, $location) {
    RequisitionHeader.get({code:$scope.$parent.facility}, function (data) {
        $scope.header = data.requisitionHeader;
    }, function () {
        $location.path("init-rnr");
    });

    ProgramRnRColumnList.get({programCode:$scope.$parent.program.code}, function (data) {
        if (validate(data)) {
            $scope.$parent.error = "";
            $scope.programRnRColumnList = data.rnrColumnList;
        } else {
            $scope.$parent.error = "Please contact Admin to define R&R template for this program";
            $location.path('init-rnr');
        }
    }, function () {
        $location.path('init-rnr');
    });


    $scope.fieldMap = new Object();
    $scope.fieldMap['product_code'] = 'rnrLineItem.product.code';
    $scope.fieldMap['unit_of_issue'] = 'product.dispensingUnit';
    $scope.fieldMap['beginning_balance'] = 'beginningBalance';
    $scope.fieldMap['quantity_received'] = 'quantityReceived';
    $scope.fieldMap['quantity_dispensed'] = 'beginningBalance';
    $scope.fieldMap['losses_and_adjustments'] = 'lossesAndAdjustments';
    $scope.fieldMap['reason_for_losses_and_adjustments'] = 'reasonForLossesAndAdjustments';
    $scope.fieldMap['stock_in_hand'] = 'stockInHand';
    $scope.fieldMap['new_patient_count'] = 'patientCount';
    $scope.fieldMap['stock_out_days'] = 'stockOutDays';
    $scope.fieldMap['normalized_consumption'] = 'normalizedConsumption';
    $scope.fieldMap['amc'] = 'amc';
    $scope.fieldMap['max_stock_quantity'] = 'maxStockQuantity';
    $scope.fieldMap['calculated_order_quantity'] = 'calculatedOrderQuantity';
    $scope.fieldMap['quantity_requested'] = 'quantityRequested';
    $scope.fieldMap['reason_for_requested_quantity'] = 'reasonForRequestedQuantity';
    $scope.fieldMap['quantity_approved'] = 'quantityApproved';
    $scope.fieldMap['packs_to_ship'] = 'packsToShip';
    $scope.fieldMap['Price'] = 'product.price';
    $scope.fieldMap['cost'] = 'cost';
    $scope.fieldMap['remarks'] = 'remarks';

    var validate = function (data) {
        return data.rnrColumnList.length > 0 ? true : false;
    }

}