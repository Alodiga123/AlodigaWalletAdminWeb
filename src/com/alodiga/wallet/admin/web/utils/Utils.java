package com.alodiga.wallet.admin.web.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFPatriarch;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listhead;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;

import com.alodiga.wallet.common.model.Language;
import com.alodiga.wallet.common.utils.Constants;




public class Utils {

    public static boolean validatePhoneNumberUSA(String number) {
        boolean valid = false;
        if (number.length() == 11 && number.substring(0, 1).equals(String.valueOf(Constants.USA_CODE)) && GeneralUtils.isNumeric(number)) {
            valid = true;
        }
        return valid;

    }

    public static String getPromotionTypeName(String promotionType) {
        String value = "";
        if (promotionType.equals("INITIAL_PURCHASE")) {
            value = Labels.getLabel("sp.promotionType.initialPurchase");
        } else if (promotionType.equals("ACCOUNT_CREATION")) {
            value = Labels.getLabel("sp.promotionType.accountCreation");
        } else if (promotionType.equals("GOAL_ACCOMPLISHMENT")) {
            value = Labels.getLabel("sp.promotionType.goalAccomplishment");
        }

        return value;
    }

//    public static String getPeriodName(Long periodId) {
//        String value = "";
//        if (periodId.equals(Period.DIARY)) {
//            value = Labels.getLabel("wallet.period.diary");
//        } else if (periodId.equals(Period.WEEKLY)) {
//            value = Labels.getLabel("sp.period.weekly");
//        } else if (periodId.equals(Period.BIWEEKLY)) {
//            value = Labels.getLabel("sp.period.biweekly");
//        } else if (periodId.equals(Period.MONTHLY)) {
//            value = Labels.getLabel("sp.period.monthly");
//        } else if (periodId.equals(Period.QUARTERLY)) {
//            value = Labels.getLabel("sp.period.quartely");
//        }
//
//        return value;
//    }

    public static String getLanguageName(Long languageId) {
        String value = "";

        if (languageId.equals(Language.ENGLISH)) {
            value = Labels.getLabel("wallet.common.english");
        } else {
            value = Labels.getLabel("wallet.common.language.spanish");
        }
        return value;
    }

