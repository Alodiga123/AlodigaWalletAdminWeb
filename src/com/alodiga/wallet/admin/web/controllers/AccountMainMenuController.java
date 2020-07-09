//Menu
package com.alodiga.wallet.admin.web.controllers;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Listcell;

import com.alodiga.wallet.admin.web.utils.WebConstants;

public class AccountMainMenuController extends GenericForwardComposer {

    private static final long serialVersionUID = -9145887024839938515L;
    //UserEJB userEJB = null;
    Listcell ltcFullName;
    Listcell ltcLevel;
    Listcell ltcLogin;
    Listcell ltcPoints;
    Listcell ltcDistributorList;
    Listcell ltcDistributorHierarchy;
    Listcell ltcPurchaseBalance;
    Listcell ltcPurchasePins;
    Listcell ltcAddPinFrees;
    Listcell ltcRecharTopTups;
    Listcell ltcBonificationes;
    Listcell ltcTransactions;
    Listcell ltcRoyalties;
    Listcell ltcAddDescendant;
    Listcell ltcPromotiones;
    Listcell ltcPinBalance;
    Listcell ltcTopUpBalance;
    Listcell ltcAccessNumber;
    private static String OPTION = "option";
    private static String OPTION_NONE = "none";
    private static String OPTION_DISTRIBUTORS_LIST = "ltcDistributorList";
    private static String OPTION_DISTRIBUTORS_HIERARCHY = "ltcDistributorHierarchy";
    private static String OPTION_PURCHASE_BALANCE = "ltcPurchaseBalance";
    private static String OPTION_PURCHASE_PINS = "ltcPurchasePins";
    private static String OPTION_ADD_PINFREES = "ltcAddPinFrees";
    private static String OPTION_RECHARGE_TOP_UP = "ltcRecharTopTups";
    private static String OPTION_ADD_DESCENDANT = "ltcAddDescendant";
    private static String OPTION_BONIFICATIONES = "ltcBonificationes";
    private static String OPTION_TRANSACTIONS = "ltcTransactions";
    private static String OPTION_ROYALTIES = "ltcRoyalties";
    private static String OPTION_PROMOTIONES = "ltcPromotiones";
    private static String OPTION_ACCESSNUMBER = "ltcAccessNumber";


    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        initialize();
        startListener();
    }

    public void initialize() {
        try {
            loadAccountData();
            checkOption();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void startListener() {
        EventQueue que = EventQueues.lookup("updateDistributorData", EventQueues.APPLICATION, true);
        que.subscribe(new EventListener() {

            public void onEvent(Event evt) {
                try {
                    loadAccountData();

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    private void checkOption() {
        String option = getOptionInSession();
        if (option.equals(OPTION_NONE)) {
        } else if (option.equals(OPTION_PURCHASE_BALANCE)) {
            ltcPurchaseBalance.setImage("/images/icon-target.png");
        } else if (option.equals(OPTION_DISTRIBUTORS_LIST)) {
            ltcDistributorList.setImage("/images/icon-target.png");
        } else if (option.equals(OPTION_DISTRIBUTORS_HIERARCHY)) {
            ltcDistributorHierarchy.setImage("/images/icon-target.png");
        } else if (option.equals(OPTION_PURCHASE_PINS)) {
            ltcPurchasePins.setImage("/images/icon-target.png");
        } else if (option.equals(OPTION_ADD_PINFREES)) {
            ltcAddPinFrees.setImage("/images/icon-target.png");
        } else if (option.equals(OPTION_RECHARGE_TOP_UP)) {
            ltcRecharTopTups.setImage("/images/icon-target.png");
        } else if (option.equals(OPTION_BONIFICATIONES)) {
            ltcBonificationes.setImage("/images/icon-target.png");
        } else if (option.equals(OPTION_TRANSACTIONS)) {
            ltcTransactions.setImage("/images/icon-target.png");
        } else if (option.equals(OPTION_ROYALTIES)) {
            ltcRoyalties.setImage("/images/icon-target.png");
        } else if (option.equals(OPTION_ADD_DESCENDANT)) {
            ltcAddDescendant.setImage("/images/icon-target.png");
        } else if (option.equals(OPTION_PROMOTIONES)) {
            ltcPromotiones.setImage("/images/icon-target.png");
        }else if (option.equals(OPTION_ACCESSNUMBER)) {
            ltcAccessNumber.setImage("/images/icon-target.png");
        }

    }

    private void loadAccountData() {
        try {

     
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void onClick$ltcPurchaseBalance() {
        setOptionInSession(OPTION_PURCHASE_BALANCE);
        Executions.sendRedirect("./d-purchaseBalance.zul");
    }

    public void onClick$ltcDistributorList() {
        setOptionInSession(OPTION_DISTRIBUTORS_LIST);
        Executions.sendRedirect("./d-listDistributors.zul");
    }

    public void onClick$ltcDistributorHierarchy() {
        setOptionInSession(OPTION_DISTRIBUTORS_HIERARCHY);
        Executions.sendRedirect("./d-distributorsHierarchy.zul");
    }

    public void onClick$ltcPurchasePins() {
        setOptionInSession(OPTION_PURCHASE_PINS);
        Executions.sendRedirect("./d_purchasePin.zul");
    }

    public void onClick$ltcAddPinFrees() {
        setOptionInSession(OPTION_ADD_PINFREES);
        Executions.sendRedirect("./d_addPinFree.zul");
    }

    public void onClick$ltcRecharTopTups() {
        setOptionInSession(OPTION_RECHARGE_TOP_UP);
        Executions.sendRedirect("./d_topUpRecharge.zul");
    }

    public void onClick$ltcBonificationes() {
        setOptionInSession(OPTION_BONIFICATIONES);
        Executions.sendRedirect("./d_bonificationDistributor.zul");
    }

    public void onClick$ltcTransactions() {
        setOptionInSession(OPTION_TRANSACTIONS);
        Executions.sendRedirect("./d_reportSalesDistributor.zul");
    }

    public void onClick$ltcRoyalties() {
        setOptionInSession(OPTION_ROYALTIES);
        Executions.sendRedirect("./d_royaltyDistributor.zul");
    }

    public void onClick$ltcAddDescendant() {
        Sessions.getCurrent().setAttribute("eventType", WebConstants.EVENT_ADD);
        Sessions.getCurrent().removeAttribute("object");
        setOptionInSession(OPTION_ADD_DESCENDANT);
        Executions.getCurrent().sendRedirect("d-adminDistributor.zul");
    }

    public void onClick$ltcPromotiones() {
        setOptionInSession(OPTION_PROMOTIONES);
        Executions.sendRedirect("./d_reportPromotionUser.zul");
    }

     public void onClick$ltcAccessNumber() {
        setOptionInSession(OPTION_ACCESSNUMBER);
        Executions.sendRedirect("./accessNumber.zul");
    }

    public void onClick$mniMyAccout() {
        // AccessControl.logout();
        //Executions.sendRedirect("./admin.zul");
    }

    private void setOptionInSession(String option) {
        Sessions.getCurrent().setAttribute(OPTION, option);
    }

    private String getOptionInSession() {
        return Sessions.getCurrent().getAttribute(OPTION) != null ? Sessions.getCurrent().getAttribute(OPTION).toString() : OPTION_NONE;
    }
}
