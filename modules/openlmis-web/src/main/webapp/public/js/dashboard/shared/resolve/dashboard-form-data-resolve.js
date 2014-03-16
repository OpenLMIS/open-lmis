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
    },
    formInputValue : function(messageService){
               return {
                yearOptionAll : messageService.get('input.year.option.all'),
                programOptionSelect : messageService.get('input.program.option.select'),
                scheduleOptionSelect : messageService.get('input.schedule.option.select'),
                requisitionOptionAll : messageService.get('input.requisition.option.all'),
                facilityOptionSelect : messageService.get('input.facility.option.select'),
                periodOptionSelect : messageService.get('input.period.option.select')
            };
    }
};