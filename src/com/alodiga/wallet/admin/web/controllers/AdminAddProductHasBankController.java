package com.alodiga.wallet.admin.web.controllers;

import com.alodiga.wallet.admin.web.generic.controllers.GenericAbstractAdminController;
import com.alodiga.wallet.admin.web.utils.WebConstants;
import com.alodiga.wallet.common.ejb.ProductEJB;
import com.alodiga.wallet.common.ejb.UtilsEJB;
import com.alodiga.wallet.common.exception.EmptyListException;
import com.alodiga.wallet.common.exception.GeneralException;
import com.alodiga.wallet.common.exception.NullParameterException;
import com.alodiga.wallet.common.exception.RegisterNotFoundException;
import com.alodiga.wallet.common.model.BankHasProduct;
import com.alodiga.wallet.common.model.Product;
import com.alodiga.wallet.common.utils.EJBServiceLocator;
import com.alodiga.wallet.common.utils.EjbConstants;
import java.util.List;
import com.alodiga.wallet.common.genericEJB.EJBRequest;
import com.alodiga.wallet.common.model.Bank;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Window;

public class AdminAddProductHasBankController extends GenericAbstractAdminController {

    private static final long serialVersionUID = -9145887024839938516L;
    private Listbox lbxRecords;
    private Combobox cmbProduct;
    private Combobox cmbBank;
    private Label lblProduct;
    private ProductEJB productEJB = null;
    private UtilsEJB utilsEJB = null;
    private BankHasProduct bankHasProductParam;
    private Button btnSave;
    public Window winAdminAddCommerceCategory;
    private Integer eventType;
    Product product;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        eventType = (Integer) Sessions.getCurrent().getAttribute(WebConstants.EVENTYPE);
        switch (eventType) {
            case WebConstants.EVENT_EDIT:
                bankHasProductParam = (BankHasProduct) Sessions.getCurrent().getAttribute("object");
                break;
            case WebConstants.EVENT_VIEW:
                bankHasProductParam = (BankHasProduct) Sessions.getCurrent().getAttribute("object");
                break;
            case WebConstants.EVENT_ADD:
                bankHasProductParam = null;
                break;
        }
        initialize();
    }

    @Override
    public void initialize() {
        super.initialize();
        try {
            productEJB = (ProductEJB) EJBServiceLocator.getInstance().get(EjbConstants.PRODUCT_EJB);
            utilsEJB = (UtilsEJB) EJBServiceLocator.getInstance().get(EjbConstants.UTILS_EJB);
            loadData();
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void clearFields() {
    }

    public void blockFields() {
        cmbBank.setDisabled(true);
        //cmbProduct.setDisabled(true);
        btnSave.setVisible(false);
    }

    public Boolean validateEmpty() {
        if (cmbBank.getSelectedItem() == null) {
            cmbBank.setFocus(true);
            this.showMessage("sp.crud.product.bank.select", true, null);
        } else {
            return true;
        }
        return false;
    }

    private void saveBankHasProduct(BankHasProduct _bankHasProduct) {
        BankHasProduct bankHasProduct = null;
        try {
            if (_bankHasProduct != null) {
                bankHasProduct = _bankHasProduct;
            } else {
                bankHasProduct = new BankHasProduct();
            }

            bankHasProduct.setProductId(product);
            bankHasProduct.setBankId((Bank) cmbBank.getSelectedItem().getValue());
            //request1.setParam(bankHasProduct);

            if (validate(bankHasProduct)) {
                bankHasProduct = productEJB.saveBankHasProduct(bankHasProduct);
                bankHasProductParam = bankHasProduct;
                eventType = WebConstants.EVENT_EDIT;
                this.showMessage("sp.common.save.success", false, null);

                if (eventType == WebConstants.EVENT_ADD) {
                    btnSave.setVisible(false);
                } else {
                    btnSave.setVisible(true);
                }
                
                EventQueues.lookup("updateProductHasBank", EventQueues.APPLICATION, true).publish(new Event(""));
            } else {
                this.showMessage("sp.crud.product.exist", true, null);
            }

        } catch (ConstraintViolationException ex) {
            for (ConstraintViolation actual : ex.getConstraintViolations()) {
                System.out.println(actual.toString());
            }
            this.showMessage("sp.crud.product.exist", true, null);
        } catch (NullParameterException ex) {
            showError(ex);
        } catch (GeneralException ex) {
            showError(ex);
        } catch (RegisterNotFoundException ex) {
            showError(ex);
        }
    }

    public void onClick$btnSave() throws RegisterNotFoundException, NullParameterException, GeneralException {
        if (validateEmpty()) {
            switch (eventType) {
                case WebConstants.EVENT_ADD:
                    saveBankHasProduct(null);
                    break;
                case WebConstants.EVENT_EDIT:
                    saveBankHasProduct(bankHasProductParam);
                    break;
                default:
                    break;
            }
        }
    }

    public void onClick$btnBack() {
        winAdminAddCommerceCategory.detach();
    }

    public void loadData() {
        switch (eventType) {
            case WebConstants.EVENT_EDIT:
                loadFiels(eventType);
                loadCmbBank(eventType);
                break;
            case WebConstants.EVENT_VIEW:
                blockFields();
                loadFiels(eventType);
                loadCmbBank(eventType);
                break;
            case WebConstants.EVENT_ADD:
                //cmbProduct.setDisabled(true);
                loadCmbBank(eventType);
                loadFiels(eventType);
                break;
            default:
                break;
        }
    }

      private void loadFiels(Integer eventType) {
        //product = (Sessions.getCurrent().getAttribute("object") != null) ? (Product) Sessions.getCurrent().getAttribute("object") : null;
        AdminProductController admin = new AdminProductController();
        product= admin.getProductParent();
        lblProduct.setValue(product.getName());
    }
    
    private void loadCmbProduct(Integer eventType) {
        EJBRequest request1 = new EJBRequest();
        List<Product> productList;
        List<Product> productListAux = new ArrayList<>();
        try {
            productList = productEJB.getProducts(request1);
            if (!productList.isEmpty()) {
                for (Product product_ : productList) {
                    if (product_.isIndHasAssociatedBank()) {
                        productListAux.add(product_);
                    }
                }
            }
            loadGenericCombobox(productListAux, cmbProduct, "name", eventType, Long.valueOf(bankHasProductParam != null ? bankHasProductParam.getProductId().getId() : 0));
            productList = null;
            System.gc();
        } catch (EmptyListException ex) {
            showError(ex);
            ex.printStackTrace();
        } catch (GeneralException ex) {
            showError(ex);
            ex.printStackTrace();
        } catch (NullParameterException ex) {
            showError(ex);
            ex.printStackTrace();
        }
    }

    private void loadCmbBank(Integer eventType) {
        EJBRequest request1 = new EJBRequest();
        List<Bank> bankList;
        try {
            bankList = utilsEJB.getBank(request1);
            loadGenericCombobox(bankList, cmbBank, "name", eventType, Long.valueOf(bankHasProductParam != null ? bankHasProductParam.getBankId().getId() : 0));
            bankList = null;
            System.gc();
        } catch (EmptyListException ex) {
            showError(ex);
            ex.printStackTrace();
        } catch (GeneralException ex) {
            showError(ex);
            ex.printStackTrace();
        } catch (NullParameterException ex) {
            showError(ex);
            ex.printStackTrace();
        }
    }

    private boolean validate(BankHasProduct bankHasProduct) {
        EJBRequest request1 = new EJBRequest();
        List<BankHasProduct> list;

        try {
            list = (List<BankHasProduct>) productEJB.getBankHasProductByID(bankHasProduct);

            if (list.isEmpty()) {
                return true;
            }

        } catch (GeneralException ex) {
            Logger.getLogger(AdminAddProductHasBankController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (EmptyListException ex) {
            Logger.getLogger(AdminAddProductHasBankController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NullParameterException ex) {
            Logger.getLogger(AdminAddProductHasBankController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
}
