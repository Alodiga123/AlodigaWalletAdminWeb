package com.alodiga.wallet.admin.web.components;

import org.zkoss.util.resource.Labels;
import org.zkoss.zul.Listcell;

public class ListcellInvoiceButton extends Listcell {

    public ListcellInvoiceButton() {
    }

    public ListcellInvoiceButton(String destinationView, Object obj, Long permissionId) {
        InvoiceButton button = new InvoiceButton(destinationView, obj,permissionId);
        button.setTooltiptext(Labels.getLabel("wallet.common.invoices"));
        button.setClass("open orange");
        button.setParent(this);
    }

    public ListcellInvoiceButton(String destinationView, Object obj, boolean isRedirect, Long permissionId) {
        InvoiceButton invoiceButton = new InvoiceButton(destinationView, obj,permissionId);
        invoiceButton.setParent(this);
    }
}
