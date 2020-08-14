package com.alodiga.wallet.admin.web.controllers;

import java.util.ArrayList;
import java.util.List;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import com.alodiga.wallet.admin.web.components.ListcellEditButton;
import com.alodiga.wallet.admin.web.components.ListcellViewButton;
import com.alodiga.wallet.admin.web.generic.controllers.GenericAbstractListController;
import com.alodiga.wallet.admin.web.utils.AccessControl;
import com.alodiga.wallet.admin.web.utils.Utils;
import com.alodiga.wallet.admin.web.utils.WebConstants;
import com.alodiga.wallet.common.ejb.PersonEJB;
import com.alodiga.wallet.common.ejb.UserEJB;
import com.alodiga.wallet.common.exception.EmptyListException;
import com.alodiga.wallet.common.exception.GeneralException;
import com.alodiga.wallet.common.exception.NullParameterException;
import com.alodiga.wallet.common.manager.PermissionManager;
import com.alodiga.wallet.common.model.Permission;
import com.alodiga.wallet.common.model.Person;
import com.alodiga.wallet.common.model.Profile;
import com.alodiga.wallet.common.model.User;
import com.alodiga.wallet.common.utils.EJBServiceLocator;
import com.alodiga.wallet.common.utils.EjbConstants;

public class ListAplicantOFACController extends GenericAbstractListController<Person> {

