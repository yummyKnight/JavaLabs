import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashSet;

public class ChangeForm extends JDialog {
    private JPanel rootPanel;
    private JTable driversOnRouteTable;
    private JTable ExistingStopsTable;
    private JTable ExistingDriversTable;
    private JScrollPane scrollPanel1;
    private JTable stopsOnRouteTable;
//     TODO: buttons realisation
    private JButton addNewDriverButton;
    private JButton addNewStopButton;
    private DataSingleton singleton = DataSingleton.getInstance();

    ChangeForm() {
        $$$setupUI$$$();
        setContentPane(rootPanel);
        setModal(true);
        setSize(new Dimension(1000, 500));
        doubleClickTransitBetweenTables(ExistingDriversTable, driversOnRouteTable);
        doubleClickTransitBetweenTables(ExistingStopsTable, stopsOnRouteTable);
        doubleClickTransitBetweenTables(driversOnRouteTable, ExistingDriversTable);
        doubleClickTransitBetweenTables(stopsOnRouteTable, ExistingStopsTable);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    public static void main(String[] args) {
        ChangeForm dialog = new ChangeForm();
        //dialog.getContentPane().setPreferredSize(new Dimension(500, 1000));
        dialog.pack();
        dialog.setVisible(true);
    }

    private void doubleClickTransitBetweenTables(JTable src, JTable dst) {
        src.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && !e.isConsumed()) {
                    Point point = e.getPoint();
                    int row = src.rowAtPoint(point);
                    String info = (String) src.getValueAt(row, 0);
                    DefaultTableModel dst_model = (DefaultTableModel) dst.getModel();
                    DefaultTableModel src_model = (DefaultTableModel) src.getModel();
                    dst_model.addRow(new Object[]{info});
                    dst_model.fireTableDataChanged();
                    src_model.removeRow(row);
                }
            }
        });

    }

    private Object[][] getALLDriversFIOAsArrays() {
        Object[][] result = new Object[singleton.allDrivers.size()][1];
        int i = 0;
        for (Driver driver : singleton.allDrivers) {
            result[i][0] = driver.getFIO();
            i++;
        }
        return result;
    }

    private Object[][] getALLStops() {
        HashSet<String> tmp = new HashSet<>();
        for (Route route : singleton.allRouts) {
            Object[][] t = route.getStopsArrays();
            for (Object[] objects : t) {
                tmp.add((String) objects[0]);
            }
        }
        Object[][] result = new Object[tmp.size()][1];
        int i = 0;
        for (String stop : tmp) {
            result[i][0] = stop.strip();
            i++;
        }
        return result;
    }


    private void initTables() {

        var driveModel = new DefaultTableModel(getALLDriversFIOAsArrays(), new String[]{"Все Водители"}) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        ExistingDriversTable = new JTable(driveModel);

        var stopsModel = new DefaultTableModel(getALLStops(), new String[]{"Остановки"}) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        ExistingStopsTable = new JTable(stopsModel);

        var driveModel_1 = new DefaultTableModel(null, new String[]{"Водители на маршруте"}) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        driversOnRouteTable = new JTable(driveModel_1);
//        TODO: get data from cell
        var stopsModel_1 = new DefaultTableModel(null, new String[]{"Остановки на маршруте"}) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        stopsOnRouteTable = new JTable(stopsModel_1);
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        createUIComponents();
        rootPanel = new JPanel();
        rootPanel.setLayout(new GridLayoutManager(5, 4, new Insets(0, 0, 0, 0), -1, -1));
        scrollPanel1 = new JScrollPane();
        rootPanel.add(scrollPanel1, new GridConstraints(0, 1, 4, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(303, 427), null, 0, false));
        scrollPanel1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-4473925)), null));
        scrollPanel1.setViewportView(driversOnRouteTable);
        final JScrollPane scrollPane1 = new JScrollPane();
        rootPanel.add(scrollPane1, new GridConstraints(0, 0, 4, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(608, 427), null, 0, false));
        scrollPane1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-4473925)), null));
        scrollPane1.setViewportView(ExistingDriversTable);
        final JScrollPane scrollPane2 = new JScrollPane();
        rootPanel.add(scrollPane2, new GridConstraints(0, 2, 4, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        ExistingStopsTable.setAutoCreateRowSorter(false);
        scrollPane2.setViewportView(ExistingStopsTable);
        final JScrollPane scrollPane3 = new JScrollPane();
        rootPanel.add(scrollPane3, new GridConstraints(0, 3, 4, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        stopsOnRouteTable.setAutoCreateRowSorter(false);
        scrollPane3.setViewportView(stopsOnRouteTable);
        addNewDriverButton = new JButton();
        addNewDriverButton.setText("Добавить");
        addNewDriverButton.setToolTipText("Добавть нового водителя");
        rootPanel.add(addNewDriverButton, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        addNewStopButton = new JButton();
        addNewStopButton.setText("Добавить");
        addNewStopButton.setToolTipText("Добавть новую остановку");
        rootPanel.add(addNewStopButton, new GridConstraints(4, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return rootPanel;
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
        initTables();

    }
}

