package com.alodiga.wallet.admin.web.utils;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listhead;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

public class PDFUtil {

    private static String FILE = "/home/usuario/turbinas.pdf";
    private static Font catFont = new Font(Font.FontFamily.TIMES_ROMAN, 16,
            Font.BOLD);
    private static Font redFont = new Font(Font.FontFamily.TIMES_ROMAN, 10,
            Font.NORMAL, BaseColor.RED);
    private static Font subFont = new Font(Font.FontFamily.TIMES_ROMAN, 14,
            Font.BOLD);
    private static Font smallBold = new Font(Font.FontFamily.TIMES_ROMAN, 8,
            Font.BOLD);
    private static Font smallSimple = new Font(Font.FontFamily.TIMES_ROMAN, 8,
            Font.NORMAL);

    public static void main(String[] args) {
        try {
            Document document = new Document();
            document.setPageSize(PageSize.A4.rotate());
            PdfWriter.getInstance(document, new FileOutputStream(FILE));
            document.open();
            //addTitlePage(document);
            //addContent(document);
            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void exportPdf(String pdfName, String title, Listbox box, int quitCellTotheBack) {
        try {
            try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                Filedownload.save(createDocument(out, title, box, quitCellTotheBack), null, pdfName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static byte[] createDocument(ByteArrayOutputStream out, String title, Listbox box, int quitCellTotheBack) throws DocumentException, IOException {
        Document document = new Document();
        document.setPageSize(PageSize.A4.rotate());
        PdfWriter.getInstance(document, out);
        document.open();
        //addTitlePage(document);
        addContent(document, title, box, quitCellTotheBack);
        document.close();
        return out.toByteArray();
    }

    private static void addContent(Document document, String title, Listbox box, int quitCelltoBack) throws DocumentException {

        Paragraph subPara = new Paragraph();
        // add a table
        createTableTitle(subPara, title);
        // add a table
        document.add(subPara);

        Paragraph paragraphOne = new Paragraph("     ");
        document.add(paragraphOne);

        Paragraph subPara2 = new Paragraph();
        createTable(subPara2, box, quitCelltoBack);
        document.add(subPara2);
        // now add all this to the document

    }

    private static int getLenghtColumByBox(Listbox box) {
        int i = 0;

        for (Object head : box.getHeads()) {
            for (Object header : ((Listhead) head).getChildren()) {
                i++;
            }
        }
        return i;
    }

    private static void createTable(Paragraph subCatPart, Listbox box, int quitCellToBack)
            throws BadElementException {
        int lenghtCol = getLenghtColumByBox(box) - quitCellToBack;
        PdfPTable table = new PdfPTable(lenghtCol);
        table.setWidthPercentage(90f);
        int i = 0;
        for (Object head : box.getHeads()) {
            for (Object header : ((Listhead) head).getChildren()) {
                if (lenghtCol > i) {
                    String h = ((Listheader) header).getLabel();
                    Phrase phrase = new Phrase(h, smallBold);
                    PdfPCell c1 = new PdfPCell(phrase);
                    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(c1);
                    i++;
                } else {
                    break;
                }
            }
        }

        table.setHeaderRows(1);
        // dettaglio
        int x = 8;
        int y = 0;
        for (Object item : box.getItems()) {
            y = 0;
            for (Object lbCell : ((Listitem) item).getChildren()) {
                if (lenghtCol > y) {
                    String h;
                    h = ((Listcell) lbCell).getLabel();
                    Phrase phrase = new Phrase(h, smallSimple);
                    PdfPCell cell = new PdfPCell(phrase);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cell);

                    y++;
                } else {
                    break;
                }

            }
            x++;
        }
        subCatPart.add(table);
    }

    private static Image getLogo() throws BadElementException {
        //String imageUrl = "http://mxl4361xd8:8080/ServicesProviderWeb/images/logo_header.png";
        String webPath = Sessions.getCurrent().getWebApp().getRealPath("");
        webPath += "/images/logo_header.png";
        Image image2 = null;
        try {
            image2 = Image.getInstance(webPath);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image2;
    }

    private static void createTableTitle(Paragraph subCatParTitle, String title)
            throws BadElementException {
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(90f);
        PdfPCell c1 = new PdfPCell(getLogo());
        c1.setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell(c1);
        PdfPCell c2 = new PdfPCell();
        c2.addElement(new Phrase(title));
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = format.format(new Date());
        c2.addElement(new Phrase(dateString));
        c2.setVerticalAlignment(Element.ALIGN_MIDDLE);
        c2.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(c2);
        subCatParTitle.add(table);
    }

}
