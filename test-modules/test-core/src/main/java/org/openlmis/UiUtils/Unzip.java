/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.UiUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Unzip {


  public boolean deleteFile(String file) {
    boolean flag = false;
    File fileToBeDeleted = new File(file);
    if (fileToBeDeleted.exists()) {
        flag=fileToBeDeleted.delete();
    }
    return flag;
  }


  public void unZipIt(String zipFile, String outputFolder) {

    byte[] buffer = new byte[2048];

    try {
      if (new File(zipFile).exists()) {

        File folder = new File(outputFolder);
        if (!folder.exists()) {
          folder.mkdir();
        }

        ZipInputStream zis =
          new ZipInputStream(new FileInputStream(zipFile));
        ZipEntry ze = zis.getNextEntry();

        while (ze != null) {

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

    } catch (IOException ex) {
      ex.printStackTrace();
    }
  }

}



