import org.xml.sax.SAXException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.StringJoiner;
import java.util.regex.PatternSyntaxException;

public class mainForm extends JDialog {
    // logger
    private final Logger logger = LoggerFactory.getLogger(mainForm.class);

    //UI
    private JPanel contentPane;
    private JButton addButton;
    private JButton deleteButton;
    private JButton saveButton;
    private JToolBar ToolBar;
    private JButton questionButton;
    private JTable mainTable;
    private JButton routeButton;
    private JButton driverButton;
    private JPanel subPanel;
    private JTextField searchField;
    private JScrollPane mainScrollPanel;
    private JCheckBox registerCheckBox;
    private TableRowSorter<TableModel> rowSorter;
    private String[] header = new String[]{"ID", "Водители", "Маршрут", "График"};
    private String test = "ул. Пупкина, ул. Мохнатова, ул. Стремина";

    private void createData() {
        try {
            dbClass.Conn();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        logger.debug("Начальные данные загружены");
    }

    private void updateTable() {
        // clearing table
        DefaultTableModel model = (DefaultTableModel) mainTable.getModel();
        model.setRowCount(0);
        // add data back
        Object[][] tableData = createTableData();
        for (Object[] tableDatum : tableData) {
            model.addRow(tableDatum);
        }
        model.fireTableDataChanged();
        logger.debug("Таблица обновлена");
    }

    private Object[][] createTableData() {
        try {
            HashMap<Integer, Route> data = dbClass.ReadRouteBD();
            Object[][] result = new Object[data.size()][4];
            int i = 0;
            for (int route_id : data.keySet()) {
                Route route = data.get(route_id);
                result[i][0] = route_id;
                StringJoiner joiner = new StringJoiner(",");
                for (String name : dbClass.getDriversOnRoute(route_id).values()) {
                    joiner.add(name);
                }
                result[i][1] = joiner.toString();
                result[i][2] = route.displayShortRoute();
                result[i][3] = route.getTime();
                i++;
            }
            return result;
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
        logger.debug("Данные успешно загружены");
        return null;
    }

    private mainForm() {
        //Конструктор формы
        createData();
        $$$setupUI$$$();
        setContentPane(contentPane);
        setModal(true);
        // Listeners:
        routeButton.addActionListener(e -> addNewRout());
        // editing
        mainTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && !e.isConsumed()) {
//                    JOptionPane.showMessageDialog(null, "Проверка нажатия на кнопку");
                    Point point = e.getPoint();
                    int row = mainTable.rowAtPoint(point);
                    int id = (int) mainTable.getValueAt(row, 0);
                    ChangeForm form1 = new ChangeForm(id);
                    form1.setVisible(true);
                    updateTable();
                }
            }
        });
        // search
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                findInTable(searchField.getText());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                findInTable(searchField.getText());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                throw new UnsupportedOperationException();
            }
        });
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
//                try {
//                    saveData();
//                    XMLWrapper.writeXML(mainTable, "table.xml");
//                    CustomThreads.doExperiment(mainTable);
                JOptionPane.showMessageDialog(null, "Данные успешно записаны");
                //                } catch (IOException | TransformerException | ParserConfigurationException ex) {
