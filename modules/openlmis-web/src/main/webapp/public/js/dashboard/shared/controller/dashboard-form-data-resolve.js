/**
 * Created with IntelliJ IDEA.
 * User: issa
 * Date: 2/26/14
 * Time: 9:37 PM
 * To change this template use File | Settings | File Templates.
 */

ResolveDashboardFormData = {
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