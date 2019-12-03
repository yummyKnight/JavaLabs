import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.DateFormatter;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.text.Format;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class ChangeForm extends JDialog {
    // logger
    private final Logger logger = LoggerFactory.getLogger(ChangeForm.class);
    // UI
    private JPanel rootPanel;
    private JTable driversOnRouteTable;
    private JTable ExistingStopsTable;
    private JTable ExistingDriversTable;
    private JScrollPane scrollPanel1;
    private JTable stopsOnRouteTable;
    private JButton addNewDriverButton;
    private JButton addNewStopButton;
    private JButton okButton;
    private JSpinner StartTimeSpinner;
    private JSpinner EndTimeSpinner;
    private JFormattedTextField idField;
    private HashSet<Integer> currentDriversIDs = new HashSet<>();
    private ArrayList<String> currentStops = new ArrayList<>();
    private int modifiableID; // -1 open form for data enrty

    ChangeForm(int modifiableID) {
        this.modifiableID = modifiableID;
        $$$setupUI$$$();
        setContentPane(rootPanel);
        setModal(true);
        setSize(new Dimension(1000, 500));
        addNewDriverButton.addActionListener(e -> {
            try {
                addNewDriver();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });
        doubleClickTransitBetweenTablesWithTwoColumns(ExistingDriversTable, driversOnRouteTable);
        doubleClickTransitBetweenTables(ExistingStopsTable, stopsOnRouteTable);
        doubleClickTransitBetweenTablesWithTwoColumns(driversOnRouteTable, ExistingDriversTable);
        doubleClickTransitBetweenTables(stopsOnRouteTable, ExistingStopsTable);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        addNewStopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String stop = JOptionPane.showInputDialog("Введите название остановки");
                if (stop != null && !stop.equals("")) {
                    DefaultTableModel model = (DefaultTableModel) ExistingStopsTable.getModel();
                    model.addRow(new Object[]{stop});
                    try {
                        dbClass.addNewStop(stop);
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                    model.fireTableDataChanged();
                    logger.debug("Новая остановка добавлена");
                }
            }
        });
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // adding route
                try {
                    validateData();
                    if (modifiableID == -1) {
                        addRouteToTable();
                    } else {
                        changeRoute();
                    }
                    setVisible(false);
                    dispose();
                } catch (IllegalDataException | SQLException ex) {
                    JOptionPane.showMessageDialog(null, ex.getMessage());
                }
            }
        });
        // setup 1го спиннера
        JSpinner.DateEditor editor = new JSpinner.DateEditor(StartTimeSpinner, "HH:mm");
        DateFormatter formatter = (DateFormatter) editor.getTextField().getFormatter();
        formatter.setAllowsInvalid(false); // this makes what you want
        formatter.setOverwriteMode(true);
        StartTimeSpinner.setEditor(editor);
        // setup 2го спиннера
        JSpinner.DateEditor editor1 = new JSpinner.DateEditor(EndTimeSpinner, "HH:mm");
        DateFormatter formatter1 = (DateFormatter) editor1.getTextField().getFormatter();
        formatter1.setAllowsInvalid(false); // this makes what you want
        formatter1.setOverwriteMode(true);
        EndTimeSpinner.setEditor(editor1);
        logger.info("UI загружен");
    }

    private void addRouteToTable() throws SQLException {
        int id = Integer.parseInt(idField.getText());
        dbClass.insertRoute(createRouteFromData(), id);

    }

    private Route createRouteFromData() {
        for (int i = 0; i < driversOnRouteTable.getRowCount(); i++) {
            int driver_id = (Integer) driversOnRouteTable.getValueAt(i, 0); ///???
            currentDriversIDs.add(driver_id);
        }
        for (int i = 0; i < stopsOnRouteTable.getRowCount(); i++) {
            String stop = (String) stopsOnRouteTable.getValueAt(i, 0);
            currentStops.add(stop);
        }
        Format formatter = new SimpleDateFormat("HH.mm");
        String startTime = formatter.format((Date) StartTimeSpinner.getValue());
        String endTime = formatter.format((Date) EndTimeSpinner.getValue());
        return new Route(currentDriversIDs, currentStops, startTime + " - " + endTime);
    }

    private void changeRoute() throws SQLException {
        int id = Integer.parseInt(idField.getText());
        // db update
        dbClass.updateRoute(createRouteFromData(), modifiableID, id);
    }


    public static void main(String[] args) {
        ChangeForm dialog = new ChangeForm(-1);
        //dialog.getContentPane().setPreferredSize(new Dimension(500, 1000));
        dialog.pack();
        dialog.setVisible(true);
    }


    private void validateData() throws IllegalDataException {
        if (driversOnRouteTable.getRowCount() == 0)
            throw new IllegalDataException("Добаьте хотя бы 1 водителя");
        if (stopsOnRouteTable.getRowCount() < 2)
            throw new IllegalDataException("Добаьте хотя бы 2 остановки");
        int id = Integer.parseInt(idField.getText());
        if (id != modifiableID && dbClass.getAllRoutesID().contains(id)) {
            throw new IllegalDataException("Такой id уже есть");
        }
        Date start = (Date) StartTimeSpinner.getValue();
        Date end = (Date) EndTimeSpinner.getValue();
        if (start.after(end))
            throw new IllegalDataException("Время страрта должно быть меньше времени окончания");
        logger.info("Данные успешно прошли проверку");
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

    private void doubleClickTransitBetweenTablesWithTwoColumns(JTable src, JTable dst) {
        src.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && !e.isConsumed()) {
                    Point point = e.getPoint();
                    int row = src.rowAtPoint(point);
                    Integer id = (Integer) src.getValueAt(row, 0);
                    String info = (String) src.getValueAt(row, 1);
                    DefaultTableModel dst_model = (DefaultTableModel) dst.getModel();
                    DefaultTableModel src_model = (DefaultTableModel) src.getModel();
                    dst_model.addRow(new Object[]{id, info});
                    dst_model.fireTableDataChanged();
                    src_model.removeRow(row);
                }
            }
        });

    }

    private void addNewDriver() throws SQLException {
        DriverForm driverForm = new DriverForm(null);
        driverForm.setVisible(true);
//        Проверка на изменения
        int tmpID = driverForm.getDriverID();
        if (tmpID != -1) {
            DefaultTableModel model = (DefaultTableModel) ExistingDriversTable.getModel();
            model.addRow(new Object[]{tmpID, dbClass.getDriverFIOByKey(tmpID)});
            model.fireTableDataChanged();
            logger.info("Новый водитель успешно добавлен");
        }
    }

    private Object[][] setDriversOnRouteTable() throws SQLException {
        if (modifiableID != -1) {
            HashMap<Integer, String> DriversID = dbClass.getDriversOnRoute(modifiableID);
            Object[][] result = new Object[DriversID.size()][2];
            int i = 0;
            for (int t : DriversID.keySet()) {
                result[i][0] = t;
                result[i][1] = DriversID.get(t);
                i++;
            }
            return result;
        }
        return null;
    }

    private Object[][] driversDataToTable() throws SQLException {
        HashMap<Integer, String> freeDriversID = dbClass.getFreeDrivers();
        Object[][] result = new Object[freeDriversID.size()][2];
        int i = 0;
        for (int t : freeDriversID.keySet()) {
            result[i][0] = t;
            result[i][1] = freeDriversID.get(t);
            i++;
        }
        return result;
    }

    private Object[][] setExistingStopsTable() throws SQLException {
        ArrayList<String> tmp = dbClass.getAllStops();
        if (modifiableID != -1) {
            tmp.removeAll(dbClass.getRouteStops(modifiableID));
        }
        Object[][] result = new Object[tmp.size()][1];
        int i = 0;
        for (String stop : tmp) {
            result[i][0] = stop.strip();
            i++;
        }
        return result;
    }

    private Object[][] setStopsOnRouteTable() throws SQLException {
        if (modifiableID != -1) {
            return convertArrListTo2dArr(dbClass.getRouteStops(modifiableID));
        }
        return null;
    }

    ///// utility
    private Object[][] convertArrListTo2dArr(ArrayList<String> arrayList) {
        Object[][] result = new Object[arrayList.size()][1];
        int i = 0;
        for (String stop : arrayList) {
            result[i][0] = stop;
            i++;
        }
        return result;
    }

    //////

    private void initTables() throws SQLException {

        var driveModel = new DefaultTableModel(driversDataToTable(), new String[]{"id", "Все Водители"}) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        ExistingDriversTable = new JTable(driveModel);

        var stopsModel = new DefaultTableModel(setExistingStopsTable(), new String[]{"Остановки"}) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        ExistingStopsTable = new JTable(stopsModel);

        var driveModel_1 = new DefaultTableModel(setDriversOnRouteTable(), new String[]{"id", "Водители на маршруте"}) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        driversOnRouteTable = new JTable(driveModel_1);
        var stopsModel_1 = new DefaultTableModel(setStopsOnRouteTable(), new String[]{"Остановки на маршруте"}) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        stopsOnRouteTable = new JTable(stopsModel_1);
    }

    private void setTimeSpinners() throws ParseException, SQLException {
        SpinnerDateModel model = new SpinnerDateModel();
        SpinnerDateModel model1 = new SpinnerDateModel();
        if (modifiableID != -1) {
            String dates_raw = dbClass.getRouteTime(modifiableID);
            String dateS = dates_raw.split("-")[0].strip();
            String dateF = dates_raw.split("-")[1].strip();
            Format formatter = new SimpleDateFormat("HH.mm");
            Date startTime = (Date) formatter.parseObject(dateS);
            Date endTime = (Date) formatter.parseObject(dateF);
            model.setValue(startTime);
            model1.setValue(endTime);
        } else {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, 24); // 24 == 12 PM == 00:00:00
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            model.setValue(calendar.getTime());
            model1.setValue(calendar.getTime());
        }
        StartTimeSpinner = new JSpinner(model);
        EndTimeSpinner = new JSpinner(model1);

    }

    private void setIdField() {
        NumberFormat format = NumberFormat.getInstance();
        NumberFormatter formatter = new NumberFormatter(format);
        formatter.setValueClass(Integer.class);
        formatter.setMinimum(0);
        formatter.setMaximum(Integer.MAX_VALUE);
        formatter.setAllowsInvalid(false);
        // If you want the value to be committed on each keystroke instead of focus lost
        formatter.setCommitsOnValidEdit(true);
        idField = new JFormattedTextField(formatter);

        if (modifiableID != -1) {
            idField.setText(Integer.toString(modifiableID));
        } else {
            idField.setText("0");
        }
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
        rootPanel.setLayout(new GridLayoutManager(8, 5, new Insets(0, 0, 0, 0), -1, -1));
        scrollPanel1 = new JScrollPane();
        rootPanel.add(scrollPanel1, new GridConstraints(2, 2, 4, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(303, 427), null, 0, false));
        scrollPanel1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-4473925)), null));
        scrollPanel1.setViewportView(driversOnRouteTable);
        final JScrollPane scrollPane1 = new JScrollPane();
        rootPanel.add(scrollPane1, new GridConstraints(2, 0, 4, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(608, 427), null, 0, false));
        scrollPane1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-4473925)), null));
        scrollPane1.setViewportView(ExistingDriversTable);
        final JScrollPane scrollPane2 = new JScrollPane();
        rootPanel.add(scrollPane2, new GridConstraints(2, 3, 4, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        ExistingStopsTable.setAutoCreateRowSorter(false);
        scrollPane2.setViewportView(ExistingStopsTable);
        final JScrollPane scrollPane3 = new JScrollPane();
        rootPanel.add(scrollPane3, new GridConstraints(2, 4, 4, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        stopsOnRouteTable.setAutoCreateRowSorter(false);
        scrollPane3.setViewportView(stopsOnRouteTable);
        addNewDriverButton = new JButton();
        addNewDriverButton.setText("Добавить");
        addNewDriverButton.setToolTipText("Добавть нового водителя");
        rootPanel.add(addNewDriverButton, new GridConstraints(6, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        addNewStopButton = new JButton();
        addNewStopButton.setText("Добавить");
        addNewStopButton.setToolTipText("Добавть новую остановку");
        rootPanel.add(addNewStopButton, new GridConstraints(6, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        rootPanel.add(spacer1, new GridConstraints(7, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        okButton = new JButton();
        okButton.setText("Ok");
        rootPanel.add(okButton, new GridConstraints(7, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        rootPanel.add(spacer2, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new BorderLayout(0, 0));
        rootPanel.add(panel1, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel1.add(StartTimeSpinner, BorderLayout.WEST);
        panel1.add(EndTimeSpinner, BorderLayout.EAST);
        final JLabel label1 = new JLabel();
        label1.setText("Номер маршрута");
        rootPanel.add(label1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        idField.setText("");
        rootPanel.add(idField, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return rootPanel;
    }

    private void createUIComponents() {
        try {
            setTimeSpinners();
            setIdField();
            initTables();
        } catch (ParseException | SQLException e) {
            e.printStackTrace();
        }

    }
}

