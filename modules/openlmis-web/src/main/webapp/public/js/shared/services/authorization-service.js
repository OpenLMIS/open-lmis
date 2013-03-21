services.factory('AuthorizationService', function (localStorageService, $window) {

  var rights = localStorageService.get(localStorageKeys.RIGHT);

  var hasPermission = function (permission) {
    if (rights && rights.indexOf(permission) == -1) {
      $window.location = "/public/pages/access-denied.html";
      return false;
    }

    return true;
  };

  return{
    hasPermission:hasPermission
  }
});
