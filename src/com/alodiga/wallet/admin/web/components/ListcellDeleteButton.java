package com.alodiga.wallet.admin.web.components;

import org.zkoss.zul.Listcell;

public class ListcellDeleteButton extends Listcell {

	public ListcellDeleteButton() {
	}

	public ListcellDeleteButton(Object obj) {
		DeleteButton deleteButton = new DeleteButton(obj);
		deleteButton.setParent(this);

	}
}
