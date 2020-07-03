/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.alodiga.wallet.admin.web.controllers;

import org.zkoss.zul.CategoryModel;
import org.zkoss.zul.PieModel;
import org.zkoss.zul.SimpleCategoryModel;
import org.zkoss.zul.SimplePieModel;

/**
 *
 * @author lromero
 */
public class ModelData {

    public static CategoryModel getModel() {
        SimpleCategoryModel model = new SimpleCategoryModel();
        model.setValue("Hoy", "", new Integer(100));
        model.setValue("Ayer", "", new Integer(143));
        model.setValue("Ultimos 7", "", new Integer(223));
        model.setValue("Ultimos 15", "", new Integer(378));
        return model;
    }

    public static CategoryModel getModelLevel() {
        SimpleCategoryModel model = new SimpleCategoryModel();
        model.setValue("X", "Nivel X", new Integer(450));
        model.setValue("X1", "Nivel C1", new Integer(350));
        model.setValue("X2", "Nivel 1", new Integer(200));
        model.setValue("X3", "Nivel 2", new Integer(150));
        model.setValue("X4", "Nivel 3", new Integer(100));
        model.setValue("X5", "Nivel 4", new Integer(80));
        return model;
    }

    public static PieModel getModelpie(){
        PieModel model = new SimplePieModel();
        model.setValue("Pines Electronicos", new Double(30));
        model.setValue("Recargas TopUp", new Double(50));
        model.setValue("Pago de Facturas", new Double(20));
        return model;
    }
}
