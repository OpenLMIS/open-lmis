/**
 * Created by issa on 10/24/14.
 */
function UserPreferenceController($scope,ReportProductsByProgram,user,Users,EditUserPreference,UserFacilitiesForProgram,programs,$location,messageService,UpdateUserPreference,userDashboardPreferenceValues){
    $scope.user = user || {};
    $scope.programs = programs;

    $scope.preference = {program: userDashboardPreferenceValues[localStorageKeys.PREFERENCE.DEFAULT_PROGRAM],
        facility: userDashboardPreferenceValues[localStorageKeys.PREFERENCE.DEFAULT_FACILITY],
        products: userDashboardPreferenceValues[localStorageKeys.PREFERENCE.DEFAULT_PRODUCTS].split(', ')};

    $scope.$watch('preference.program',function(){
        loadFacilities();
        loadProducts();
    });

    var loadFacilities = function(){
        UserFacilitiesForProgram.get({userId:$scope.user.id, programId:$scope.preference.program},function(data){
            $scope.allFacilities = data.facilities;
        });
    };

    var loadProducts = function(){
        ReportProductsByProgram.get({programId: $scope.preference.program}, function(data){
            $scope.products = data.productList;
        });
    }

    $scope.saveUser = function () {
        var successHandler = function (msgKey) {
            $scope.showError = false;
            $scope.error = "";
            $scope.$parent.message = messageService.get(msgKey, $scope.user.firstName, $scope.user.lastName);
            $scope.$parent.userId = $scope.user.id;
           // $location.path('');
        };

        var saveSuccessHandler = function (response) {
            $scope.user = response.user;
            successHandler(response.success);
        };

        var updateSuccessHandler = function () {
            successHandler("message.user.updated.success");
        };

        var errorHandler = function (response) {
            $scope.showError = true;
            $scope.message = "";
            $scope.error = response.data.error;
        };

        if ($scope.user.id) {
            EditUserPreference.update({id: $scope.user.id}, $scope.user, updateSuccessHandler, errorHandler);

            $scope.preference.program = isUndefined($scope.preference.program)? 1: $scope.preference.program;
            $scope.preference.facility = isUndefined($scope.preference.facility)? 1: $scope.preference.facility;
            $scope.preference.products = isUndefined($scope.preference.products)? [1]: $scope.preference.products;
            alert('preferences '+JSON.stringify($scope.preference))

            UpdateUserPreference.update({userId: $scope.user.id, programId: $scope.preference.program,
                facilityId:$scope.preference.facility, products:$scope.preference.products},{},updateSuccessHandler, errorHandler);
        }

        return true;
    };

}

UserPreferenceController.resolve = {

    user: function ($q, Users,EditUserPreference, $route, $timeout) {

        var userId = $route.current.params.userId;
        if (!userId) return undefined;
        var deferred = $q.defer();
        $timeout(function () {
            EditUserPreference.get({id: userId}, function (data) {
                deferred.resolve(data.user);
            }, function () {
            });
        }, 100);
        return deferred.promise;
    },

    programs: function ($q, UserPrograms,$route, $timeout) {

        var userId = $route.current.params.userId;

        if (!userId) return undefined;
        var deferred = $q.defer();

        $timeout(function () {
            UserPrograms.get({userId:userId}, function (data) {
                deferred.resolve(data.programs);

            }, function () {
            });
        }, 100);

        return deferred.promise;
    },
    userDashboardPreferenceValues: function($q,$timeout,$route,UserPreferences){
        var userId = $route.current.params.userId;

        if (!userId) return undefined;
        var deferred = $q.defer();

        $timeout(function () {
            UserPreferences.get({userId:userId}, function (data) {
                deferred.resolve(data.preferences);

            }, function () {
            });
        }, 100);


        return deferred.promise;
    }

};