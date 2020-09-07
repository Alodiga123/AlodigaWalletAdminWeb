package com.alodiga.wallet.admin.web.controllers;

import java.util.List;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Textbox;
import com.alodiga.wallet.admin.web.generic.controllers.GenericAbstractAdminController;
import com.alodiga.wallet.admin.web.utils.WebConstants;
import com.alodiga.wallet.common.ejb.AccessControlEJB;
import com.alodiga.wallet.common.ejb.UtilsEJB;
import com.alodiga.wallet.common.ejb.ProductEJB;
import com.alodiga.wallet.common.exception.EmptyListException;
import com.alodiga.wallet.common.exception.GeneralException;
import com.alodiga.wallet.common.exception.NullParameterException;
import com.alodiga.wallet.common.genericEJB.EJBRequest;
import com.alodiga.wallet.common.manager.PermissionManager;
import com.alodiga.wallet.common.model.Category;
import com.alodiga.wallet.common.model.Enterprise;
import com.alodiga.wallet.common.model.Permission;
import com.alodiga.wallet.common.model.Product;
import com.alodiga.wallet.common.model.ProductIntegrationType;
import com.alodiga.wallet.common.model.Profile;
import com.alodiga.wallet.common.utils.EJBServiceLocator;
import com.alodiga.wallet.common.utils.EjbConstants;
import org.zkoss.util.resource.Labels;
import org.zkoss.zul.Button;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Toolbarbutton;

public class AdminProductController extends GenericAbstractAdminController {

