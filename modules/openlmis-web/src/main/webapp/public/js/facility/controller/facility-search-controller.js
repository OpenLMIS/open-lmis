function FacilitySearchController($scope, facilities, $location) {

  $scope.facilityList = facilities;

  $scope.editFacility = function (id) {
    $location.path('edit/' + id);
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

