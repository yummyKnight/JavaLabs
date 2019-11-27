import org.xml.sax.SAXException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;

public class CustomThreads {
    public static void doExperiment(JTable table) {
        ThreadHTML threadHTML = new ThreadHTML("Bruh",table);
        threadHTML.start();
    }
}
class ThreadHTML extends Thread {
    private JTable thisIsWrong;
    ThreadHTML(String name, JTable table) {
        super("ThreadHTML_" + name);
        this.thisIsWrong = table;
    }

    @Override
    public void run() {
        try {
            changeXMLThread thread = new changeXMLThread("1",thisIsWrong);
            thread.start();
            thread.join();
            ReportGen.createHTML("table.xml", "template.xslt", "sample.html");
            System.out.println("ThreadHTML done");
        } catch (TransformerException | IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
class changeXMLThread extends Thread {
    private JTable thisIsWrong;
     changeXMLThread(String name, JTable table) {
        super("changeXMLThread_" + name);
        this.thisIsWrong = table;
    }
    public void run() {
        try {
            loadFromXMLThread thread = new loadFromXMLThread("1", thisIsWrong);
            thread.start();
            thread.join();
            DefaultTableModel model = (DefaultTableModel) thisIsWrong.getModel();
            model.addRow(new Object[] {"Bla1", "Bla2", "Bla3"});
            model.fireTableDataChanged();
            XMLWrapper.writeXML(thisIsWrong,"table.xml");
            System.out.println("changeXMLThread done");
        } catch (InterruptedException | TransformerException | IOException | ParserConfigurationException e) {
            e.printStackTrace();
        }
    }
}
class loadFromXMLThread extends Thread{
    private JTable thisIsWrong;
     loadFromXMLThread(String name, JTable table) {
        super("loadFromXMLThread_" + name);
        this.thisIsWrong = table;
    }

    @Override
    public void run() {
        try {
            var model = (DefaultTableModel)thisIsWrong.getModel();
            model.setRowCount(0);
            XMLWrapper.readXML(model, "table.xml");
            model.fireTableDataChanged();
            System.out.println("loadFromXMLThread done");
        } catch (ParserConfigurationException | IOException | SAXException e) {
            e.printStackTrace();
        }
    }
}