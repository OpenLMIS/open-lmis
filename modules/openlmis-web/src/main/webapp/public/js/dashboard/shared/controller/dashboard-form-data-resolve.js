/**
 * Created with IntelliJ IDEA.
 * User: issa
 * Date: 2/26/14
 * Time: 9:37 PM
 * To change this template use File | Settings | File Templates.
 */

ResolveDashboardFormData = {
    programs: function($q, $timeout,ReportPrograms){
        var deferred = $q.defer();
        $timeout(function () {
            ReportPrograms.get({},function(data){
               deferred.resolve(data.programs);
            },{});

        },100);

        return deferred.promise;
    },

    schedules: function($q, $timeout, ReportSchedules){
        var deferred = $q.defer();
        $timeout(function () {
            ReportSchedules.get({},function(data){
                deferred.resolve(data.schedules);
            },{});

        },100);

        return deferred.promise;
    },
    operationYears:function($q, $timeout, OperationYears){
        var deferred = $q.defer();
        $timeout(function () {
            OperationYears.get({},function(data){
                deferred.resolve(data.years);
            },{});

        },100);

        return deferred.promise;
    },
    userFacilityData :function ($q, $timeout, UserFacilityList) {
        var deferred = $q.defer();
        $timeout(function () {
            UserFacilityList.get({}, function (data) {
                deferred.resolve(data);
            }, {});
        }, 100);
        return deferred.promise;
    }
};