//                    JOptionPane.showMessageDialog(null, "Ошибка записи!!!");
//                }
            }
        });
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
//                    loadData();
                    //
                    DefaultTableModel model = (DefaultTableModel) mainTable.getModel();
                    // clear table
                    model.setRowCount(0);
                    XMLWrapper.readXML(model, "table.xml");

                    JOptionPane.showMessageDialog(null, "Данные успешно загружены");
                } catch (IOException | ParserConfigurationException | SAXException ex) {
                    JOptionPane.showMessageDialog(null, "Ошибка загрузки");
                }
            }
        });
        Action deleteRows = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                //do nothing
                int i = JOptionPane.showConfirmDialog(null, "Вы действительно хотите удалить информацию из таблицы?",
                        "Удаление", JOptionPane.YES_NO_OPTION);
                if (i == 0) {
                    int start = mainTable.getSelectedRow();
                    int interval = mainTable.getSelectedRowCount();
                    System.out.printf("%d, %d", start, interval - 1);
                    DefaultTableModel model = (DefaultTableModel) mainTable.getModel();

                    for (int j = 0; j < interval; j++) {
                        int route_id = (int) model.getValueAt(start, 0);
                        try {
                            dbClass.deleteRoute(route_id);
                            logger.info(String.format("Маршрут № %d успешно удален", route_id));
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                        }
                        model.removeRow(start);
                    }
                    model.fireTableDataChanged();

                }

            }
        };
        mainTable.getInputMap().put(KeyStroke.getKeyStroke("DELETE"),
                "deleteRows");
        mainTable.getActionMap().put("deleteRows",
                deleteRows);

        logger.info("UI загружен");
        driverButton.addActionListener(e ->  {
           DriversDialog dialog = new DriversDialog();
           dialog.setVisible(true);
        });
    }

    /// debug
    private void findInTable(String text) {
        if (text.trim().length() == 0) {
            rowSorter.setRowFilter(null);
        } else {
            try {
                if (registerCheckBox.isSelected()) {
                    rowSorter.setRowFilter(RowFilter.regexFilter(text));
                } else {
                    rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
                }
            } catch (PatternSyntaxException e) {
                rowSorter.setRowFilter(null);
                logger.debug("Введено неправильное регулярное выражение");
            }
        }
    }


    private void addNewRout() {
        ChangeForm form1 = new ChangeForm(-1);
        form1.setVisible(true);
        updateTable();
    }

    private void setIcons() {
        /*
          Установка иконок

          */
        buttonConverter(addButton);
        addButton.setIcon(new ImageIcon("icons/play-button.png"));
        addButton.setPressedIcon(new ImageIcon("icons/play-deleteButton.png"));
        buttonConverter(deleteButton);
        deleteButton.setIcon(new ImageIcon("icons/minus.png"));
        deleteButton.setPressedIcon(new ImageIcon("icons/minus (1).png"));
        buttonConverter(saveButton);
        saveButton.setIcon(new ImageIcon("icons/save (1).png"));
        saveButton.setPressedIcon(new ImageIcon("icons/save (2).png"));
        buttonConverter(questionButton);
        questionButton.setIcon(new ImageIcon("icons/question.png"));
        questionButton.setPressedIcon(new ImageIcon("icons/question (1).png"));

    }

    private void buttonConverter(JButton button) {
        // От кнопки остается только значок
        button.setBorderPainted(false);
        button.setBorder(null);
        button.setFocusable(false);
        button.setMargin(new Insets(0, 0, 0, 0));
        button.setContentAreaFilled(false);
    }

    private void saveData() throws IOException {
        FileWriter writer = new FileWriter("save.txt");
        for (Object[] data : createTableData()) {
            String row = data[0] + "|" + data[1] + "|" + data[2] + "\n";
            writer.write(row);
        }
        writer.close();
    }

    private void loadData() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader("save.txt"));
        DefaultTableModel model = (DefaultTableModel) mainTable.getModel();
        model.setRowCount(0);
        while (reader.ready()) {
            String row = reader.readLine();
            Object[] data = row.split("\\|");
            model.addRow(data);
        }
        model.fireTableDataChanged();
    }

    public static void main(String[] args) {
        mainForm dialog = new mainForm();
        dialog.getContentPane().setPreferredSize(new Dimension(1000, 500));
        dialog.setIcons();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
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
        contentPane = new JPanel();
        contentPane.setLayout(new BorderLayout(0, 0));
        contentPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLoweredBevelBorder(), null));
        ToolBar = new JToolBar();
        ToolBar.setFloatable(false);
        ToolBar.setMargin(new Insets(5, 5, 5, 5));
        contentPane.add(ToolBar, BorderLayout.NORTH);
        addButton = new JButton();
        addButton.setEnabled(true);
        addButton.setHideActionText(false);
        addButton.setText("");
        addButton.setToolTipText("Добавить информацию");
        ToolBar.add(addButton);
        final JToolBar.Separator toolBar$Separator1 = new JToolBar.Separator();
        ToolBar.add(toolBar$Separator1);
        deleteButton = new JButton();
        deleteButton.setText("");
        deleteButton.setToolTipText("Удалить инфу");
        ToolBar.add(deleteButton);
        final JToolBar.Separator toolBar$Separator2 = new JToolBar.Separator();
        ToolBar.add(toolBar$Separator2);
        saveButton = new JButton();
        saveButton.setText("");
        saveButton.setToolTipText("Сохранить");
        ToolBar.add(saveButton);
        final JToolBar.Separator toolBar$Separator3 = new JToolBar.Separator();
        ToolBar.add(toolBar$Separator3);
        questionButton = new JButton();
        questionButton.setText("");
        questionButton.setToolTipText("Справка");
        ToolBar.add(questionButton);
        final JToolBar.Separator toolBar$Separator4 = new JToolBar.Separator();
        ToolBar.add(toolBar$Separator4);
        final JLabel label1 = new JLabel();
        label1.setText("Поиск по");
        ToolBar.add(label1);
        final JToolBar.Separator toolBar$Separator5 = new JToolBar.Separator();
        ToolBar.add(toolBar$Separator5);
        final JToolBar.Separator toolBar$Separator6 = new JToolBar.Separator();
        ToolBar.add(toolBar$Separator6);
        searchField = new JTextField();
        ToolBar.add(searchField);
        registerCheckBox = new JCheckBox();
        registerCheckBox.setText("регистр");
        registerCheckBox.setToolTipText("Игнорировать регистр?");
        ToolBar.add(registerCheckBox);
        subPanel = new JPanel();
        subPanel.setLayout(new BorderLayout(0, 0));
        contentPane.add(subPanel, BorderLayout.SOUTH);
        routeButton = new JButton();
        routeButton.setText("Добавить новый маршрут");
        subPanel.add(routeButton, BorderLayout.WEST);
        driverButton = new JButton();
        driverButton.setText("Просмотр списка водителей");
        subPanel.add(driverButton, BorderLayout.EAST);
        mainScrollPanel = new JScrollPane();
        mainScrollPanel.setHorizontalScrollBarPolicy(31);
        contentPane.add(mainScrollPanel, BorderLayout.CENTER);
        mainTable.setShowHorizontalLines(true);
        mainTable.setShowVerticalLines(true);
        mainScrollPanel.setViewportView(mainTable);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return contentPane;
    }

    private void createUIComponents() {
        DefaultTableModel model;
        // TODO: place custom component creation code here
        model = new DefaultTableModel(createTableData(), header) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        mainTable = new JTable(model);
        rowSorter = new TableRowSorter<TableModel>(model);
        mainTable.setRowSorter(rowSorter);
    }
}