    private static final long serialVersionUID = -9145887024839938515L;
    private Combobox cmbEnterprise;
    private Combobox cmbCategory;
    private Combobox cmbProductIntegrationType;
    private Textbox txtName;
    private Textbox txtSymbol;
    private Textbox txtReferenceCode;
    private Textbox txtRatesUrl;
    private Textbox txtAccessNumberUrl;
    private Radio rEnabledYes;
    private Radio rEnabledNo;
    private Radio rIsFreeYes;
    private Radio rIsFreeNo;
    private Radio rIsAlocashProductYes;
    private Radio rIsAlocashProductNo;
    private Radio rIsPayTopUpYes;
    private Radio rIsPayTopUpNo;
    private Radio rIsExchangeProductYes;
    private Radio rIsExchangeProductNo;
    private Radio rIsRemettenceYes;
    private Radio rIsRemettenceNo;
    private Radio rTaxIncludeYes;
    private Radio rTaxIncludeNo;
    private Radio rIsPaymentInfoYes;
    private Radio rIsPaymentInfoNo;
    private UtilsEJB utilsEJB = null;
    private ProductEJB productEJB;
    private AccessControlEJB accessEJB = null;
    private Button btnSave;
    private Toolbarbutton tbbTitle;
    private Product productParam;
    public static Product productParent = null;
    private Integer eventType;
    private boolean editingPassword = false;
    private Tab tabProductHasBank;
    private Tab tabCommissionByProduct;
    private Profile currentProfile;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        eventType = (Integer) Sessions.getCurrent().getAttribute(WebConstants.EVENTYPE);
        if (eventType == WebConstants.EVENT_ADD) {
            productParam = null;
        } else {
            productParam = (Sessions.getCurrent().getAttribute("object") != null) ? (Product) Sessions.getCurrent().getAttribute("object") : null;
            productParent = productParam;
        }
        initialize();
    }

    @Override
    public void initialize() {
        super.initialize();
        switch (eventType) {
            case WebConstants.EVENT_EDIT:
                tbbTitle.setLabel(Labels.getLabel("sp.crud.product.edit"));
                break;
            case WebConstants.EVENT_VIEW:
                tbbTitle.setLabel(Labels.getLabel("sp.crud.product.view"));
                break;
            case WebConstants.EVENT_ADD:
                tabCommissionByProduct.setDisabled(true);
                tabProductHasBank.setDisabled(true);
                tbbTitle.setLabel(Labels.getLabel("sp.crud.product.add"));
                break;
            default:
                break;
        }
        try {
            utilsEJB = (UtilsEJB) EJBServiceLocator.getInstance().get(EjbConstants.UTILS_EJB);
            productEJB = (ProductEJB) EJBServiceLocator.getInstance().get(EjbConstants.PRODUCT_EJB);
            loadData();
        } catch (Exception ex) {
            showError(ex);
        }
    }
    
    public Product getProductParent() {
        return this.productParent;
    }
    
    public void setProductParent(Product product) {
        this.productParent=product;
    }
    
    public Integer getEventType() {
        return this.eventType;
    }

    public void disableTab() {
          if (productParam.getIndHasAssociatedBank() != null) {
            tabProductHasBank.setDisabled(true); 
          }
    }
    public void clearFields() {
        txtName.setRawValue(null);
        txtSymbol.setRawValue(null);
        txtReferenceCode.setRawValue(null);
        txtRatesUrl.setRawValue(null);
        txtAccessNumberUrl.setRawValue(null);
    }

    private void loadFields(Product product) {
        try {
            txtName.setText(product.getName());
            txtSymbol.setText(product.getSymbol());
            txtReferenceCode.setText(product.getReferenceCode());
            txtRatesUrl.setText(product.getRatesUrl());
            txtAccessNumberUrl.setText(product.getAccessNumberUrl());
            btnSave.setVisible(true);

            if (product.getEnabled() == true) {
                rEnabledYes.setChecked(true);
            } else {
                rEnabledNo.setChecked(true);
            }

            if (product.getIsFree() == true) {
                rIsFreeYes.setChecked(true);
            } else {
                rIsFreeNo.setChecked(true);
            }

            if (product.getIsAlocashProduct() == true) {
                rIsAlocashProductYes.setChecked(true);
            } else {
                rIsAlocashProductNo.setChecked(true);
            }

            if (product.isIsPayTopUp() == true) {
                rIsPayTopUpYes.setChecked(true);
            } else {
                rIsPayTopUpNo.setChecked(true);
            }

            if (product.isIsExchangeProduct() == true) {
                rIsExchangeProductYes.setChecked(true);
            } else {
                rIsExchangeProductNo.setChecked(true);
            }

            if (product.isIsRemettence() == true) {
                rIsRemettenceYes.setChecked(true);
            } else {
                rIsRemettenceYes.setChecked(true);
            }

            if (product.getTaxInclude() == true) {
                rTaxIncludeYes.setChecked(true);
            } else {
                rTaxIncludeNo.setChecked(true);
            }

            if (product.isIsPaymentInfo() == true) {
                rIsPaymentInfoYes.setChecked(true);
            } else {
                rIsPaymentInfoNo.setChecked(true);
            }
            
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void blockFields() {
        cmbEnterprise.setReadonly(true);
        cmbCategory.setReadonly(true);
        cmbProductIntegrationType.setReadonly(true);
        txtName.setReadonly(true);
        txtSymbol.setReadonly(true);
        txtReferenceCode.setReadonly(true);
        txtRatesUrl.setReadonly(true);
        txtAccessNumberUrl.setReadonly(true);
        btnSave.setVisible(false);
    }

    public boolean validateEmpty() {
        this.showMessage("", false, null);
        if (cmbEnterprise.getSelectedItem() == null) {
            this.showMessage("sp.crud.bank.enterprise.error", true, null);
            cmbEnterprise.setFocus(true);
            return false;
        }

        if (cmbCategory.getSelectedItem() == null) {
            this.showMessage("sp.common.category.error", true, null);
            cmbCategory.setFocus(true);
            return false;
        }

        if (cmbProductIntegrationType.getSelectedItem() == null) {
            this.showMessage("sp.crud.product.integration.type.error", true, null);
            cmbProductIntegrationType.setFocus(true);
            return false;
        }

        if (txtName.getText().isEmpty()) {
            txtName.setFocus(true);
            this.showMessage("sp.crud.product.name.error", true, null);
            return false;
        }

        if (txtSymbol.getText().isEmpty()) {
            txtSymbol.setFocus(true);
            this.showMessage("sp.crud.product.symbol.error", true, null);
            return false;
        }

        if (txtReferenceCode.getText().isEmpty()) {
            txtReferenceCode.setFocus(true);
            this.showMessage("sp.crud.product.referenceCode.error", true, null);
            return false;
        }

        if (txtRatesUrl.getText().isEmpty()) {
            txtRatesUrl.setFocus(true);
            this.showMessage("sp.crud.product.ratesUrl.error", true, null);
            return false;
        }

        if (txtAccessNumberUrl.getText().isEmpty()) {
            txtAccessNumberUrl.setFocus(true);
            this.showMessage("sp.crud.product.accessNumberUrl.error", true, null);
            return false;
        }

        if (!(rEnabledYes.isChecked() || rEnabledNo.isChecked())) {
            this.showMessage("sp.common.enabled.error", true, null);
            rEnabledYes.setFocus(true);
            return false;
        }

        if (!(rIsFreeYes.isChecked() || rIsFreeNo.isChecked())) {
            this.showMessage("sp.crud.product.isFree.error", true, null);
            rIsFreeYes.setFocus(true);
            return false;
        }

        if (!(rIsAlocashProductYes.isChecked() || rIsAlocashProductNo.isChecked())) {
            this.showMessage("sp.crud.product.isAlocashProduct.error", true, null);
            rIsAlocashProductYes.setFocus(true);
            return false;
        }

        if (!(rIsPayTopUpYes.isChecked() || rIsPayTopUpNo.isChecked())) {
            this.showMessage("sp.crud.product.isPayTopUp.error", true, null);
            rIsPayTopUpYes.setFocus(true);
            return false;
        }

        if (!(rIsExchangeProductYes.isChecked() || rIsExchangeProductNo.isChecked())) {
            this.showMessage("sp.crud.product.isExchangeProduct.error", true, null);
            rIsExchangeProductYes.setFocus(true);
            return false;
        }

        if (!(rIsRemettenceYes.isChecked() || rIsRemettenceNo.isChecked())) {
            this.showMessage("sp.crud.product.isRemettence.error", true, null);
            rIsRemettenceYes.setFocus(true);
            return false;
        }

        if (!(rTaxIncludeYes.isChecked() || rTaxIncludeNo.isChecked())) {
            this.showMessage("sp.crud.product.taxInclude.error", true, null);
            rTaxIncludeYes.setFocus(true);
            return false;
        }

        if (!(rIsPaymentInfoYes.isChecked() || rIsPaymentInfoNo.isChecked())) {
            this.showMessage("sp.crud.product.isPaymentInfo.error", true, null);
            rIsPaymentInfoYes.setFocus(true);
            return false;
        }

        return true;

    }

    private void saveProduct(Product _product) {
        try {
            Product product = null;

            if (_product != null) {
                product = _product;
            } else {
                product = new Product();
            }

            product.setEnterpriseId((Enterprise) cmbEnterprise.getSelectedItem().getValue());
            product.setCategoryId((Category) cmbCategory.getSelectedItem().getValue());
            product.setProductIntegrationTypeId((ProductIntegrationType) cmbProductIntegrationType.getSelectedItem().getValue());
            product.setName(txtName.getText());
            product.setTaxInclude(rTaxIncludeYes.isChecked() ? true : false);
            product.setEnabled(rEnabledYes.isChecked() ? true : false);
            product.setReferenceCode(txtReferenceCode.getText());
            product.setRatesUrl(txtRatesUrl.getText());
            product.setAccessNumberUrl(txtAccessNumberUrl.getText());
            product.setIsFree(rIsFreeYes.isChecked() ? true : false);
            product.setIsAlocashProduct(rIsAlocashProductYes.isChecked() ? true : false);
            product.setSymbol(txtSymbol.getText());
            product.setIsPayTopUp(rIsPayTopUpYes.isChecked() ? true : false);
            product.setIsExchangeProduct(rIsExchangeProductYes.isChecked() ? true : false);
            product.setIsRemettence(rIsRemettenceYes.isChecked() ? true : false);
            product.setIsPaymentInfo(rIsPayTopUpYes.isChecked() ? true : false);
            EJBRequest request1 = new EJBRequest();
            request1.setParam(product);
            product = productEJB.saveProduct(request1);
            productParent = product;
            this.showMessage("sp.common.save.success", false, null);
            if (eventType == WebConstants.EVENT_ADD) {
                btnSave.setVisible(false);
                tabCommissionByProduct.setDisabled(false);
                tabProductHasBank.setDisabled(false);
            } else {
                btnSave.setVisible(true);
            }
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void onClick$btnCancel() {
        clearFields();
    }

    public void onClick$btnSave() {
        if (validateEmpty()) {
            switch (eventType) {
                case WebConstants.EVENT_ADD:
                    saveProduct(productParam);
                    break;
                case WebConstants.EVENT_EDIT:
                    saveProduct(productParam);
                    break;
                default:
                    break;
            }
        }
    }

    public void loadData() {
        switch (eventType) {
            case WebConstants.EVENT_EDIT:
                loadFields(productParam);
                loadCmbProductIntegrationType(eventType);
                loadCmbEnterprise(eventType);
                loadCmbCategory(eventType);
                break;
            case WebConstants.EVENT_VIEW:
                loadFields(productParam);
                blockFields();
                loadCmbProductIntegrationType(eventType);
                loadCmbEnterprise(eventType);
                loadCmbCategory(eventType);
                break;
            case WebConstants.EVENT_ADD:
                loadCmbProductIntegrationType(eventType);
                loadCmbEnterprise(eventType);
                loadCmbCategory(eventType);
                break;
            default:
                break;
        }
    }

    private void loadCmbCategory(Integer evenInteger) {
        EJBRequest request1 = new EJBRequest();
        List<Category> category;
        try {
            category = productEJB.getCategories(request1);
            loadGenericCombobox(category, cmbCategory, "name", evenInteger, Long.valueOf(productParam != null ? productParam.getCategoryId().getId() : 0));
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

    private void loadCmbEnterprise(Integer evenInteger) {
        EJBRequest request1 = new EJBRequest();
        List<Enterprise> enterprise;
        try {
            enterprise = utilsEJB.getEnterprises(request1);
            loadGenericCombobox(enterprise, cmbEnterprise, "name", evenInteger, Long.valueOf(productParam != null ? productParam.getEnterpriseId().getId() : 0));
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

    private void loadCmbProductIntegrationType(Integer evenInteger) {
        EJBRequest request1 = new EJBRequest();
        List<ProductIntegrationType> productIntegrationType;
        try {
            productIntegrationType = productEJB.getProductIntegrationType(request1);
            loadGenericCombobox(productIntegrationType, cmbProductIntegrationType, "name", evenInteger, Long.valueOf(productParam != null ? productParam.getProductIntegrationTypeId().getId() : 0));
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

}
