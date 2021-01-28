package com.alodiga.wallet.admin.web.components;

import org.zkoss.util.resource.Labels;
import org.zkoss.zul.Listcell;

public class ListcellAddButton extends Listcell {

    public ListcellAddButton() {
    	AddButton editButton = new AddButton();
        editButton.setParent(this);
    }

    public ListcellAddButton(String destinationView, Object obj, Long permissionId) {
    	AddButton button = new AddButton	(destinationView, obj,permissionId);
        button.setTooltiptext(Labels.getLabel("wallet.actions.income"));
        button.setClass("open orange");
        button.setParent(this);
    }

}
