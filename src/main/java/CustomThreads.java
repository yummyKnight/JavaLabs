import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private final Logger logger = LoggerFactory.getLogger(ThreadHTML.class);
    private JTable thisIsWrong;
    ThreadHTML(String name, JTable table) {
        super("ThreadHTML_" + name);
        this.thisIsWrong = table;
    }

    @Override
    public void run() {
        try {
            logger.debug("Нить ThreadHTML начала выполнение");
            changeXMLThread thread = new changeXMLThread("1",thisIsWrong);
            thread.start();
            thread.join();
            ReportGen.createHTML("table.xml", "template.xslt", "sample.html");
            logger.debug("Нить ThreadHTML закончила выполнение");
        } catch (TransformerException | IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
class changeXMLThread extends Thread {
    private final Logger logger = LoggerFactory.getLogger(changeXMLThread.class);
    private JTable thisIsWrong;
     changeXMLThread(String name, JTable table) {
        super("changeXMLThread_" + name);
        this.thisIsWrong = table;
    }
    public void run() {
        try {
            logger.debug("Нить changeXMLThread начала выполнение");
            loadFromXMLThread thread = new loadFromXMLThread("1", thisIsWrong);
            thread.start();
            thread.join();
            DefaultTableModel model = (DefaultTableModel) thisIsWrong.getModel();
            model.addRow(new Object[] {"Bla1", "Bla2", "Bla3"});
            model.fireTableDataChanged();
            XMLWrapper.writeXML(thisIsWrong,"table.xml");
            logger.debug("Нить changeXMLThread закончила выполнение");
        } catch (InterruptedException | TransformerException | IOException | ParserConfigurationException e) {
            e.printStackTrace();
        }
    }
}
class loadFromXMLThread extends Thread{
    private final Logger logger = LoggerFactory.getLogger(loadFromXMLThread.class);
    private JTable thisIsWrong;
     loadFromXMLThread(String name, JTable table) {
        super("loadFromXMLThread_" + name);
        this.thisIsWrong = table;
    }

    @Override
    public void run() {
        try {
            logger.debug("Нить loadFromXMLThread начала выполнение");
            var model = (DefaultTableModel)thisIsWrong.getModel();
            model.setRowCount(0);
            XMLWrapper.readXML(model, "table.xml");
            model.fireTableDataChanged();
            logger.debug("Нить loadFromXMLThread закончила выполнение");
        } catch (ParserConfigurationException | IOException | SAXException e) {
            e.printStackTrace();
        }
    }
}