package com.alodiga.wallet.admin.web.components;

import org.zkoss.zul.Listcell;

public class ListcellImage extends Listcell {

	public ListcellImage() {
	}

	public ListcellImage(String destinationView) {
		this.setImage(destinationView);
	}
}
