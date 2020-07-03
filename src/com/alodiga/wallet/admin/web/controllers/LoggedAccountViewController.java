package com.alodiga.wallet.admin.web.controllers;
//package com.alodiga.services.provider.web.controllers;
//
//
//import com.alodiga.services.provider.commons.ejbs.ProductEJB;
//import com.alodiga.services.provider.commons.ejbs.TransactionEJB;
//import com.alodiga.services.provider.commons.ejbs.UserEJB;
//import com.alodiga.services.provider.commons.models.Account;
//import com.alodiga.services.provider.commons.models.Category;
//import com.alodiga.services.provider.commons.models.Product;
//import com.alodiga.services.provider.commons.models.Profile;
//import com.alodiga.services.provider.commons.utils.EJBServiceLocator;
//import com.alodiga.services.provider.commons.utils.EjbConstants;
//import com.alodiga.services.provider.commons.utils.QueryConstants;
//import com.alodiga.services.provider.web.generic.controllers.GenericSPController;
//import com.alodiga.services.provider.web.utils.AccessControl;
//import com.alodiga.services.provider.web.utils.BarChartEngine;
//import java.text.DecimalFormat;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import org.zkoss.zk.ui.Component;
//import org.zkoss.zk.ui.util.GenericForwardComposer;
//import org.zkoss.util.resource.Labels;
//import org.zkoss.zul.Button;
//import org.zkoss.zul.CategoryModel;
//import org.zkoss.zul.Cell;
//import org.zkoss.zul.Chart;
//import org.zkoss.zul.Label;
//import org.zkoss.zul.Div;
//import org.zkoss.zul.Grid;
//import org.zkoss.zul.Row;
//import org.zkoss.zul.Rows;
//import org.zkoss.zul.SimpleCategoryModel;
//import org.zkoss.zul.impl.ChartEngine;
//
//public class LoggedAccountViewController extends GenericForwardComposer implements GenericSPController {
//
//    static final long serialVersionUID = -9145887024839938515L;
//    private Label lblInfo;
//    private Div divDistributor;
//    private Div divMasterStore;
//    private Div divRetailStore1;
//    private Div divRetailStore2;
//    private TransactionEJB transactionEJB;
//    private UserEJB userEJB;
//    private ProductEJB productEJB;
//    private String currencySymbol = "";
//    private Account currentAccount;
//    private Button btnPurchaseEP1;
//    private Button btnRechargeTU1;
//    private Button btnPurchaseEP2;
//    private Button btnRechargeTU2;
//    private Button btnBillPayment1;
//    private Button btnBillPayment2;
//    private Button btnAddPinFrees1;
//    private Button btnAddPinFrees2;
//    private Button buttonPurchase;
//    private Grid grdSummary;
//    private Chart chtSales;
//    private Button buttonPayment;
//
//
//        @Override
//    public void doAfterCompose(Component comp) throws Exception {
//        super.doAfterCompose(comp);
//
//        initialize();
//        loadData();
//        manageCentralPanel();
//    }
//
//    public void initialize() {
//        try {
//            userEJB = (UserEJB) EJBServiceLocator.getInstance().get(EjbConstants.USER_EJB);
//            transactionEJB = (TransactionEJB) EJBServiceLocator.getInstance().get(EjbConstants.TRANSACTION_EJB);
//            productEJB = (ProductEJB) EJBServiceLocator.getInstance().get(EjbConstants.PRODUCT_EJB);
//            currentAccount = AccessControl.loadCurrentAccount();
//            currencySymbol = currentAccount.getEnterprise().getCurrency().getSymbol();
//        } catch (Exception ex) {
//            lblInfo.setValue(Labels.getLabel("sp.error.general"));
//            ex.getStackTrace();
//        }
//    }
//
//    private void manageCentralPanel() {
////        if (currentAccount.getCurrentProfile().getId().equals(Profile.ACCOUNT) || currentAccount.getCurrentProfile().getId().equals(Profile.SUB_ACCOUNT)) {
////            divDistributor.setVisible(true);
////        }
////        } else if (currentAccount.getCurrentProfile().getId().equals(Profile.RETAIL_STORE)) {
////            Account accountParent = null;
////            Map params = new HashMap<String, Object>();
////            params.put(QueryConstants.PARAM_ACCOUNT, currentAccount.getId());
////            request.setParams(params);
////
////            try {
////                List<Product> products = productEJB.getProductsByAccount(request);
////                accountParent = userEJB.loadCurrentParentByAccount(currentAccount.getId());
////                if (accountParent.getCurrentProfile().getId().equals(Profile.MASTER_STORE)) {
////                    divRetailStore1.setVisible(true);
////                    for (Product p : products) {
////                        if (p.getEnabled() && p.getCategory().getId().equals(Category.ELECTRONIC_PIN)) {
////                            btnPurchaseEP1.setVisible(true);
////                            btnAddPinFrees1.setVisible(true);
////                        }
////                    }
////                } else {
////                    divRetailStore2.setVisible(true);
////                    for (Product p : products) {
////                        if (p.getEnabled() && p.getCategory().getId().equals(Category.ELECTRONIC_PIN)) {
////                            btnPurchaseEP2.setVisible(true);
////                            btnAddPinFrees2.setVisible(true);
////                        }
////                    }
////                }
////            } catch (Exception ex) {
////                divRetailStore1.setVisible(true);
////                ex.printStackTrace();
////            }
////        }
//    }
//
//    public void loadData() {
//        try {
//            if (currentAccount.getIsPrePaid()) {
//                buttonPurchase.setVisible(true);
//                buttonPayment.setVisible(false);
//            } else {
//                buttonPurchase.setVisible(false);
//            }
//            if (AccessControl.getNeedUpdate()) {
//                setSummaryData();
//            }
//        } catch (Exception ex) {
//            ex.getStackTrace();
//        }
//    }
//
//    private void setSummaryData() throws Exception {
//
//        CategoryModel salesCategoryModel = new SimpleCategoryModel();
//
//        Rows rows = new Rows();
//        Float todaySales = 0F;
//        List<Float> _sales = transactionEJB.getEntireSalesAmountByAccount(currentAccount.getId());
//        for (int i = 1; i <= 5; i++) {
//            String msg1 = "";
//            String msgSales = "";
//            Float sales = 0F;
//
//            switch (i) {
//                case 1:
//                   msg1 = Labels.getLabel("sp.common.stadistics");
//                    msgSales = Labels.getLabel("sp.common.sales");
//                    break;
//                case 2://TODAY
//                    msg1 = Labels.getLabel("sp.common.today");
//                    todaySales = _sales.get(0);
//                    salesCategoryModel.setValue(msg1, "", todaySales);
//                    msgSales = new DecimalFormat("#.##").format(todaySales) + currencySymbol;
//                    break;
//                case 3://YESTERDAY
//                    msg1 = Labels.getLabel("sp.common.yesterday");
//                    sales = _sales.get(1);
//                    sales = sales - todaySales;
//                    salesCategoryModel.setValue(msg1, "", sales);
//                    msgSales = new DecimalFormat("#.##").format(sales) + currencySymbol;
//                    break;
//                case 4://LAST 7 DAYS
//                    msg1 = Labels.getLabel("sp.common.last7days");
//                    sales = _sales.get(2);
//                    salesCategoryModel.setValue(msg1, "", sales);
//                    msgSales = new DecimalFormat("#.##").format(sales) + currencySymbol;
//                    break;
//                case 5://LAST 15 DAYS
//                    msg1 = Labels.getLabel("sp.common.last15days");
//                    sales = _sales.get(3);
//                    salesCategoryModel.setValue(msg1, "", sales);
//                    msgSales = new DecimalFormat("#.##").format(sales) + currencySymbol;
//                    break;
//                default:
//                    break;
//            }
//            Row row = new Row();
//            Label label = new Label(msg1);
//            label.setStyle("font-weight: bold;color: #424242;");
//            Cell cell = new Cell();
//            cell.appendChild(label);
//            row.appendChild(cell);
//
//                label = new Label(msgSales);
//                if (i == 1) {
//                    label.setStyle("font-weight: bold;color: #424242;");
//                }
//                cell = new Cell();
//                cell.appendChild(label);
//                row.appendChild(cell);
//
//                chtSales.setModel(salesCategoryModel);
////                chtSales.setEngine(new BarChartEngine(Labels.getLabel("sp.chart.sales.title")));
//                chtSales.setEngine((ChartEngine) new BarChartEngine(Labels.getLabel("sp.chart.sales.title")));    
//            if (i == 1) {
//                label.setStyle("font-weight: bold;color: #424242;");
//            }
//            cell = new Cell();
//            cell.appendChild(label);
//            row.appendChild(cell);
//            rows.appendChild(row);
//            grdSummary.appendChild(rows);
//        }
//    }
//}