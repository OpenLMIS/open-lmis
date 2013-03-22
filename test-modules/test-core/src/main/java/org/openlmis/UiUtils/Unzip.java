/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.UiUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Unzip
{


  public boolean deleteFile(String file){
    boolean flag=false;
    File fileToBeDeleted = new File(file);
    if(fileToBeDeleted.exists())
    {
      fileToBeDeleted.delete();
      flag=true;
    }
    return flag;
  }


  public void unZipIt(String zipFile, String outputFolder){

    byte[] buffer = new byte[2048];

    try{
      if(new File(zipFile).exists())
      {

      File folder = new File(outputFolder);
      if(!folder.exists()){
        folder.mkdir();
      }

      ZipInputStream zis =
        new ZipInputStream(new FileInputStream(zipFile));
      ZipEntry ze = zis.getNextEntry();

      while(ze!=null){

        String fileName = ze.getName();
        File newFile = new File(outputFolder + File.separator + fileName);

        new File(newFile.getParent()).mkdirs();

        FileOutputStream fos = new FileOutputStream(newFile);

        int len;
        while ((len = zis.read(buffer)) > 0) {
          fos.write(buffer, 0, len);
        }

        fos.close();
        ze = zis.getNextEntry();
      }

      zis.closeEntry();
      zis.close();

      }

    }catch(IOException ex){
      ex.printStackTrace();
    }
  }

}



