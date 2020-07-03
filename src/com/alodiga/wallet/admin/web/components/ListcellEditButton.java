package com.alodiga.wallet.admin.web.components;

import org.zkoss.util.resource.Labels;
import org.zkoss.zul.Listcell;

public class ListcellEditButton extends Listcell {

    public ListcellEditButton() {
        EditButton editButton = new EditButton();
        editButton.setParent(this);
    }

    public ListcellEditButton(String destinationView, Object obj, Long permissionId) {
        EditButton button = new EditButton(destinationView, obj,permissionId);
        button.setTooltiptext(Labels.getLabel("sp.common.actions.edit"));
        button.setClass("open orange");
        button.setParent(this);
    }

}
