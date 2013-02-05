services.factory('messageService', function (Messages, localStorageService) {

  var populate = function () {
    var messagesInStorage = localStorageService.get(localStorageKeys.MESSAGES);
    if (!messagesInStorage) {
      Messages.get({}, function (data) {
        for(var attr in data.messages){
          localStorageService.add('message.'+attr, data.messages[attr]);
        }
      }, {});
    }
  }

  var get = function(key){
   return localStorageService.get('message.'+key);
  }

  return{
    populate:populate,
    get:get
  }
});
