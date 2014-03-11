/**
 * Created with IntelliJ IDEA.
 * User: issa
 * Date: 2/26/14
 * Time: 9:37 PM
 * To change this template use File | Settings | File Templates.
 */

ResolveDashboardFormData = {
    userGeographicZoneList :function ($q, $timeout,$rootScope, UserFacilityList) {
        var deferred = $q.defer();
        $timeout(function () {
            var userGeographicZoneList = $rootScope.userGeographicZones;

            if(userGeographicZoneList){
                deferred.resolve(userGeographicZoneList);
                $rootScope.userGeographicZones = undefined;
                return;
            }
            UserFacilityList.get({}, function (data) {
                var userFacilities = data.facilityList;
                if(userFacilities){
                    var zones = _.map(userFacilities, function(facility){return facility.geographicZone;});
                    deferred.resolve(zones);
                }else{

                    deferred.resolve(null);
                }
            }, {});
        }, 100);
        return deferred.promise;
    }
};