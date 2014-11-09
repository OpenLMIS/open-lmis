/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

package org.openlmis.report.exporter;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.*;
import net.sf.jasperreports.engine.util.JRLoader;
import org.openlmis.report.ReportOutputOption;
import org.openlmis.report.model.ReportData;
import org.openlmis.report.util.Constants;
import org.springframework.stereotype.Component;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

/**
 * Handles Exporting of Jasper reports
 */
@Component
public class JasperReportExporter implements ReportExporter {


    @Override
    public ByteArrayOutputStream exportReportBytesStream(InputStream reportInputStream, HashMap<String, Object> reportExtraParams, List<? extends ReportData> reportData, ReportOutputOption outputOption) {

        try{

            JasperPrint jasperPrint = getJasperPrintResult(reportExtraParams, reportInputStream, outputOption, reportData);

            String reportOutputFileName = reportExtraParams != null ? ((String) reportExtraParams.get(Constants.REPORT_NAME)) : "";

            //Jasper export handler
            return export(outputOption, reportOutputFileName, jasperPrint);

        } catch (JRException e) {

            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void exportReport(InputStream reportInputStream, HashMap<String, Object> reportExtraParams, List<? extends ReportData> reportData, ReportOutputOption outputOption, HttpServletResponse response) {

        try{

            JasperPrint jasperPrint = getJasperPrintResult(reportExtraParams, reportInputStream, outputOption, reportData);

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

            String reportOutputFileName = reportExtraParams != null ? ((String) reportExtraParams.get(Constants.REPORT_NAME)) : "";

            //Jasper export handler
            export(outputOption, reportOutputFileName, jasperPrint, response, byteArrayOutputStream);

            //Write to servlet output stream
            writeToServletOutputStream(response, byteArrayOutputStream);

        } catch (JRException e) {

            e.printStackTrace();
        }
    }

    private JasperPrint getJasperPrintResult(HashMap<String, Object> reportExtraParams, InputStream reportInputStream, ReportOutputOption outputOption,
                                             List<? extends ReportData> reportData) throws JRException {

        JasperReport jasperReport = (JasperReport) JRLoader.loadObject(reportInputStream);

        //removes pagination in exel output. It allows to remove repeating column header
        if(reportExtraParams != null && (outputOption != null && !outputOption.equals(ReportOutputOption.PDF))){
            reportExtraParams.put(JRParameter.IS_IGNORE_PAGINATION, Boolean.TRUE);
        }
        JasperPrint jasperPrint = null;

        //Check for empty report data. Fill empty datasource when there is no data to fill
        if(reportData.size() == 0){

            jasperPrint = JasperFillManager.fillReport(jasperReport, reportExtraParams , new JREmptyDataSource());

        } else{

            jasperPrint =  JasperFillManager.fillReport(jasperReport, reportExtraParams , new JRBeanCollectionDataSource(reportData,false));

        }

        return jasperPrint;
    }
    /**
     *
     * @param outputOption
     * @param jasperPrint
     * @param response
     * @param byteArrayOutputStream
     * @return
     */
    private HttpServletResponse export(ReportOutputOption outputOption, String outputFileName, JasperPrint jasperPrint, HttpServletResponse response, ByteArrayOutputStream byteArrayOutputStream) {

        switch (outputOption){

            case PDF:
                return exportPdf(jasperPrint, outputFileName, response, byteArrayOutputStream);
            case XLS:
                return exportXls(jasperPrint, outputFileName, response, byteArrayOutputStream);
            case CSV:
                return exportCsv(jasperPrint, outputFileName, response, byteArrayOutputStream);
            case HTML:
                return exportHtml(jasperPrint, outputFileName, response, byteArrayOutputStream);
        }

        return response;
    }

    /**
     *
     * @param outputOption
     * @param jasperPrint
     * @return
     */
    private ByteArrayOutputStream export(ReportOutputOption outputOption, String outputFileName, JasperPrint jasperPrint) {

        switch (outputOption){

            case PDF:
                return exportPdfByteArrayOutputStream(jasperPrint, outputFileName);
            case XLS:
                return exportXlsByteArrayOutputStream(jasperPrint, outputFileName);
            case CSV:
                return exportCsvByteArrayOutputStream(jasperPrint, outputFileName);
            case HTML:
                return exportHtmlByteArrayOutputStream(jasperPrint, outputFileName);
        }

        return null;
    }

    /**
     * @param jasperPrint
     * @param outputFileName
     * @return
     */
    private ByteArrayOutputStream exportHtmlByteArrayOutputStream(JasperPrint jasperPrint, String outputFileName) {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        JRHtmlExporter exporter = new JRHtmlExporter();
        exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
        exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, byteArrayOutputStream);
        exporter.setParameter(JRHtmlExporterParameter.IS_USING_IMAGES_TO_ALIGN, Boolean.FALSE);
        exporter.setParameter(JRHtmlExporterParameter.ZOOM_RATIO, 1.5F);

        try {

            exporter.exportReport();

            return byteArrayOutputStream;

        } catch (JRException e) {
            throw new RuntimeException(e);
        }
    }

    private ByteArrayOutputStream exportCsvByteArrayOutputStream(JasperPrint jasperPrint, String outputFileName) {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        JRCsvExporter exporter = new JRCsvExporter();

        exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
        exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, byteArrayOutputStream);

        exporter.setParameter(JRXlsAbstractExporterParameter.IS_ONE_PAGE_PER_SHEET, Boolean.FALSE);
        exporter.setParameter(JRXlsAbstractExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.TRUE);
        exporter.setParameter(JRXlsAbstractExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_COLUMNS, Boolean.TRUE);
        exporter.setParameter(JRXlsAbstractExporterParameter.IS_WHITE_PAGE_BACKGROUND, Boolean.FALSE);
        exporter.setParameter(JRXlsExporterParameter.IS_IGNORE_GRAPHICS, Boolean.FALSE);

        try {

            exporter.exportReport();

            return byteArrayOutputStream;

        } catch (JRException e) {
            throw new RuntimeException(e);
        }
    }

