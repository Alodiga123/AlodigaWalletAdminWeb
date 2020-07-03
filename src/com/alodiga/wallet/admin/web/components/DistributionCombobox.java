package com.alodiga.wallet.admin.web.components;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;


public class DistributionCombobox {

    public DistributionCombobox() {
    }

    public static Combobox fillComponent(Combobox combobox, Map<String, String> values) {
        Set<String> keys = values.keySet();

        for (String key : keys) {
            Comboitem comboitem = new DistributionComboitem();
            String value = values.get(key);
            comboitem.setId(key);
            comboitem.setValue(value);
            comboitem.setParent(combobox);
        }
        return combobox;
    }

    public static Combobox fillComponent(Combobox combobox, List entities, String methodName) {
        for (Object o : entities) {
            Comboitem comboitem = new Comboitem();
            comboitem.setValue(o);
            Method m = null;
            try {
                m = o.getClass().getMethod(methodName, null);
                comboitem.setLabel("" + m.invoke(o, null));
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (SecurityException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
            comboitem.setParent(combobox);
        }

        return combobox;
    }

//    public static Comboitem findComboItemIndex(List<Comboitem> items, Long id) {
//        for (Comboitem item : items) {
//            Object entity = item.getValue();
//            if (entity != null) {
//                Long pk = (Long) ((AbstractSPEntity) item.getValue()).getPk();
//                if (pk.equals(id)) {
//                    return item;
//                }
//            }
//
//        }
//        return null;
//    }
}
