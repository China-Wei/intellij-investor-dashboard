package com.vermouthx.stocker.listeners;

import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ComponentManager;
import com.intellij.openapi.components.ServiceManager;
import com.vermouthx.stocker.entities.MyConfig;
import com.vermouthx.stocker.entities.StockerQuote;
import com.vermouthx.stocker.utils.MyConfigService;
import com.vermouthx.stocker.utils.StockerTableModelUtil;
import com.vermouthx.stocker.views.StockerTableView;

import javax.swing.table.DefaultTableModel;
import java.util.List;

public class StockerQuoteUpdateListener implements StockerQuoteUpdateNotifier {
    private final StockerTableView myTableView;

    public StockerQuoteUpdateListener(StockerTableView myTableView) {
        this.myTableView = myTableView;
    }

    @Override
    public void syncQuotes(List<StockerQuote> quotes, int size) {
        DefaultTableModel tableModel = myTableView.getTableModel();
        quotes.forEach(quote -> {
//            DefaultTableModel tableModel1 = myTableView.getTableModel();
            synchronized (tableModel) {
                int rowIndex = StockerTableModelUtil.existAt(tableModel, quote.getCode());
                if (rowIndex != -1) {
                    if (!tableModel.getValueAt(rowIndex, 1).equals(quote.getName())) {
                        tableModel.setValueAt(quote.getName(), rowIndex, 1);

                    }
                    if (!tableModel.getValueAt(rowIndex, 2).equals(quote.getCurrent())) {
                        tableModel.setValueAt(quote.getCurrent(), rowIndex, 2);

                    }
//                    tableModel.setValueAt(100, rowIndex,4);
                    if (!tableModel.getValueAt(rowIndex, 3).equals(quote.getPercentage())) {
                        tableModel.setValueAt(quote.getPercentage() + "%", rowIndex, 3);
//                        更新当天收益
                        String chiyou = (String)tableModel.getValueAt(rowIndex, 4);
                        if (chiyou != null && chiyou != "") {
                            double chiyou_d = Double.parseDouble(chiyou);
                            double percentage = quote.getPercentage();
                            double current = (double)(tableModel.getValueAt(rowIndex, 2));
                            double profit = (current*percentage*chiyou_d)/(1-percentage/100)/100;
                            tableModel.setValueAt(profit, rowIndex, 5);
                        }else{
                            // 在某个地方获取服务实例并读取配置
                            Application application = ApplicationManager.getApplication();
                            MyConfigService service = application.getService(MyConfigService.class);
                            List<MyConfig> configs = service.getConfigs();
                            if(configs!= null && !configs.isEmpty()){
                                for (MyConfig config: configs) {
                                    if(config.getCode().equals(quote.getCode())){
                                        tableModel.setValueAt(config.getChiyou(), rowIndex, 4);
                                        double percentage = quote.getPercentage();
                                        double current = (double)(tableModel.getValueAt(rowIndex, 2));
                                        double profit = (current*percentage*config.getChiyou())/(1-percentage/100)/100;
                                        tableModel.setValueAt(profit, rowIndex, 5);
                                    }
                                }
                            }

                        }

                    }
                } else {
                    if (quotes.size() == size) {
                        tableModel.addRow(new Object[]{quote.getCode(), quote.getName(), quote.getCurrent(), quote.getPercentage() + "%", "", ""});
                    }
                }
            }
        });
    }

    @Override
    public void syncIndices(List<StockerQuote> indices) {
        synchronized (myTableView) {
            myTableView.syncIndices(indices);
        }
    }

}
