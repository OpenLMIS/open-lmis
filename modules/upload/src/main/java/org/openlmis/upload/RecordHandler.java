package org.openlmis.upload;

public interface RecordHandler<I extends Importable> {

    public void execute(I importable, int rowNumber, Integer modifiedBy);
}
