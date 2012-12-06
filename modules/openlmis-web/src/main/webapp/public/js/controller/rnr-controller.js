function InitiateRnrController($http, $scope, UserFacilityList, UserSupportedProgramInFacilityForAnOperation, $location) {
    UserFacilityList.get({}, function (data) {
            $scope.facilities = data.facilityList;
        }, {}
    );

    $scope.loadPrograms = function () {
        if ($scope.$parent.facility) {
            UserSupportedProgramInFacilityForAnOperation.get({facilityCode:$scope.facility}, function (data) {
                $scope.$parent.programsForFacility = data.programList;
            }, {});
        } else {
            $scope.$parent.program = null;
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
        $http.post('/logistics/rnr/' + encodeURIComponent($scope.facility) + '/' + encodeURIComponent($scope.program.code) + '/init.json', {}).success(function (data) {
            $scope.error = "";
            $scope.$parent.rnr = data.rnr;
            $location.path('create-rnr');
        }).error(function () {
                $scope.error = "Rnr initialization failed!";
                $scope.message = "";
            });
    };
}

function CreateRnrController($scope, RequisitionHeader, ProgramRnRColumnList, $location, $http) {

    $scope.positiveInteger = function(value, errorHolder){
       var INTEGER_REGEXP = /^\d*$/;
       var valid = INTEGER_REGEXP.test(value);

       if(errorHolder!=undefined) toggleErrorMessageDisplay(valid, errorHolder)

       return valid;
    }

   var  toggleErrorMessageDisplay = function(valid, errorHolder){
        if(valid){
            document.getElementById(errorHolder).style.display='none';
        }else {
            document.getElementById(errorHolder).style.display='block';
        }
   }

    $scope.positiveFloat = function(value){
        var FLOAT_REGEXP = /^\d+(\.\d\d)?$/;
        return FLOAT_REGEXP.test(value);
    }

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

    var validate = function (data) {
        return data.rnrColumnList.length > 0 ? true : false;
    }

    $scope.saveRnr = function () {
        if ($scope.saveRnrForm.$error.rnrError!=undefined && $scope.saveRnrForm.$error.rnrError!=false && $scope.saveRnrForm.$error.rnrError.length > 0  ) {
              $scope.error = "Please correct errors before saving.";
              $scope.message = "";
              return ;
        }
        $http.post('/logistics/rnr/' + $scope.$parent.rnr.id+ '/save.json', $scope.$parent.rnr ).success(function (data){
         $scope.message = "R&R saved successfully!";
         $scope.error="";
        });
    }
}