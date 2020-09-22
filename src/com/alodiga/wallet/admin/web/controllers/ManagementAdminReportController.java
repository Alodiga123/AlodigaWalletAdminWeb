package com.alodiga.wallet.admin.web.controllers;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFPatriarch;
import org.apache.poi.hssf.usermodel.HSSFPicture;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Caption;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Doublebox;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listhead;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Menubar;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Textbox;
import com.alodiga.wallet.common.ejb.ReportEJB;
import com.alodiga.wallet.common.ejb.UserEJB;
import com.alodiga.wallet.common.model.Product;
import com.alodiga.wallet.common.model.Report;
import com.alodiga.wallet.common.model.ReportParameter;
import com.alodiga.wallet.common.model.User;
import com.alodiga.wallet.common.utils.EJBServiceLocator;
import com.alodiga.wallet.common.utils.EjbConstants;
import com.alodiga.wallet.admin.web.generic.controllers.GenericAbstractController;
import com.alodiga.wallet.admin.web.utils.AccessControl;
import com.alodiga.wallet.admin.web.utils.WebConstants;

public class ManagementAdminReportController extends GenericAbstractController {

    private static final long serialVersionUID = -9145887024839938515L;
    UserEJB userEjb = null;
    private Label lblUser;
    private Listbox lbxReport;
    private ReportEJB reportEJB = null;
    private UserEJB userEJB = null;
    protected Grid gridParams;
    protected Rows filasParams;
    protected Row rowInsumo;
    protected List<ReportParameter> reportParameters = null;
    protected List<Product> products = null;
    private String sql;
    private Listhead listhead;
    private List<String> params;
    private List<String> paramsList;
    private Menubar menuBar;
    private Report auxReport;
    private User currentUser;
    private Boolean isWallet = false;
    private Label lbldescription;
    private Label lbldescription2;
    private Groupbox gbxReport;
    Boolean isStoreAll = false;
    private String falseVal = "FALSE";
    private String trueVal = "TRUE";

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        initialize();
        loadData();
    }

    @Override
    public void initialize() {
        try {
            reportEJB = (ReportEJB) EJBServiceLocator.getInstance().get(EjbConstants.REPORT_EJB);
            userEJB = (UserEJB) EJBServiceLocator.getInstance().get(EjbConstants.USER_EJB);
            currentUser = AccessControl.loadCurrentUser();
            userEjb = (UserEJB) EJBServiceLocator.getInstance().get(EjbConstants.USER_EJB);
            currentUser = AccessControl.loadCurrentUser();
        } catch (Exception ex) {
            this.showMessage("sp.error.general", true, ex);
        }
    }

    public void clearFields() {
        gridParams.getRows().getChildren().clear();
        lbxReport.getChildren().clear();
    }

    public void loadData() {
       initReport();
    }

        public void onClick$mniLogout() {
        AccessControl.logout();
        Executions.sendRedirect("./admin.zul");
    }

    private void fillComboTstatus(Combobox combo, Object object, Long objectId, String label, String selectedId) {
        Comboitem item = new Comboitem();
        item.setValue(object);
        item.setDescription(object.toString());
        item.setLabel(label);
        item.setParent(combo);
        item.setId(selectedId.toString());
    }

    private void fillComboBoolean(Combobox combo, Object object, Long objectId, String label, String selectedId) {
        Comboitem item = new Comboitem();
        item.setValue(object);
        if (object.equals(trueVal)) {
            item.setLabel(Labels.getLabel("sp.common.enabled"));
        } else {
            item.setLabel(Labels.getLabel("sp.common.disabled"));
        }
        item.setParent(combo);
        item.setId(selectedId.toString());
    }

    private void fillComboAccounts(Combobox combo, Object object, Long objectId, String label, String selectedId) {
        Comboitem item = new Comboitem();
//        Account a = (Account) object;
        item.setValue(object);
        item.setLabel(label);
//        item.setDescription(a.getDistributorHasLevel().get(0).getLevel().getName());
        item.setParent(combo);
        item.setId(selectedId.toString());
    }

    public void initReport() {
        filasParams.setParent(gridParams);
        auxReport = (Report) Sessions.getCurrent().getAttribute(WebConstants.SESSION_REPORT);
        Caption caption = new Caption();
        caption.setLabel(auxReport.getName());
        caption.setParent(gbxReport);
        paintDescription(auxReport.getDescription());
        reportParameters = auxReport.getReportParameters();
        Row rowParent = new Row();
        for (ReportParameter reportParameter : reportParameters) {
            int param = Integer.valueOf(reportParameter.getParameterTypeId().getId().toString());
            rowParent = new Row();
            Label lbl1 = new Label();
            lbl1.setValue(reportParameter.getName());
            lbl1.setParent(rowParent);
            rowParent.setParent(filasParams);

            switch (param) {
                case WebConstants.INTEGER:
                    Intbox intValue1 = new Intbox();
                    intValue1.setParent(rowParent);
                    break;
                case WebConstants.FLOAT:
                    Doublebox doubleValue = new Doublebox();
                    doubleValue.setParent(rowParent);
                    break;
                case WebConstants.STRING:
                    Textbox txtvalue1 = new Textbox();
                    txtvalue1.setParent(rowParent);
                    break;
                case WebConstants.DATE:
                    Datebox dtbDate = new Datebox();
                    dtbDate.setFormat("yyyy/MM/dd");
                    dtbDate.setValue(new Timestamp(new java.util.Date().getTime()));
                    dtbDate.setParent(rowParent);
                    break;
                case WebConstants.BOOLEAN:
                    Combobox cmbBoolean = new Combobox();
                    List<String> cStatus = new ArrayList<String>();
                    cStatus.add(falseVal);
                    cStatus.add(trueVal);
                    for (String tS : cStatus) {
                        fillComboBoolean(cmbBoolean, tS, 1l, tS, "" + "notText");
                    }
                    cmbBoolean.setSelectedIndex(1);
                    cmbBoolean.setParent(rowParent);
                    break;

                    case WebConstants.T_STATUS:
                    Combobox cmbTStatus = new Combobox();
                    List<String> tStatus = new ArrayList<String>();
                    //tStatus.add(TransactionStatus.APPROVED.toString());
                     Comboitem item1 = new Comboitem();
                    item1.setValue(Labels.getLabel("sp.common.solicitud.all"));
                    item1.setLabel(Labels.getLabel("sp.common.solicitud.all"));
                    item1.setId(WebConstants.PREFIX_ALL_STORE);
                    item1.setParent(cmbTStatus);
                    cmbTStatus.setParent(rowParent);
                    for (String tS : tStatus) {
                        fillComboTstatus(cmbTStatus, tS, 1l, tS, "" + "notText");
                    }

                    break;
               
                case WebConstants.CHECK_WALLET:
                    isWallet = true;
                    break;
            }
        }
    }

    public void onClick$btnBack() {
        Sessions.getCurrent().removeAttribute(WebConstants.SESSION_REPORT);
        Executions.sendRedirect("managementReport.zul");
    }

    public void onClick$btnRunReport() {
        try {
            lbxReport.getChildren().clear();
            sql = auxReport.getQuery();
            pivotListbox();
        } catch (Exception ex) {
            showMessage("sp.error.general", true, ex);
        }
    }

    public void onClick$btnExport() {
        try {
            auxReport = (Report) Sessions.getCurrent().getAttribute(WebConstants.SESSION_REPORT);
            exportExcel(lbxReport, auxReport.getName() + ".xls");
        } catch (Exception ex) {
            showMessage("sp.error.general", true, ex);
        }
    }

    public void pivotListbox() throws IOException {
        params = getParams();
        if (!isWallet) {
            Connection conn = null;
            PreparedStatement ps = null;
            ResultSet rs = null;
            try {
                InitialContext initalContext = new InitialContext();
                DataSource dataSource = (DataSource) initalContext.lookup(WebConstants.CONNECTIONPOOL);
                conn = dataSource.getConnection();
                String[] tempSql = sql.split("UNION");
                replaceSql(tempSql.length);
                //System.out.println(sql);
                if (isStoreAll) {
                    sql = replaceSql(sql);
                    isStoreAll = false;
                }
                ps = conn.prepareStatement(sql);
                rs = ps.executeQuery();
                ResultSetMetaData md = rs.getMetaData();
                int count = md.getColumnCount();
                List<List<String>> rows = new ArrayList<List<String>>();
                while (rs.next()) {
                    List<String> values = new ArrayList<String>();
                    for (int i = 1; i <= count; i++) {
                        values.add(rs.getString(i) != null ? rs.getString(i).toString() : "");
                    }
                    rows.add(values);
                }
                listhead = new Listhead();
                for (int i = 1; i <= count; i++) {
                    Listheader listheader = new Listheader();
                    listheader.setWidth("150px");
                    listheader.setLabel(md.getColumnLabel(i));
                    listheader.setAlign("center");
                    if (i == 1) {
                        listheader.setSort("auto");
                        listheader.setSortDirection("ascending");
                    }
                    listheader.setParent(listhead);
                }
                listhead.setParent(lbxReport);
                for (List<String> row : rows) {
                    Listitem item = new Listitem();
                    item.setValue(row);
                    for (int i = 0; i < row.size(); i++) {
                        item.appendChild(new Listcell(row.get(i)));
                    }
                    item.setParent(lbxReport);
                }
                if (rows.isEmpty()) {
                    Messagebox.show(Labels.getLabel("sp.error.empty.list"), "Information", Messagebox.OK, Messagebox.INFORMATION);
                }
            } catch (Exception ex) {
                showMessage("sp.error.general", true, ex);
            } finally {
                try {
                    if (conn != null) {
                        conn.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            listhead = new Listhead();
            Listheader listheader = new Listheader();
            listheader.setWidth("160px");
            listheader.setLabel("Provider");
            listheader.setAlign("center");
            listheader.setSort("auto");
            listheader.setSortDirection("ascending");
            listheader.setParent(listhead);
            listhead.setParent(lbxReport);
            listheader = new Listheader();
            listheader.setWidth("160px");
            listheader.setLabel("available balance");
            listheader.setAlign("center");
            listheader.setSort("auto");
            listheader.setSortDirection("ascending");
            listheader.setParent(listhead);
            listhead.setParent(lbxReport);
            try {
            }catch (Exception ex) {
                showMessage("sp.error.general", true, ex);
            }
            java.util.Map<String, String> topUpWalletResponse = null;
            try {
            } catch (Exception ex) {
                showMessage("sp.error.general", true, ex);
            }
    
            isWallet = false;
        }
    }

    private List<String> getParams() {
        Integer countDate = 0;
        List<String> paramRows = new ArrayList<String>();
        paramsList = new ArrayList<String>();
        List Rows = filasParams.getChildren();
        for (int i = 0; i < Rows.size(); i++) {
            Row currentRow = (Row) Rows.get(i);
            if (currentRow.getLastChild() instanceof Intbox) {
                Intbox ibx = (Intbox) currentRow.getLastChild();
                if (ibx.getText() != null) {
                    paramRows.add(ibx.getText());
                }
            } else if (currentRow.getLastChild() instanceof Combobox) {
                Combobox cbx = (Combobox) currentRow.getLastChild();
                if (cbx.getText() != null && !cbx.getText().equals(Labels.getLabel("sp.common.select"))) {
                    if (cbx.getSelectedItem().getValue().equals(trueVal)) {
                        if (cbx.getSelectedItem().getValue().equals(trueVal)) {
                            paramRows.add("1");
                        } else {
                            paramRows.add("0");
                        }
                    } else {
                        if (cbx.getSelectedItem().getValue().equals(Labels.getLabel("sp.common.solicitud.all"))) {
                            isStoreAll = true;
                        }
                        String comboId;
                        if (cbx.getSelectedItem().getId().equals("prov__ProviderAmount")) {
                            isWallet = true;
                        } else {
                            isWallet = false;
                        }
                        if (cbx.getSelectedItem().getId().equals("notText")) {
                            comboId = cbx.getSelectedItem().getValue().toString();
                            paramRows.add(comboId);
                        } else {
                            comboId = cbx.getSelectedItem().getId().substring(4);
                            paramRows.add(comboId);
                        }
                    }
                } else {
                    //lblInfo.setValue(Labels.getLabel("error.field.cannotNull"));
                }

            } else if (currentRow.getLastChild() instanceof Doublebox) {
                Doublebox dbx = (Doublebox) currentRow.getLastChild();
                if (dbx.getText() != null) {
                    paramRows.add(dbx.getText());
                } else {
                    //lblInfo.setValue(Labels.getLabel("error.field.cannotNull"));
                }
            } else if (currentRow.getLastChild() instanceof Textbox) {
                Textbox tbx = (Textbox) currentRow.getLastChild();
                if (tbx.getText() != null) {
                    paramRows.add(tbx.getText());
                } else {
                    //lblInfo.setValue(Labels.getLabel("error.field.cannotNull"));
                }
            } else if (currentRow.getLastChild() instanceof Datebox) {
                Datebox dtx = (Datebox) currentRow.getLastChild();
                if (dtx.getText() != null) {
                    countDate++;
                    if (countDate == 1) {
                        paramRows.add(dtx.getText() + " 00:00:00");
                    }
                    if (countDate == 2) {
                        paramRows.add(dtx.getText() + " 23:59:59");
                    }
                } else {
                    //lblInfo.setValue(Labels.getLabel("error.field.cannotNull"));
                }
            } else if (currentRow.getLastChild() instanceof Checkbox) {
                Checkbox dtx = (Checkbox) currentRow.getLastChild();
                if (dtx.isChecked()) {
                    paramRows.add("1");
                } else {
                    paramRows.add("0");
                }
            }
        }
        return paramRows;
    }

    private void replaceSql(int size) {
        int j = 0;
        while (j < size) {
            for (int i = 0; i < params.size(); i++) {
                sql = sql.replaceFirst("\\?", "'" + params.get(i) + "'");
            }
            for (int i = 0; i < params.size(); i++) {
                sql = sql.replaceFirst("\\¿", "'" + params.get(i - i) + "'");
            }
            for (int i = 0; i < paramsList.size(); i++) {
                sql = sql.replaceFirst("\\#", paramsList.get(i));
            }
            j++;
        }
    }

    private Comboitem addItemCombobox(String value) {
        Comboitem item = new Comboitem();
        return item;
    }

//    private String countList(List<?> evaluateList) {
//        String value = null;
//        for (int i = 0; i < evaluateList.size(); i++) {
//            AbstractSPEntity abstractMLCEntity = (AbstractSPEntity) evaluateList.get(i);
//            if (i < evaluateList.size() - 1) {
//                value = (value != null ? value : "") + abstractMLCEntity.getPk().toString() + ",";
//            } else {
//                value = (value != null ? value : "") + abstractMLCEntity.getPk().toString();
//            }
//        }
//        return value;
//    }

    public void exportExcel(Listbox box, String nomeFile) throws IOException {
        HSSFWorkbook workbook = new HSSFWorkbook();

        HSSFSheet sheet = workbook.createSheet("Distribution");
        HSSFPatriarch patriarch = sheet.createDrawingPatriarch();

        HSSFClientAnchor anchor = new HSSFClientAnchor();
        anchor.setAnchor((short) 0, 0, 0, 0, (short) 1, 1, 1023, 255);
        anchor.setAnchorType(2);
        String webPath = Sessions.getCurrent().getWebApp().getRealPath("");
        webPath += "/images/img-alodiga-logo.png";
        try {
            File files = new File(webPath);
            HSSFPicture picture = patriarch.createPicture(anchor, loadPicture(files, workbook));
        } catch (FileNotFoundException e) {
            //nothing
        }
        HSSFRow row = sheet.createRow(0);
        HSSFFont fontRedBold = workbook.createFont();
        HSSFFont fontNormal = workbook.createFont();
        fontRedBold.setColor(HSSFFont.COLOR_RED);
        fontRedBold.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        fontNormal.setColor(HSSFFont.COLOR_NORMAL);
        fontNormal.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);

        // Create the style
        HSSFCellStyle cellStyleRedBold = workbook.createCellStyle();
        HSSFCellStyle cellStyleNormal = workbook.createCellStyle();
        cellStyleRedBold.setFont(fontRedBold);
        cellStyleNormal.setFont(fontNormal);

        HSSFRow row2 = sheet.createRow(2);
        HSSFCell cell0 = row2.createCell(0);
        cell0.setCellValue(Labels.getLabel("sp.file.commission.topUp.letterHead1"));

        HSSFRow rowTitle = sheet.createRow(0);
        HSSFCell cellTitle = rowTitle.createCell(4);
        cellTitle.setCellValue(auxReport.getName().toString());

        // headers
        int i = 3;
        row = sheet.createRow(3);
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
        int x = 4;
        int y = 3;
        for (Object item : box.getItems()) {
            row = sheet.createRow(x);
            y = 3;
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

        FileOutputStream fOut = new FileOutputStream(nomeFile);

        // Write the Excel sheet
        workbook.write(fOut);
        fOut.flush();

        // Done deal. Close it.
        fOut.close();
        File file = new File(nomeFile);
        Filedownload.save(file, "XLS");
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

    private static String replaceSql(String sql1) {
        String constantFirst = WebConstants.ID_PARAM_REPORT_OPEN;
        String constantSecond = WebConstants.ID_PARAM_REPORT_CLOSED;
        int init = sql1.indexOf(constantFirst);
        if (init == -1) {
            return sql1;
        }
        int end = sql1.indexOf(constantSecond) + constantSecond.length();
        String sqlCut = sql1.substring(init, end);
        String sqlReplace = sql1.replace(sqlCut, "");
        //System.out.println(sqlReplace);
        return sqlReplace;
    }

//        private static String replaceSqlAccount(String sql1) {
//        String constantFirst = WebConstants.ID_PARAM_REPORT_OPEN;
//        String constantSecond = WebConstants.ID_PARAM_REPORT_CLOSED;
//        int init = sql1.indexOf(constantFirst);
//        if (init == -1) {
//            return sql1;
//        }
//        int end = sql1.indexOf(constantSecond) + constantSecond.length();
//        String sqlCut = sql1.substring(init, end);
//        String sqlReplace = sql1.replace(sqlCut, "");
//        //System.out.println(sqlReplace);
//        return sqlReplace;
//    }
        

    public void paintDescription(String desc) {
        if (desc.length() >= 180) {

            lbldescription.setValue(desc.substring(0, 180) + "-");
            lbldescription2.setValue(desc.substring(180, desc.length()));
        } else {
            lbldescription.setValue(desc);
        }
    }
   }

