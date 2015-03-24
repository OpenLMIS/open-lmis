/**
 * Created by issa on 10/24/14.
 */
function UserPreferenceController($scope,ReportProductsByProgram,user,roles_map,supervisoryNodes,UserFacilitiesForProgram,programs,$location,messageService,
                                  UpdateUserPreference,userDashboardPreferenceValues, UserPreferences, localStorageService){
    $scope.user = user || {};
    $scope.programs = programs;
    $scope.supervisoryNodes = supervisoryNodes;
    $scope.rolesMap = roles_map;

    $scope.preference = {program: userDashboardPreferenceValues[localStorageKeys.PREFERENCE.DEFAULT_PROGRAM],
        facility: userDashboardPreferenceValues[localStorageKeys.PREFERENCE.DEFAULT_FACILITY],
        products: userDashboardPreferenceValues[localStorageKeys.PREFERENCE.DEFAULT_PRODUCTS].split(',')};

    $scope.$watch('preference.program',function(){
        loadFacilities();
        loadProducts();
    });

    var loadFacilities = function(){
        if(!isUndefined($scope.programs) && $scope.programs.length > 0){
            UserFacilitiesForProgram.get({userId:$scope.user.id, programId:$scope.preference.program},function(data){
                $scope.allFacilities = data.facilities;
            });
        }
    };

    var loadProducts = function(){
        if(!isUndefined($scope.programs) && $scope.programs.length > 0){
            ReportProductsByProgram.get({programId: $scope.preference.program}, function(data){
                $scope.products = data.productList;
            });
        }

    };

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

        var updateSuccessPreferenceHandler = function (response) {
            successHandler("message.user.updated.success");
        };

        var errorHandler = function (response) {
            $scope.showError = true;
            $scope.message = "";
            $scope.error = response.data.error;
        };

        if ($scope.user.id) {
             $scope.preference.program = isUndefined($scope.preference.program)? 1: $scope.preference.program;
            $scope.preference.facility = isUndefined($scope.preference.facility)? 1: $scope.preference.facility;
            $scope.preference.products = isUndefined($scope.preference.products)? [1]: $scope.preference.products;

            UpdateUserPreference.update({userId: $scope.user.id, programId: $scope.preference.program,
                facilityId:$scope.preference.facility, products:$scope.preference.products}, $scope.user ,updateSuccessPreferenceHandler, errorHandler);

            //if user preference of currently logged-in user changes, reload the new user preference to localstorage
            if($scope.user.id == localStorageService.get(localStorageKeys.USER_ID)){
                reloadUserDashboardPreferenceCache($scope.user.id);
            }

        }

        return true;
    };

    var reloadUserDashboardPreferenceCache = function(userId){

        $.each(localStorageKeys.PREFERENCE, function(item, idx){
            localStorageService.remove(idx);

        });
        UserPreferences.get({userId: userId}, function(data){
            $scope.prefData = data.preferences;
            for (var prefKey in $scope.prefData) {
                localStorageService.add(prefKey, $scope.prefData[prefKey]);
            }
        });

    };

    $scope.getSupervisoryNodeName = function (supervisoryNodeId) {
        return _.findWhere($scope.supervisoryNodes, {id: supervisoryNodeId}).name;
    };

    $scope.getProgramName = function (programId) {
        return _.findWhere($scope.programs, {id: programId}).name;
    };

    $scope.getRoleName = function (roleId) {
       return _.findWhere($scope.rolesMap.REQUISITION, {id: roleId}).name;
    };

}

UserPreferenceController.resolve = {

    user: function ($q, Users,UserProfile, $route, $timeout) {

        var userId = $route.current.params.userId;
        if (!userId) return undefined;
        var deferred = $q.defer();
        $timeout(function () {
            UserProfile.get({id: userId}, function (data) {
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
    },
    supervisoryNodes: function ($q, SupervisoryNodesList, $timeout) {
        var deferred = $q.defer();

        $timeout(function () {
            SupervisoryNodesList.get({}, function (data) {
                deferred.resolve(data.supervisoryNodes);
            }, function () {
            });
        }, 100);

        return deferred.promise;
    },
    roles_map: function ($q, RolesList, $timeout) {
        var deferred = $q.defer();

        $timeout(function () {
            RolesList.get({}, function (data) {
                deferred.resolve(data.roles_map);
            }, function () {
            });
        }, 100);

        return deferred.promise;
    }

};