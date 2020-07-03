package com.alodiga.wallet.admin.web.generic.controllers;

import org.zkoss.util.resource.Labels;
import org.zkoss.zul.Window;

import com.alodiga.wallet.admin.web.utils.WebConstants;

import org.zkoss.zul.Toolbarbutton;

public abstract class GenericAbstractAdminController extends GenericAbstractController implements GenericAdminController {

    private String typeView = ".view";
    private String typeEdit = ".edit";
    private String typeAdd = ".add";
    public Toolbarbutton tbbCurrentAction;
    
    public void initView(int eventType, String view) {

        switch (eventType) {

            case WebConstants.EVENT_EDIT:
                tbbCurrentAction.setLabel(Labels.getLabel(view + typeEdit));
                //initToEdit(view + typeEdit, window);
                break;
            case WebConstants.EVENT_VIEW:
                tbbCurrentAction.setLabel(Labels.getLabel(view + typeView));
                blockFields();
                //initToView(view + typeView, window);
                break;
            case WebConstants.EVENT_ADD:
                tbbCurrentAction.setLabel(Labels.getLabel(view + typeAdd));
                //initToAdd(view + typeAdd, window);
                break;
            case WebConstants.EVENT_ADD_DESCENDANT:
                tbbCurrentAction.setLabel(Labels.getLabel(view + typeAdd));
                //initToAdd(view + typeAdd, window);
                break;
            default:
                //initToView(view, window);
                break;
        }
        loadData();
    }

    protected void initToView(String view, Window window) {
        //window.setTitle(Labels.getLabel(view));
        
        blockFields();
    }

    protected void initToEdit(String view, Window window) {
        //window.setTitle(Labels.getLabel(view));
        loadData();
    }

    protected void initToAdd(String view, Window window) {
        //window.setTitle(Labels.getLabel(view));
        loadData();
    }

	public void clearFields() {
		// TODO Auto-generated method stub
		
	}
}
