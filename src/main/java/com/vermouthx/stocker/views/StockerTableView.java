package com.vermouthx.stocker.views;

import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.table.JBTable;
import com.vermouthx.stocker.components.StockerDefaultTableCellRender;
import com.vermouthx.stocker.components.StockerTableHeaderRender;
import com.vermouthx.stocker.components.StockerTableModel;
import com.vermouthx.stocker.entities.StockerQuote;
import com.vermouthx.stocker.settings.StockerSetting;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class StockerTableView {

    private JPanel mPane;
    private JScrollPane tbPane;
    private Color upColor;
    private Color downColor;
    private Color zeroColor;
    private JBTable tbBody;
    private StockerTableModel tbModel;

    private final ComboBox<String> cbIndex = new ComboBox<>();
    private final JBLabel lbIndexValue = new JBLabel("", SwingConstants.CENTER);
    private final JBLabel lbIndexExtent = new JBLabel("", SwingConstants.CENTER);
    private final JBLabel lbIndexPercent = new JBLabel("", SwingConstants.CENTER);
    private List<StockerQuote> indices = new ArrayList<>();

    public StockerTableView() {
//        syncColorPatternSetting();
        initPane();
        initTable();
    }

    public void syncIndices(List<StockerQuote> indices) {
        this.indices = indices;
        if (cbIndex.getItemCount() == 0 && !indices.isEmpty()) {
            indices.forEach(i -> cbIndex.addItem(i.getName().substring(0,1)));
            cbIndex.setSelectedIndex(0);
        }
//        syncColorPatternSetting();
        updateIndex();
    }

    private void syncColorPatternSetting() {
        StockerSetting setting = StockerSetting.Companion.getInstance();
        switch (setting.getQuoteColorPattern()) {
            case RED_UP_GREEN_DOWN:
                upColor = JBColor.RED;
                downColor = JBColor.GREEN;
                zeroColor = JBColor.GRAY;
                break;
            case GREEN_UP_RED_DOWN:
                upColor = JBColor.GREEN;
                downColor = JBColor.RED;
                zeroColor = JBColor.GRAY;
                break;
            default:
                upColor = JBColor.foreground();
                downColor = JBColor.foreground();
                zeroColor = JBColor.foreground();
                break;
        }
    }

    private void updateIndex() {
        if (cbIndex.getSelectedIndex() != -1) {
            String name = Objects.requireNonNull(cbIndex.getSelectedItem()).toString();
            for (StockerQuote index : indices) {
                if (index.getName().substring(0,1).equals(name)) {
                    lbIndexValue.setText(Double.toString(index.getCurrent()));
                    lbIndexExtent.setText(Double.toString(index.getChange()));
                    lbIndexPercent.setText(index.getPercentage() + "%");
                    double value = index.getPercentage();
                    if (value > 0) {
                        lbIndexValue.setForeground(upColor);
                        lbIndexExtent.setForeground(upColor);
                        lbIndexPercent.setForeground(upColor);
                    } else if (value < 0) {
                        lbIndexValue.setForeground(downColor);
                        lbIndexExtent.setForeground(downColor);
                        lbIndexPercent.setForeground(downColor);
                    } else {
                        lbIndexValue.setForeground(zeroColor);
                        lbIndexExtent.setForeground(zeroColor);
                        lbIndexPercent.setForeground(zeroColor);
                    }
                    break;
                }
            }
        }
    }

    private void initPane() {
        tbPane = new JBScrollPane();
        tbPane.setBorder(BorderFactory.createEmptyBorder());
        JPanel iPane = new JPanel(new GridLayout(1, 4));
        iPane.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, JBColor.border()));
        iPane.add(cbIndex);
        iPane.add(lbIndexValue);
        iPane.add(lbIndexExtent);
        iPane.add(lbIndexPercent);
        cbIndex.addItemListener(i -> updateIndex());
        mPane = new JPanel(new BorderLayout());
        mPane.add(tbPane, BorderLayout.CENTER);
        mPane.add(iPane, BorderLayout.SOUTH);
    }

    private static final String codeColumn = "Symbol";
    private static final String nameColumn = "Name";
    private static final String currentColumn = "Current";
    private static final String percentColumn = "Change%";

    private static final String chiyouInputColumn = "chiyou";
    private static final String profitColumn = "profit";
    private void initTable() {
        tbModel = new StockerTableModel();
        tbBody = new JBTable();
        tbBody.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                int row = tbBody.rowAtPoint(e.getPoint());
                if (row >= 0 && row < tbBody.getRowCount()) {
                    if (tbBody.getSelectedRows().length == 0 || Arrays.stream(tbBody.getSelectedRows()).noneMatch(p -> p == row)) {
                        tbBody.setRowSelectionInterval(row, row);
                    }
                } else {
                    tbBody.clearSelection();
                }
            }
            @Override
            public void mouseClicked(MouseEvent e) {
                int i = e.getButton(); // 通过该值可以判断单击的是哪个键
                int column = tbBody.columnAtPoint(e.getPoint());
                if (column == 1) {
                    if (i == MouseEvent.BUTTON1)
                        tbBody.getColumn(nameColumn).setCellRenderer(new StockerDefaultTableCellRender());
                    if (i == MouseEvent.BUTTON3)
                        tbBody.getColumn(nameColumn).setCellRenderer(new HiddenContentRenderer());
                }
            }
        });
        tbModel.setColumnIdentifiers(new String[]{codeColumn, nameColumn, currentColumn, percentColumn,chiyouInputColumn,profitColumn});
        tbBody.setShowVerticalLines(false);
        tbBody.setModel(tbModel);
        tbBody.getTableHeader().setReorderingAllowed(false);
        tbBody.getTableHeader().setDefaultRenderer(new StockerTableHeaderRender(tbBody));
        TableColumn code = tbBody.getColumn(codeColumn);
        code.setCellRenderer(new StockerDefaultTableCellRender());
        tbBody.getColumn(nameColumn).setCellRenderer(new HiddenContentRenderer());
        TableColumn chiyouColumn = tbBody.getColumn(chiyouInputColumn);
        chiyouColumn.setCellRenderer(new StockerDefaultTableCellRender());
        tbBody.getColumn(currentColumn).setCellRenderer(new StockerDefaultTableCellRender() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
//                syncColorPatternSetting();
                setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
                String percent = table.getValueAt(row, table.getColumn(percentColumn).getModelIndex()).toString();
                Double v = Double.parseDouble(percent.substring(0, percent.indexOf("%")));
                applyColorPatternToTable(v, this);
                return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            }
        });
        tbBody.getColumn(percentColumn).setCellRenderer(new StockerDefaultTableCellRender() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
//                syncColorPatternSetting();
                setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
                String percent = value.toString();
                Double v = Double.parseDouble(percent.substring(0, percent.indexOf("%")));
                applyColorPatternToTable(v, this);
                return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            }
        });
        tbBody.getColumn(nameColumn).setCellRenderer(new HiddenContentRenderer());
        tbPane.add(tbBody);
        tbPane.setViewportView(tbBody);

    }

    private void applyColorPatternToTable(Double value, DefaultTableCellRenderer renderer) {
        if (value > 0) {
            renderer.setForeground(upColor);
        } else if (value < 0) {
            renderer.setForeground(downColor);
        } else {
            renderer.setForeground(zeroColor);
        }
    }

    public JComponent getComponent() {
        return mPane;
    }

    public JBTable getTableBody() {
        return tbBody;
    }

    public DefaultTableModel getTableModel() {
        return tbModel;
    }

    // 自定义单元格渲染器，用于隐藏内容
    static class HiddenContentRenderer extends JLabel implements TableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setText(""); // 设置空字符串来隐藏内容
            setOpaque(true); // 设置为不透明，以便可以设置背景色

            // 如果单元格被选中，设置背景色为选中色
            if (isSelected) {
                setBackground(table.getSelectionBackground());
                setForeground(table.getSelectionForeground());
            } else {
                setBackground(table.getBackground());
                setForeground(table.getForeground());
            }

            return this;
        }
    }
}
