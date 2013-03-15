var LossAndAdjustment = function (lossAndAdjustmentJSon) {
  this.id = lossAndAdjustmentJSon.id;
  this.type = lossAndAdjustmentJSon.type;
  this.quantity = lossAndAdjustmentJSon.quantity;

  LossAndAdjustment.prototype.isQuantityValid = function () {
    return isUndefined(this.quantity)? false:true;
  };
};
