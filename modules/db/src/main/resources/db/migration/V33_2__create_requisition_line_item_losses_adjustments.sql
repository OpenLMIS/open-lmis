CREATE TABLE requisition_line_item_losses_adjustments (
  requisitionLineItemId         INTEGER REFERENCES requisition_line_items(id),
  type                          VARCHAR(250) REFERENCES losses_adjustments_types(name),
  quantity                      INTEGER,
  PRIMARY KEY(requisitionLineItemId, type)
);