package com.vermouthx.stocker.components;

import javax.swing.table.DefaultTableModel;

public class StockerTableModel extends DefaultTableModel {
    @Override
    public boolean isCellEditable(int row, int column) {
        if(column== 4){
            return true;
        }
        return false;
    }

    @Override
    public void setValueAt(Object aValue, int row, int column) {
        super.setValueAt(aValue, row, column);
    }
}
