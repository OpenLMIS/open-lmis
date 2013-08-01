distributionModule.service('SharedDistributions', function (IndexedDB, $rootScope) {

  this.distributionList = [];

  var thisService = this;

  this.update = function () {

    var transaction = IndexedDB.getConnection().transaction('distributions');

    var distributionsStore = transaction.objectStore('distributions');

    var cursorRequest = distributionsStore.openCursor();
    var aggregate = [];

    cursorRequest.onsuccess = function (event) {
      if (event.target.result) {
        aggregate.push(event.target.result.value);
        event.target.result['continue']();
      }
    };

    transaction.oncomplete = function (e) {
      thisService.distributionList = aggregate;
      $rootScope.$apply();
    };

  }

});