    private static final long serialVersionUID = -9145887024839938515L;
    private Listbox lbxRecords;
    private UserEJB userEJB = null;
    private PersonEJB personEJB = null;
    private List<Person> personList = null;
    private User currentUser;
    private Profile currentProfile;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        initialize();
    }

    @Override
    public void checkPermissions() {
        try {
//            btnAdd.setVisible(PermissionManager.getInstance().hasPermisssion(currentProfile.getId(), Permission.ADD_APLICANT_OFAC));
            permissionEdit = PermissionManager.getInstance().hasPermisssion(currentProfile.getId(), Permission.EDIT_APLICANT_OFAC);
            permissionRead = PermissionManager.getInstance().hasPermisssion(currentProfile.getId(), Permission.VIEW_APLICANT_OFAC);
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void startListener() {

    }

    @Override
    public void initialize() {
        super.initialize();
        try {
            currentUser = AccessControl.loadCurrentUser();
            personEJB = (PersonEJB) EJBServiceLocator.getInstance().get(EjbConstants.PERSON_EJB);
            currentProfile = currentUser.getCurrentProfile();
            checkPermissions();
            adminPage = "adminAplicantOFAC.zul";
            getData();
            loadList(personList);
        } catch (Exception ex) {
            showError(ex);
        }
    }

//    public List<Person> getFilteredList(String filter) {
//        List<Person> list = new ArrayList<Person>();
//        if (personList != null) {
//            for (Iterator<Person> i = personList.iterator(); i.hasNext();) {
//                Person tmp = i.next();
//                String field = tmp.getName().toLowerCase();
//                if (field.indexOf(filter.trim().toLowerCase()) >= 0) {
//                    list.add(tmp);
//                }
//            }
//        }
//        return list;
//    }
    public void onClick$btnAdd() throws InterruptedException {
        Sessions.getCurrent().setAttribute("eventType", WebConstants.EVENT_ADD);
        Sessions.getCurrent().removeAttribute("object");
        Executions.getCurrent().sendRedirect(adminPage);
    }

    public void onClick$btnDelete() {
    }

    public void loadList(List<Person> list) {
        String applicantNameLegal = "";
        String tipo = "";
        try {
            lbxRecords.getItems().clear();
            Listitem item = null;
            if (list != null && !list.isEmpty()) {
                btnDownload.setVisible(true);

                for (Person aplicant : list) {
                    item = new Listitem();
                    item.setValue(aplicant);
                    if (aplicant.getPersonTypeId().getIndNaturalPerson() == true) {
                        StringBuilder applicantNameNatural = new StringBuilder(aplicant.getNaturalPerson().getFirstName());
                        applicantNameNatural.append(" ");
                        applicantNameNatural.append(aplicant.getNaturalPerson().getLastName());
                        item.appendChild(new Listcell(applicantNameNatural.toString()));
                        item.appendChild(new Listcell(aplicant.getNaturalPerson().getDocumentsPersonTypeId().getDescription()));
                        item.appendChild(new Listcell(aplicant.getNaturalPerson().getIdentificationNumber()));
                        item.appendChild(new Listcell(Labels.getLabel("sp.tab.businessAffiliationRequests.mainApplicant")));
                    } else {
                        if (aplicant.getPersonClassificationId().getId() != 3) {
                            applicantNameLegal = aplicant.getLegalPerson().getBusinessName();
                            item.appendChild(new Listcell(applicantNameLegal));
                            item.appendChild(new Listcell(aplicant.getLegalPerson().getDocumentsPersonTypeId().getDescription()));
                            item.appendChild(new Listcell(aplicant.getLegalPerson().getIdentificationNumber()));
                            item.appendChild(new Listcell(Labels.getLabel("sp.tab.businessAffiliationRequests.mainApplicant")));
                        } else {
                            StringBuilder applicantLegalR = new StringBuilder(aplicant.getLegalRepresentative().getFirstNames());
                            applicantLegalR.append(" ");
                            applicantLegalR.append(aplicant.getLegalRepresentative().getLastNames());
                            item.appendChild(new Listcell(applicantLegalR.toString()));
                            item.appendChild(new Listcell(aplicant.getLegalRepresentative().getDocumentsPersonTypeId().getDescription()));
                            item.appendChild(new Listcell(aplicant.getLegalRepresentative().getIdentificationNumber()));
                            item.appendChild(new Listcell(Labels.getLabel("sp.tab.businessAffiliationRequests.legalRepresentative")));
                        }
                    }
                    item.appendChild(permissionEdit ? new ListcellEditButton(adminPage, aplicant, Permission.EDIT_APLICANT_OFAC) : new Listcell());
                    item.appendChild(permissionRead ? new ListcellViewButton(adminPage, aplicant, Permission.VIEW_APLICANT_OFAC) : new Listcell());
                    item.setParent(lbxRecords);
                }
            } else {
                btnDownload.setVisible(false);
                item = new Listitem();
                item.appendChild(new Listcell(Labels.getLabel("sp.error.empty.list")));
                item.appendChild(new Listcell());
                item.appendChild(new Listcell());
                item.appendChild(new Listcell());
                item.setParent(lbxRecords);
            }
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void getData() {
        personList = new ArrayList<Person>();
        try {
            request.setFirst(0);
            request.setLimit(null);
            personList = personEJB.getPerson(request);
        } catch (NullParameterException ex) {
            showError(ex);
        } catch (EmptyListException ex) {
            showEmptyList();
        } catch (GeneralException ex) {
            showError(ex);
        }
    }

    private void showEmptyList() {
        Listitem item = new Listitem();
        item.appendChild(new Listcell(Labels.getLabel("sp.error.empty.list")));
        item.appendChild(new Listcell());
        item.appendChild(new Listcell());
        item.appendChild(new Listcell());
        item.setParent(lbxRecords);
    }

    public void onClick$btnDownload() throws InterruptedException {
        try {
            Utils.exportExcel(lbxRecords, Labels.getLabel("sp.businessAffiliationRequests.ofac.list"));
            AccessControl.saveAction(Permission.LIST_APLICANT_OFAC, "Se descargo listado OFAC en formato excel");
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void onClick$btnClear() throws InterruptedException {

    }

    public void onClick$btnSearch() throws InterruptedException {
        try {
        } catch (Exception ex) {
            showError(ex);
        }
    }

    @Override
    public List<Person> getFilteredList(String filter) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
