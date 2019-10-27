import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.PatternSyntaxException;

public class mainForm extends JDialog {
    private JPanel contentPane;
    private JButton addButton;
    private JButton deleteButton;
    private JButton saveButton;
    private JToolBar ToolBar;
    private JButton questionButton;
    private JTable mainTable;
    private JButton routeButon;
    private JButton driverButton;
    private JPanel subPanel;
    private JTextField searchField;
    private JScrollPane mainScrollPanel;
    private JCheckBox registerCheckBox;
    private TableRowSorter<TableModel> rowSorter;
    private DefaultTableModel model;
    private String[] header = new String[]{"Водители", "Маршрут", "График"};
    private boolean searchMode = false;
    private String test = "ул. Пупкина, ул. Мохнатова, ул. Стремина";
    private ArrayList<Driver> allDrivers = new ArrayList<>();
    private ArrayList<Route> allRouts = new ArrayList<>();

    public ArrayList<Driver> getAllDrivers() {
        return allDrivers;
    }

    public ArrayList<Route> getAllRouts() {
        return allRouts;
    }

    private void createData() {
        int i = 0;
        for (String n : ("Лазарев Гордей Оскарович\n" +
                "Журавлёв Нинель Григорьевич\n" +
                "Архипов Лазарь Мартынович\n" +
                "Калашников Савелий Ростиславович\n" +
                "Потапов Арнольд Платонович\n" +
                "Ермаков Богдан Антонович\n" +
                "Хохлов Людвиг Давидович\n" +
                "Зайцев Аввакум Серапионович\n" +
                "Евдокимов Аристарх Матвеевич\n" +
                "Кондратьев Борис Филатович").split("\n")
        ) {
            allDrivers.add(new Driver(n, i, i));
        }
        allRouts.add(new Route(allDrivers, new ArrayList<String>(Arrays.asList(test.split(","))), "9.50 - 7.20"));
    }

    private Object[][] createTableData() {
        Object[][] result = new Object[allRouts.size()][3];
        int i = 0;
        for (Route route : allRouts) {
            result[i][0] = route.driversToString();
            result[i][1] = route.displayShortRoute();
            result[i][2] = route.getTime();
        }
        return result;
    }

    private mainForm() {
        //Конструктор формы
        createData();
        $$$setupUI$$$();
        setContentPane(contentPane);
        setModal(true);
        routeButon.addActionListener(e -> windowInvocation());

        mainTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && !e.isConsumed()) {
                    JOptionPane.showMessageDialog(null, "Проверка нажатия на кнопку");
                    Point point = e.getPoint();
                    int row = mainTable.rowAtPoint(point);
                    System.out.println(row);
                }
            }
        });

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
    }

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
            }
        }
    }

    private void windowInvocation() {
        ChangeForm form1 = new ChangeForm();
        form1.setVisible(true);
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
        routeButon = new JButton();
        routeButon.setText("Button");
        subPanel.add(routeButon, BorderLayout.WEST);
        driverButton = new JButton();
        driverButton.setText("Button");
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
