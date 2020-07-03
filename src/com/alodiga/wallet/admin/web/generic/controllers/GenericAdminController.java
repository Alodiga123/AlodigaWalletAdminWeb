package com.alodiga.wallet.admin.web.generic.controllers;

public interface GenericAdminController {

    public void initView(int eventType, String view);
    public void clearFields();
    public void loadData();
    public void blockFields();
    public void showMessage(String message, boolean isError, Exception exception);
    public void clearMessage();
}