    private ByteArrayOutputStream exportXlsByteArrayOutputStream(JasperPrint jasperPrint, String outputFileName) {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        JRXlsExporter exporter = new JRXlsExporter();

        exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
        exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, byteArrayOutputStream);

        exporter.setParameter(JRXlsAbstractExporterParameter.IS_ONE_PAGE_PER_SHEET, Boolean.FALSE);
        exporter.setParameter(JRXlsAbstractExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.TRUE);
        exporter.setParameter(JRXlsAbstractExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_COLUMNS, Boolean.TRUE);
        exporter.setParameter(JRXlsAbstractExporterParameter.IS_WHITE_PAGE_BACKGROUND, Boolean.FALSE);
        exporter.setParameter(JRXlsExporterParameter.IS_IGNORE_GRAPHICS, Boolean.FALSE);
        exporter.setParameter(JRXlsAbstractExporterParameter.IS_DETECT_CELL_TYPE, Boolean.TRUE);


        try {

            exporter.exportReport();

            return byteArrayOutputStream;

        } catch (JRException e) {
            throw new RuntimeException(e);
        }
    }

    private ByteArrayOutputStream exportPdfByteArrayOutputStream(JasperPrint jasperPrint, String outputFileName) {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        JRPdfExporter exporter = new JRPdfExporter();

        exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
        exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, byteArrayOutputStream);

        try {

            exporter.exportReport();

            return byteArrayOutputStream;

        } catch (JRException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Handles exporting of jasper print to pdf format
     * @param jasperPrint
     * @param response
     * @param byteArrayOutputStream
     * @return
     */
    private HttpServletResponse exportPdf(JasperPrint jasperPrint, String outputFileName, HttpServletResponse response, ByteArrayOutputStream byteArrayOutputStream){

        JRPdfExporter exporter = new JRPdfExporter();

        exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
        exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, byteArrayOutputStream);

        try {
            exporter.exportReport();

        } catch (JRException e) {
            throw new RuntimeException(e);
        }

        String fileName = outputFileName.isEmpty()? "openlmisReport.pdf" : outputFileName+".pdf";
        response.setHeader("Content-Disposition", "inline; filename="+ fileName);

        response.setContentType(Constants.MEDIA_TYPE_PDF);
        response.setContentLength(byteArrayOutputStream.size());

        return response;
    }

    private HttpServletResponse exportHtml(JasperPrint jasperPrint, String outputFileName, HttpServletResponse response, ByteArrayOutputStream byteArrayOutputStream){

        JRHtmlExporter exporter = new JRHtmlExporter();
        exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
        exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, byteArrayOutputStream);
        exporter.setParameter(JRHtmlExporterParameter.IS_USING_IMAGES_TO_ALIGN, Boolean.FALSE);
        exporter.setParameter(JRHtmlExporterParameter.ZOOM_RATIO, 1.5F);

        try {
            exporter.exportReport();

        } catch (JRException e) {
            throw new RuntimeException(e);
        }

        response.setContentType(Constants.MEDIA_TYPE_HTML);
        response.setContentLength(byteArrayOutputStream.size());

        return response;
    }


        /**
         *
         * @param jasperPrint
         * @param response
         * @param byteArrayOutputStream
         * @return
         */
    public HttpServletResponse exportXls(JasperPrint jasperPrint, String outputFileName, HttpServletResponse response, ByteArrayOutputStream byteArrayOutputStream){

        JRXlsExporter exporter = new JRXlsExporter();

        exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
        exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, byteArrayOutputStream);

        exporter.setParameter(JRXlsAbstractExporterParameter.IS_ONE_PAGE_PER_SHEET, Boolean.FALSE);
        exporter.setParameter(JRXlsAbstractExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.TRUE);
        exporter.setParameter(JRXlsAbstractExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_COLUMNS, Boolean.TRUE);
        exporter.setParameter(JRXlsAbstractExporterParameter.IS_WHITE_PAGE_BACKGROUND, Boolean.FALSE);
        exporter.setParameter(JRXlsExporterParameter.IS_IGNORE_GRAPHICS, Boolean.FALSE);
        exporter.setParameter(JRXlsAbstractExporterParameter.IS_DETECT_CELL_TYPE, Boolean.TRUE);


        try {
            exporter.exportReport();

        } catch (JRException e) {
            throw new RuntimeException(e);
        }

        String fileName = outputFileName.isEmpty()? "openlmisReport.xls" : outputFileName+".xls";
        response.setHeader("Content-Disposition", "inline; filename=" + fileName);

        response.setContentType(Constants.MEDIA_TYPE_EXCEL);
        response.setContentLength(byteArrayOutputStream.size());

        return response;
    }

    /**
     *
     * @param jasperPrint
     * @param response
     * @param byteArrayOutputStream
     * @return
     */
    public HttpServletResponse exportCsv(JasperPrint jasperPrint, String outputFileName, HttpServletResponse response, ByteArrayOutputStream byteArrayOutputStream){

        JRCsvExporter exporter = new JRCsvExporter();

        exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
        exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, byteArrayOutputStream);

        exporter.setParameter(JRXlsAbstractExporterParameter.IS_ONE_PAGE_PER_SHEET, Boolean.FALSE);
        exporter.setParameter(JRXlsAbstractExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.TRUE);
        exporter.setParameter(JRXlsAbstractExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_COLUMNS, Boolean.TRUE);
        exporter.setParameter(JRXlsAbstractExporterParameter.IS_WHITE_PAGE_BACKGROUND, Boolean.FALSE);
        exporter.setParameter(JRXlsExporterParameter.IS_IGNORE_GRAPHICS, Boolean.FALSE);


        try {
            exporter.exportReport();

        } catch (JRException e) {
            throw new RuntimeException(e);
        }

        String fileName = outputFileName.isEmpty()? "openlmisReport.csv" : outputFileName+".csv";
        response.setHeader("Content-Disposition", "inline; filename=" + fileName);

        response.setContentType(Constants.MEDIA_TYPE_EXCEL);
        response.setContentLength(byteArrayOutputStream.size());

        return response;
    }


    /**
     * @param response
     * @param byteArrayOutputStream
     */
    private void writeToServletOutputStream(HttpServletResponse response, ByteArrayOutputStream byteArrayOutputStream) {
        ServletOutputStream outputStream = null;
        try {

            outputStream = response.getOutputStream();

            byteArrayOutputStream.writeTo(outputStream);

        }catch (Exception e) {
            throw new RuntimeException(e);

        }finally {
            if(outputStream != null){
                try {
                    outputStream.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
