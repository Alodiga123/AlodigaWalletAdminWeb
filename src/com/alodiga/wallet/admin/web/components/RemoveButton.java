package com.alodiga.wallet.admin.web.components;

import org.zkoss.zul.Button;

import com.alodiga.wallet.admin.web.utils.WebConstants;

public class RemoveButton extends Button {

	public RemoveButton() {
		this.setImage("/images/icon-remove.png");
	}

	 public RemoveButton(String view, Object obj, Long permissionId) {
	        this.setImage("/images/icon-remove.png");
	        this.addEventListener("onClick", new ShowAdminViewListener(WebConstants.EVENT_DELETE, view, obj,permissionId));
	    }
}
