package com.alodiga.wallet.admin.web.components;

import org.zkoss.zul.Button;

import com.alodiga.wallet.admin.web.utils.WebConstants;
import org.zkoss.zk.ui.Sessions;

public class EditButton extends Button {

    public EditButton() {
        this.setImage("/images/icon-edit.png");
    }

    public EditButton(String view, Object obj, Long permissionId) {
        this.setImage("/images/icon-edit.png");
        this.addEventListener("onClick", new ShowAdminViewListener(WebConstants.EVENT_EDIT, view, obj,permissionId));
    }
    public EditButton(String view, Object obj) {
        Sessions.getCurrent().setAttribute(WebConstants.EVENTYPE, WebConstants.EVENT_EDIT);
        this.setImage("/images/icon-edit.png");
        this.addEventListener("onClick", new ShowAdminViewListener(WebConstants.EVENT_EDIT, view, obj));
    }

}
