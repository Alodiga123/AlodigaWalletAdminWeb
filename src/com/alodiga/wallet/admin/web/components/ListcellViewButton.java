package com.alodiga.wallet.admin.web.components;

import com.alodiga.wallet.admin.web.utils.WebConstants;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Listcell;

public class ListcellViewButton extends Listcell {

    public ListcellViewButton() {
    }

    public ListcellViewButton(String destinationView, Object obj, Long permissionId) {
        ViewButton button = new ViewButton(destinationView, obj,permissionId);
        button.setTooltiptext(Labels.getLabel("wallet.actions.view"));
        button.setClass("open orange");
        button.setParent(this);
    }

    public ListcellViewButton(String destinationView, Object obj, boolean isRedirect, Long permissionId) {
        ViewButton viewButton = new ViewButton(destinationView, obj,permissionId);
        viewButton.setParent(this);
    }
    
    public ListcellViewButton(String destinationView, Object obj) {
        ViewButton viewButton = new ViewButton(destinationView, obj);
        viewButton.setTooltiptext(Labels.getLabel("wallet.actions.view"));
        Sessions.getCurrent().setAttribute(WebConstants.EVENTYPE, WebConstants.EVENT_VIEW);
        viewButton.setParent(this);
    }
}
