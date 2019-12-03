import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.HashMap;

public class DriversDialog extends JDialog {
    private final Logger logger = LoggerFactory.getLogger(mainForm.class);
    private JPanel rootPanel;
    private JTable DriversTable;
    private JButton addButton;
    private JButton closeButton;

    DriversDialog() {
        //Конструктор формы
        createData();
        $$$setupUI$$$();
        setContentPane(rootPanel);
        setSize(new Dimension(1000, 500));
        setModal(true);
        addListeners();
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    private void addListeners() {
        // add new data
        addButton.addActionListener(e -> {
            DriverForm driverForm = new DriverForm(null);
            driverForm.setVisible(true);
            int tmpID = driverForm.getDriverID();
            if (tmpID != -1) {
                DefaultTableModel model = (DefaultTableModel) DriversTable.getModel();
                try {
                    model.addRow(new Object[]{tmpID, dbClass.getDriverFIOByKey(tmpID)});
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }

                updateTable();
                logger.info("Новый водитель успешно добавлен");
            }
        });
        // close form
        closeButton.addActionListener(e -> {
                    setVisible(false);
                    dispose();
                }

        );
        // delete data
        Action deleteRows = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                //do nothing
                int i = JOptionPane.showConfirmDialog(null, "Вы действительно хотите удалить информацию из таблицы?",
                        "Удаление", JOptionPane.YES_NO_OPTION);
                if (i == 0) {
                    int start = DriversTable.getSelectedRow();
                    int interval = DriversTable.getSelectedRowCount();
                    System.out.printf("%d, %d", start, interval - 1);
                    DefaultTableModel model = (DefaultTableModel) DriversTable.getModel();

                    for (int j = 0; j < interval; j++) {
                        int route_id = (int) model.getValueAt(start, 0);
                        try {
                            dbClass.deleteDriver(route_id);
                            logger.info(String.format("Водитель № %d успешно удален", route_id));
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                        }
                        model.removeRow(start);
                    }
                    model.fireTableDataChanged();

                }

            }
        };
        DriversTable.getInputMap().put(KeyStroke.getKeyStroke("DELETE"),
                "deleteRows");
        DriversTable.getActionMap().put("deleteRows",
                deleteRows);
        // edit data
        DriversTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && !e.isConsumed()) {
//                    JOptionPane.showMessageDialog(null, "Проверка нажатия на кнопку");
                    Point point = e.getPoint();
                    int row = DriversTable.rowAtPoint(point);
                    int id = (int) DriversTable.getValueAt(row, 0);
                    try {
                        Driver driver = dbClass.getDriverByKey(id);
                        DriverForm form1 = new DriverForm(driver);
                        form1.setDriverID(id);
                        form1.loadData();
                        form1.setVisible(true);
                        updateTable();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
    }


    Object[] header = new Object[]{"id", "Стаж работы (в годах)", "Класс", "ФИО", "Нарушения"};

    private void setDriversTable() {
        DefaultTableModel model = new DefaultTableModel(createData(), header) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        DriversTable = new JTable(model);

    }

    private void updateTable() {
        // clearing table
        DefaultTableModel model = (DefaultTableModel) DriversTable.getModel();
        model.setRowCount(0);
        // add data back
        Object[][] tableData = createData();
        for (Object[] tableDatum : tableData) {
            model.addRow(tableDatum);
        }
        model.fireTableDataChanged();
        logger.debug("Таблица обновлена");
    }

    private Object[][] createData() {
        try {
            HashMap<Integer, Driver> data = dbClass.ReadDriversBD();
            Object[][] result = new Object[data.size()][5];
            int i = 0;
            for (int driver_id : data.keySet()) {
                Driver driver = data.get(driver_id);
                result[i][0] = driver_id;
                result[i][1] = driver.getExperience();
                result[i][2] = driver.getClassification();
                result[i][3] = driver.getFIO();
                if (driver.getViolations() == null) {
                    result[i][4] = "Отсутствуют";
                } else {
                    result[i][4] = driver.getViolations();
                }
                i++;
            }
            return result;
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
        logger.debug("Данные успешно загружены");
        return null;
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
        rootPanel.setLayout(new BorderLayout(0, 0));
        final JScrollPane scrollPane1 = new JScrollPane();
        rootPanel.add(scrollPane1, BorderLayout.CENTER);
        scrollPane1.setViewportView(DriversTable);
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new BorderLayout(0, 0));
        rootPanel.add(panel1, BorderLayout.SOUTH);
        addButton = new JButton();
        addButton.setText("Добавить");
        panel1.add(addButton, BorderLayout.WEST);
        closeButton = new JButton();
        closeButton.setText("Закрыть");
        panel1.add(closeButton, BorderLayout.EAST);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return rootPanel;
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
        setDriversTable();
    }
}
