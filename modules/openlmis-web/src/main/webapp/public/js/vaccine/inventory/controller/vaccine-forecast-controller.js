function VaccineForecastingController($scope,$routeParams,$location,programs,homeFacility,StockRequirementsData){

    $scope.pageLineItems1 = [];
    $scope.pageLineItems = [];
    var dataToDisplay = [];
    $scope.pageSize = 10;
    var program = 0;

    if(programs.length == 1){
        program = programs[0].id;

    }
 var refreshPageLineItems = function(){
    StockRequirementsData.get(parseInt(program,10), parseInt(homeFacility.id,10)).then(function (data) {
        dataToDisplay = data;
        $scope.numberOfPages = Math.ceil(dataToDisplay.length / $scope.pageSize) || 1;
        $scope.currentPage = (utils.isValidPage($routeParams.page, $scope.numberOfPages)) ? parseInt($routeParams.page, 10) : 1;
        $scope.pageLineItems = dataToDisplay.slice($scope.pageSize * ($scope.currentPage - 1), $scope.pageSize * $scope.currentPage);
    });
 };
    refreshPageLineItems();

    $scope.$watch('currentPage', function () {
        $location.search('page', $scope.currentPage);
    });


    $scope.$on('$routeUpdate', function () {
        refreshPageLineItems();
    });

}

VaccineForecastingController.resolve = {



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