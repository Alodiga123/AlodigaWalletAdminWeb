package com.alodiga.wallet.admin.web.generic.controllers;

import java.util.List;


public interface GenericListController<T> {
	public void startListener();
	public List<T> getFilteredList(String filter);
	public void loadList(List<T> list);
	public void getData();
        public void showMessage(String message, boolean isError, Exception exception);
        public void clearMessage();
        public void checkPermissions();
        public void onClick$btnAdd() throws InterruptedException;
        public void onClick$btnDownload() throws InterruptedException;
        public void onClick$btnClear() throws InterruptedException;
        public void onClick$btnSearch() throws InterruptedException;
}