    public static void exportExcel(Listbox box, String nameFile) throws Exception {
        try {


            HSSFWorkbook workbook = new HSSFWorkbook();

            HSSFSheet sheet = workbook.createSheet("Sheet 1");
            HSSFPatriarch patriarch = sheet.createDrawingPatriarch();

            HSSFClientAnchor anchor = new HSSFClientAnchor();
            anchor.setAnchor((short) 0, 0, 0, 0, (short) 1, 1, 1023, 255);
            anchor.setAnchorType(2);
            String webPath = Sessions.getCurrent().getWebApp().getRealPath("");
            webPath += "/images/img-alodiga-logo.png";
            try {
                File files = new File(webPath);
                patriarch.createPicture(anchor, loadPicture(files, workbook));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                //nothing
            }
            HSSFRow row = sheet.createRow(0);
            HSSFFont fontRedBold = workbook.createFont();
            HSSFFont fontNormal = workbook.createFont();
            HSSFFont fontBold = workbook.createFont();
            fontRedBold.setColor(HSSFFont.COLOR_RED);
            fontRedBold.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
            fontNormal.setColor(HSSFFont.COLOR_NORMAL);
            fontNormal.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
            fontBold.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
            // Create the style
            HSSFCellStyle cellStyleRedBold = workbook.createCellStyle();
            HSSFCellStyle cellStyleBold = workbook.createCellStyle();
            HSSFCellStyle cellStyleNormal = workbook.createCellStyle();
            cellStyleRedBold.setFont(fontRedBold);
            cellStyleNormal.setFont(fontNormal);
            cellStyleBold.setFont(fontBold);

            HSSFRow row2 = sheet.createRow(3);
            HSSFCell cell0 = row2.createCell(0);
            cell0.setCellStyle(cellStyleBold);
            cell0.setCellValue(Labels.getLabel("wallet.page.title"));

            HSSFRow rowReportName = sheet.createRow(4);
            HSSFCell cellReportName = rowReportName.createCell(0);
            cellReportName.setCellStyle(cellStyleBold);
            cellReportName.setCellValue(Labels.getLabel("wallet.common.report.title") + ":");
            HSSFCell cellTitle = rowReportName.createCell(1);
            cellTitle.setCellValue(nameFile);

            HSSFRow rowReportDate = sheet.createRow(5);
            HSSFCell cellDate = rowReportDate.createCell(0);
            cellDate.setCellValue(Labels.getLabel("wallet.common.date") + ":");
            cellDate.setCellStyle(cellStyleBold);
            HSSFCell cellReportNameValue = rowReportDate.createCell(1);
            cellReportNameValue.setCellValue(GeneralUtils.dateCalendar2String(Calendar.getInstance()));

            nameFile += ".xls";
            // headers
            int i = 0;
            row = sheet.createRow(7);
            for (Object head : box.getHeads()) {
                for (Object header : ((Listhead) head).getChildren()) {
                    String h = ((Listheader) header).getLabel();
                    HSSFCell cell = row.createCell(i);
                    cell.setCellStyle(cellStyleRedBold);
                    cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                    cell.setCellValue(h);
                    i++;
                }
            }
            // dettaglio
            int x = 8;
            int y = 0;
            for (Object item : box.getItems()) {
                row = sheet.createRow(x);
                y = 0;
                for (Object lbCell : ((Listitem) item).getChildren()) {
                    String h;
                    h = ((Listcell) lbCell).getLabel();
                    HSSFCell cell = row.createCell(y);
                    cell.setCellStyle(cellStyleNormal);
                    cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                    cell.setCellValue(h);
                    y++;
                }
                x++;
            }
            FileOutputStream fOut = new FileOutputStream(nameFile);
            // Write the Excel sheet
            workbook.write(fOut);
            fOut.flush();
            // Done deal. Close it.
            fOut.close();
            File file = new File(nameFile);
            Filedownload.save(file, "XLS");
        } catch (Exception ex) {
            throw ex;
        }
    }

    private static int loadPicture(File path, HSSFWorkbook wb) throws IOException {
        int pictureIndex;
        FileInputStream fis = null;
        ByteArrayOutputStream bos = null;
        try {
            // read in the image file
            fis = new FileInputStream(path);
            bos = new ByteArrayOutputStream();
            int c;
            // copy the image bytes into the ByteArrayOutputStream
            while ((c = fis.read()) != -1) {
                bos.write(c);
            }
            // add the image bytes to the workbook
            pictureIndex = wb.addPicture(bos.toByteArray(), HSSFWorkbook.PICTURE_TYPE_PNG);
        } finally {
            if (fis != null) {
                fis.close();
            }
            if (bos != null) {
                bos.close();
            }
        }
        return pictureIndex;
    }

    public static String getTransactionStatusName(String transactionStatusId) {
        String value = "";
//        if (transactionStatusId.equals(Transaction.STATUS_PROCESSED)) {
//            value = Labels.getLabel("wallet.common.approved");
//        } else if (transactionStatusId.equals(Transaction.STATUS_PROCESSED)) {
//            value = Labels.getLabel("wallet.common.canceled");
//        } else if (transactionStatusId.equals(Transaction.STATUS_CANCELED)) {
//            value = Labels.getLabel("wallet.common.canceled");
//        } else if (transactionStatusId.equals(Transaction.STATUS_FAILED)) {
//            value = Labels.getLabel("wallet.common.failed");
//        } else if (transactionStatusId.equals(Transaction.STATUS_REJECTED_BY_PAYMENT)) {
//            value = Labels.getLabel("msj.error.rejectedByPayment");
//        }
        return value;
    }

    public static String getStatus(boolean status) {
        String value = "";
        if (status) {
            value = Labels.getLabel("wallet.common.enabled");
        } else {
            value = Labels.getLabel("wallet.common.disabled");
        }
        return value;
    }

    public static String getTransactionTypeName(Long transactionTypeId) {
        String value = "";
        return value;
    }

        
      
}
