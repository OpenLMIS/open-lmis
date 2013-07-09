/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

describe('View non full supply controller',function(){
  var scope

  beforeEach(inject(function($rootScope,$controller){
    scope = $rootScope.$new();
    scope.visibleColumns =[{name:'product'}, {name:'productCode'},{name:'lossesAndAdjustment'}, {name:'quantityApproved'}];

    $controller(ViewNonFullSupplyController, {$scope:scope});
  }));

  it('should filter visible column list',function(){
    expect(scope.visibleNonFullSupplyColumns).toEqual([{name:'product'}, {name:'productCode'},{name:'quantityApproved'}])
  });
});