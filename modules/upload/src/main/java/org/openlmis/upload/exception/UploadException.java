package org.openlmis.upload.exception;

import lombok.Data;

@Data
public class UploadException extends RuntimeException {

  private String code;
  private String[] params = new String[0];

  public UploadException(String message) {
        super(message);
    }

  public UploadException(String code, String... params) {
    this.code = code;
    this.params = params;
  }

  @Override
  public String getMessage(){
    if(code == null) return super.getMessage();

    StringBuilder messageBuilder = new StringBuilder("code: "+code+ ", params: { ");
    for(String param : params){
      messageBuilder.append("; ").append(param);
    }
    messageBuilder.append(" }");
    return messageBuilder.toString().replaceFirst("; ","");
  }

}
