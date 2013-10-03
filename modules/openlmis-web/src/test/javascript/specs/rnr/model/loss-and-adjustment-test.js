/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

describe('loss-and-adjustment', function () {
  it('should validate a lossAndAdjustment to be invalid if not quantity is present', function(){
      var lossAndAdjustment = new LossAndAdjustment({"type":{"name":"LOSS1", "additive":true}});
      expect(lossAndAdjustment.isQuantityValid()).toBeFalsy();
  });

  it('should validate a lossAndAdjustment to be valid if not quantity is present', function(){
      var lossAndAdjustment = new LossAndAdjustment({"type":{"name":"LOSS1", "additive":true}, "quantity":50});
      expect(lossAndAdjustment.isQuantityValid()).toBeTruthy();
  });


  it('should return true if two losses and adjustment have the same type name', function(){
      var lossAndAdjustment1 = new LossAndAdjustment({"type":{"name":"LOSS1"}, "quantity":60});
      var lossAndAdjustment2 = new LossAndAdjustment({"type":{"name":"LOSS1"}, "quantity":70});
      expect(lossAndAdjustment1.equals(lossAndAdjustment2)).toBeTruthy();
  });

  it('should return false if two losses and adjustment do not have the same type name', function(){
      var lossAndAdjustment1 = new LossAndAdjustment({"type":{"name":"LOSS1"}, "quantity":60});
      var lossAndAdjustment2 = new LossAndAdjustment({"type":{"name":"LOSS2"}, "quantity":60});
      expect(lossAndAdjustment1.equals(lossAndAdjustment2)).toBeFalsy();
  });
});

