function FacilitySearchController($scope, facilities, $location) {

  $scope.facilityList = facilities;

  $scope.editFacility = function (id) {
    $location.path('edit/' + id);
  };


  $scope.filterFacilitiesByNameOrCode = function(query) {
    var filteredFacilities = [];
    query = query || "";

    angular.forEach($scope.facilityList, function(facility) {
      if (facility.name.toLowerCase().indexOf(query.toLowerCase()) >= 0 || facility.code.toLowerCase().indexOf(query.toLowerCase()) >= 0) {
        filteredFacilities.push(facility);
      }
    });
    $scope.resultCount = filteredFacilities.length;
    return filteredFacilities;
  };

  $scope.clearSearch = function() {
    $scope.query = "";
    $scope.resultCount = 0;
    angular.element("#searchFacility").focus();
  };
}

FacilitySearchController.resolve = {
  facilities:function ($q, $timeout, AllFacilities) {
    var deferred = $q.defer();
    $timeout(function () {
      AllFacilities.get({}, function (data) {
        deferred.resolve(data.facilityList);
      }, {});
    }, 100);
    return deferred.promise;
  }
};
