var OrderRequisitionLineItem = function(stockCards,report){

    $.extend(this, stockCards);

    OrderRequisitionLineItem.prototype.getQuantityToRequest = function() {
       var quantity = (this.getMaximumStock() - Number(parseInt(this.stockOnHand,10)) );
           this.quantityRequested = quantity;
        return quantity;

    };

    OrderRequisitionLineItem.prototype.getMaximumStock = function(){
     var max= (Number(parseInt(this.overriddenisa,10)) * Number(this.maxmonthsofstock));
        this.maximumStock = max;
        return max;
    };
    OrderRequisitionLineItem.prototype.getMinimumStock = function(){
       var min = ( Number(this.overriddenisa) * Number(this.minmonthsofstock));
        this.minimumStock = min;
        return min;
    };

    OrderRequisitionLineItem.prototype.getReorderLevel= function(){
         var reorder = (Number(this.overriddenisa) * Number(this.eop));
        this.reOrderLevel = reorder;
        return reorder;
    };

    OrderRequisitionLineItem.prototype.getBufferStock = function(){
        var buffer = (this.getReorderLevel() - this.getMaximumStock());
        this.bufferStock = buffer;
        return buffer;

    };

    OrderRequisitionLineItem2.prototype.totalStockOnHand = function(){
        return Math.ceil(Number(this.stockOnHand) / this.getTotalByVial());
    };


};
