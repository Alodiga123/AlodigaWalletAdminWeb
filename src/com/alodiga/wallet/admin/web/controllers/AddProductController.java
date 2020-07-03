//package com.alodiga.wallet.admin.web.controllers;
//
//import java.sql.Timestamp;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import org.zkoss.zk.ui.Component;
//import org.zkoss.zk.ui.Executions;
//import org.zkoss.zk.ui.Sessions;
//import org.zkoss.zk.ui.SuspendNotAllowedException;
//import org.zkoss.zul.Button;
//import org.zkoss.zul.Checkbox;
//import org.zkoss.zul.Combobox;
//import org.zkoss.zul.Comboitem;
//import org.zkoss.zul.Intbox;
//import org.zkoss.zul.Textbox;
//import org.zkoss.zul.Window;
//
//import com.alodiga.services.provider.commons.ejbs.AuditoryEJB;
//import com.alodiga.services.provider.commons.ejbs.ProductEJB;
//import com.alodiga.services.provider.commons.ejbs.UtilsEJB;
//import com.alodiga.services.provider.commons.exceptions.RegisterNotFoundException;
//import com.alodiga.services.provider.commons.genericEJB.EJBRequest;
//import com.alodiga.services.provider.commons.managers.PermissionManager;
//import com.alodiga.services.provider.commons.models.Audit;
//import com.alodiga.services.provider.commons.models.Category;
//import com.alodiga.services.provider.commons.models.Enterprise;
//import com.alodiga.services.provider.commons.models.Event;
//import com.alodiga.services.provider.commons.models.Permission;
//import com.alodiga.services.provider.commons.models.Product;
//import com.alodiga.services.provider.commons.models.User;
//import com.alodiga.services.provider.commons.utils.EJBServiceLocator;
//import com.alodiga.services.provider.commons.utils.EjbConstants;
//import com.alodiga.services.provider.commons.utils.GeneralUtils;
//import com.alodiga.services.provider.commons.utils.QueryConstants;
//import com.alodiga.wallet.admin.web.generic.controllers.GenericAbstractAdminController;
//import com.alodiga.wallet.admin.web.utils.AccessControl;
//import com.alodiga.wallet.admin.web.utils.WebConstants;
//
//public class AddProductController extends GenericAbstractAdminController {
//
//    private static final long serialVersionUID = -9145887024839938515L;
//    private Combobox cmbEnterprise;
//    private Checkbox cbxEnabled;
//    private Textbox txtAmount;
//    private Textbox txtRealAmount;
//    private Textbox txtInitialAmount;
//    private Textbox txtBachNumber;
//    private Textbox txtUbicationFolder;
//    private Textbox txtUbicationBox;
//    private Textbox txtactNpNsn;
//    private Textbox txtDescription;
//    private Textbox txtPartNumber;
//    private Intbox intStockMax;
//    private Intbox intStockMin;
//
//    private ProductEJB productEJB = null;
//    private UtilsEJB utilsEJB = null;
//    private Product productParam;
//    private List<Enterprise> enterprises;
//    private Button btnAddDenomination;
//    private Button btnSave;
//    private Window winAddProductView;
//    private User user;
//    private AuditoryEJB auditoryEJB;
//    private String ipAddress;
//    private Category category; 
//
//    @Override
//    public void doAfterCompose(Component comp) throws Exception {
//        super.doAfterCompose(comp);
//        productParam = (Sessions.getCurrent().getAttribute("object") != null) ? (Product) Sessions.getCurrent().getAttribute("object") : null;
//        category = (Sessions.getCurrent().getAttribute("category") != null) ? (Category) Sessions.getCurrent().getAttribute("category") : null;
//        user = AccessControl.loadCurrentUser();
//        initialize();
//    }
//
//    @Override
//    public void initView(int eventType, String adminView) {
//        super.initView(eventType, "sp.crud.product");
//    }
//
//    @Override
//    public void initialize() {
//        super.initialize();
//        try {
//			ipAddress = Executions.getCurrent().getRemoteAddr();
//			auditoryEJB = (AuditoryEJB) EJBServiceLocator.getInstance().get(EjbConstants.AUDITORY_EJB);
//			productEJB = (ProductEJB) EJBServiceLocator.getInstance().get(EjbConstants.PRODUCT_EJB);
//			utilsEJB = (UtilsEJB) EJBServiceLocator.getInstance().get(EjbConstants.UTILS_EJB);
//			loadData();
//		} catch (Exception ex) {
//            showError(ex);
//        }
//    }
//
//    public void clearFields() {
//        
//    }
//
//
//    public void blockFields() {
//    	cbxEnabled.setChecked(false);
//		txtRealAmount.setReadonly(true);
//		txtInitialAmount.setReadonly(true);
//		txtBachNumber.setReadonly(true);
//		txtUbicationFolder.setReadonly(true);
//		txtUbicationBox.setReadonly(true);
//		txtactNpNsn.setReadonly(true);
//		txtDescription.setReadonly(true);
//		txtPartNumber.setReadonly(true);
//    	intStockMax.setReadonly(true);
//    	intStockMin.setReadonly(true);
//        cbxEnabled.setDisabled(true);
//        cmbEnterprise.setDisabled(true);
//        btnAddDenomination.setVisible(false);
//        btnSave.setVisible(false);
//    }
//
//    public Boolean validateEmpty() {
//    	    if (txtUbicationFolder.getText().isEmpty()) {
//           	txtUbicationFolder.setFocus(true);
//               this.showMessage("sp.error.field.cannotNull", true, null);
//           }else if (txtUbicationBox.getText().isEmpty()) {
//           	txtUbicationBox.setFocus(true);
//               this.showMessage("sp.error.field.cannotNull", true, null);
//           }else if (txtDescription.getText().isEmpty()) {
//           	txtDescription.setFocus(true);
//               this.showMessage("sp.error.field.cannotNull", true, null);
//           }else if (txtPartNumber.getText().isEmpty()) {
//           	txtPartNumber.setFocus(true);
//               this.showMessage("sp.error.field.cannotNull", true, null); 
//           }else if (intStockMax.getText().isEmpty()) {
//           	intStockMax.setFocus(true);
//               this.showMessage("sp.error.field.cannotNull", true, null);
//           }else if (intStockMin.getText().isEmpty()) {
//           	intStockMin.setFocus(true);
//               this.showMessage("sp.error.field.cannotNull", true, null);
//           }else if (txtPartNumber.getText().isEmpty()) {
//           	txtPartNumber.setFocus(true);
//               this.showMessage("sp.error.field.cannotNull", true, null);
//           }else if (intStockMin.getValue()>intStockMax.getValue()) {
//           	intStockMin.setFocus(true);
//               this.showMessage("sp.common.stock.min.error", true, null);
//           }else if (!GeneralUtils.isNumeric(txtAmount.getText())) {
//           	txtAmount.setFocus(true);
//               this.showMessage("sp.error.field.number", true, null);
//           } else {
//            return true;
//        }
//        return false;
//
//    }
//
//    private void loadEnterprises(Enterprise enterprise) {
//        try {
//            cmbEnterprise.getItems().clear();
//            enterprises = utilsEJB.getEnterprises();
//            for (Enterprise e : enterprises) {
//                Comboitem cmbItem = new Comboitem();
//                cmbItem.setLabel(e.getName());
//                cmbItem.setValue(e);
//                cmbItem.setParent(cmbEnterprise);
//                if (enterprise != null && enterprise.getId().equals(e.getId())) {
//                    cmbEnterprise.setSelectedItem(cmbItem);
//                } else {
//                    cmbEnterprise.setSelectedIndex(0);
//                }
//            }
//        } catch (Exception ex) {
//            showError(ex);
//        }
//    }
//    
//    private void save(Product _product) {
//		Product product = new Product();
//		try {
//			if (_product != null)
//				product.setId(_product.getId());
//			 Map params = new HashMap<String, Object>();
//             params.put(QueryConstants.PARAM_PART_NUMBER, txtPartNumber.getText());
//             params.put(QueryConstants.PARAM_CATEGORY_ID, category.getId());
//             request.setParams(params);
//			if (_product == null) {
//				try {
//					productParam = productEJB.loadProductByPartNumber(request);
//					eventType = WebConstants.EVENT_EDIT;
//					loadData();
//				} catch (RegisterNotFoundException ex) {
//					productParam = null;
//				}
//			}
//			if (_product == null && productParam!=null) {
//				this.showMessage("sp.error.part.number.exist", true, null);
//				
//			} else {
//				product.setDescription(txtDescription.getText());
//				product.setAmount(Float.valueOf(txtAmount.getText()));
//				product.setActNpNsn(txtactNpNsn.getText());
//				product.setBatchNumber(txtBachNumber.getText());
//				product.setEnabled(cbxEnabled.isChecked());
//				product.setPartNumber(txtPartNumber.getText());
//				product.setUbicationBox(txtUbicationBox.getText());
//				product.setUbicationFolder(txtUbicationFolder.getText());
//				product.setStockMin(intStockMin.getValue());
//				product.setStockMax(intStockMax.getValue());
//				Enterprise e = (Enterprise) cmbEnterprise.getSelectedItem().getValue();
//				product.setEnterprise(e);
//				product.setCategory(category);
//				request.setParam(product);
//				request.setAuditData(null);
//				product = productEJB.saveProduct(request);
//				this.showMessage("sp.common.save.success", false, null);
//				AccessControl.saveAction(Permission.ADD_PRODUCT, "Ingreso el producto= " + product.getPartNumber());
//				saveAudit(_product, product);
//			}
//		} catch (Exception ex) {
//			showError(ex);
//		}
//	}
//    
//    public void onClick$btnSave() {
//        if (validateEmpty()) {
//            switch (eventType) {
//                case WebConstants.EVENT_ADD:
//                    save(null);
//                    break;
//                case WebConstants.EVENT_EDIT:
//                    save(productParam);
//                    break;
//                default:
//                    break;
//            }
//        }
//    }
//    
// public void loadFields(Product product) {
//    	
//    	intStockMax.setValue(product.getStockMax());
//    	intStockMin.setValue(product.getStockMin());
//    	txtAmount.setText(String.valueOf(product.getAmount()));
//		txtBachNumber.setText(product.getBatchNumber());
//		txtUbicationFolder.setText(product.getUbicationFolder());
//		txtUbicationBox.setText(product.getUbicationBox());
//		txtactNpNsn.setText(product.getActNpNsn());
//		txtDescription.setText(product.getDescription());
//		txtPartNumber.setText(product.getPartNumber());
//		txtPartNumber.setReadonly(true);
//		cbxEnabled.setChecked(product.getEnabled());
//    }
//
//    public void loadData() {
//        switch (eventType) {
//            case WebConstants.EVENT_EDIT:
//                loadFields(productParam);
//                loadEnterprises(productParam.getEnterprise());
//                break;
//            case WebConstants.EVENT_VIEW:
//                loadFields(productParam);
//                loadEnterprises(productParam.getEnterprise());
//                blockFields();
//                break;
//            case WebConstants.EVENT_ADD:
//                loadEnterprises(null);
//                break;
//            default:
//                break;
//        }
//    }
//	
//	 public void onClick$btnBack() {
//		 winAddProductView.detach();
//	   	 Window window = (Window)Executions.createComponents("catProducts.zul", null, null);
//	        try {
//				window.doModal();
//			} catch (SuspendNotAllowedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//	   }
//	 
//	 public void saveAudit(Product fCustomerOld ,Product fCustomerNew){
//	        EJBRequest request1 = new EJBRequest();
//	        EJBRequest request2 = new EJBRequest();            
//	        String result = "";
//	         String oldValue ="";
//	        request1.setParam(fCustomerOld);
//	        request2.setParam(fCustomerNew);
//
//	        try {
//	            result = auditoryEJB.getNaturalFieldProduct(request1, request2);
//	        } catch (Exception ex) {
//	            
//	        }
//
//	        if(!result.isEmpty()||!"".equals(result)){
//	            String name = fCustomerOld.getDescription();
//	            String actNpNsn = fCustomerOld.getActNpNsn();
//	            String batchNumber = fCustomerOld.getBatchNumber();
//	            String box = fCustomerOld.getUbicationBox();
//	            String folder = fCustomerOld.getUbicationFolder();
//	            String partNumber = fCustomerOld.getPartNumber();
//	            String amount = String.valueOf(fCustomerOld.getAmount());
//	            oldValue = "Description:"+name+"|ActNpNsn:"+actNpNsn+"|BatchNumber:"+batchNumber
//	                    +"|UbicationBox:"+box+"|UbicationFolder:"+folder+"|PartNumber:"+partNumber+"|Amount:"+amount;
//	            
//	            try {
//					EJBRequest ejbRequest = new EJBRequest();
//					ejbRequest.setParam(eventType);
//					Event ev = auditoryEJB.loadEvent(ejbRequest);
//					Audit audit = new Audit();
//					EJBRequest auditRequest = new EJBRequest();
//					audit.setUser(user);
//					audit.setEvent(ev);
//					Permission permission = PermissionManager.getInstance().getPermissionById(2L);
//					audit.setPermission(permission);
//					audit.setCreationDate(new Timestamp((new java.util.Date().getTime())));
//					audit.setTableName("Product");
//					audit.setRemoteIp(ipAddress);
//					audit.setOriginalValues(oldValue);
//					audit.setNewValues(result);
//					audit.setResponsibleType("usuario");
//					auditRequest.setParam(audit);
//					audit = auditoryEJB.saveAudit(auditRequest);
//				} catch (Exception ex) {
//					ex.printStackTrace();
//				}
//	        }
//	    }
//}
