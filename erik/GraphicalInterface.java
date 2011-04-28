package erik;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.text.DateFormat;

public class GraphicalInterface implements ActionListener {

    private static JFrame frame = new JFrame("Messenger 0.53.35333");
    private Container cont;
    private JMenuBar menu = new JMenuBar();
    private static JPanel tabs = new JPanel();
    private static JPanel info = new JPanel();
    private static JPanel cl = new JPanel();

    private static Vector<String> tabList = new Vector<String>();
    private static JTextArea infoLog = new JTextArea();
    private static JTextField ta = new JTextField();
    private int width = -1;
    private int height = -1;

    public GraphicalInterface() {

        menuBar();
        frame.setJMenuBar(menu);
        cont = frame.getContentPane();
        BoxLayout boxLayout = new BoxLayout(cont, BoxLayout.Y_AXIS);
        frame.setLayout(boxLayout);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 800);
        frame.setVisible(true);
        frame.setResizable(true);

        init();
        commandLine();
        infoWindow();
        tabBarInit();

        frame.add(tabs);
        frame.add(Box.createVerticalStrut(2));
        frame.add(info);
        frame.add(Box.createVerticalStrut(2));
        frame.add(cl);
   
        validation();
        
    }

    private static void validation() {
        tabs.repaint();
        cl.repaint();
        info.repaint();
        frame.validate();
    }
// Main init //
    private void init() {
        height = cont.getHeight();
        width = cont.getWidth();
        Border border = BorderFactory.createLineBorder(Color.black);

        tabs.setMinimumSize(new Dimension(width, 24));
        tabs.setPreferredSize(new Dimension(width, 24));
        tabs.setBorder(border);
        info.setPreferredSize(new Dimension(width, height-54));
        info.setBorder(border);
        cl.setMinimumSize(new Dimension(width, 30));
        cl.setPreferredSize(new Dimension(width, 30));
        cl.setBorder(border);
    }

    public static void callOut() {
        addTab("Console");
        for(int i=0;i<25;i++)
        addTab("t" + i);
        validation();
    }
// Adds menubar to the frame //
    private void menuBar() {
        JMenu file = new JMenu("File");
        JMenu tools = new JMenu("Tools");
        file.add("Info");
        file.add("Exit");
        tools.add("Options");
        tools.add("About");
        menu.add(file);
        menu.add(tools);
    }
// need to add scroll, right and left click actionlistener also need to
// write method to delete tabs
// Adds the tabbar, method callout adds new tab to the list //
    private static int firstTab = 0;
    private int lastRefreshed = 0;
    private static MouseWheelListener mwl = null;
    private static ActionListener al = null;
    private static JButton queue;
    private void tabBarInit() {      
        mwl = new MouseWheelListener() {
            int maxtabs;
            int tab = 0;
            public void mouseWheelMoved(MouseWheelEvent e) {
                int scrolled = e.getScrollAmount()/3;
                if(e.getWheelRotation() == -1) {
                    tab += scrolled;
                    int size = 0;
                    //if(tab == tabList.size()) {
                        for(int i=tab ; i<tabList.size(); i++) {
                            size += (int)(tabList.get(i).length()*7.5+40);
                        }
                    System.out.println(size);
                    //if(size < width)
                        maxtabs = tab;
                //    }
                        //tab = tabList.size()-1;
                }
                if(e.getWheelRotation() == 1) {
                    tab -= scrolled;
                    if(tab <= 0)
                        tab = 0;
                }
                setFirst(tab);
                refreshTabs(tab);
            }
        };
    }

    private static void addTab(String newEntry) {
        int last = 2;

        tabs.removeAll();
        tabs.setLayout(null);
        tabs.setVisible(false);
        tabs.setVisible(true);

        for(int i=firstTab; i<tabList.size(); i++) {
            queue = new JButton(tabList.get(i));
            int length = 40+(int)(queue.getText().length()*7.5);
            queue.setBounds(last, 2, length, 20);
            last +=length+2;
            queue.addActionListener(al);
            queue.addMouseWheelListener(mwl);
            tabs.add(queue);
        }

        tabList.add(newEntry);
        queue = new JButton(newEntry);
        int length = 40+(int)(queue.getText().length()*7.5);
        queue.setBounds(last, 2, length, 20);
        queue.addActionListener(al);
        queue.addMouseWheelListener(mwl);
        tabs.add(queue);


        appendText("[KONSOLE] New tab has been added: " + newEntry + " at id: " + (tabList.size()-1));
    }

    private void refreshTabs(int tab) {
        JButton queue;
        int last = 2;
        tabs.removeAll();
        validation();
        tabs.setLayout(null);
        tabs.setVisible(false);
        tabs.setVisible(true);

        for(int i=tab; i<tabList.size(); i++) {
            queue = new JButton(tabList.get(i));
            int length = 40+(int)(queue.getText().length()*7.5);
            queue.setBounds(last, 2, length, 20);
            last +=length+2;
            queue.addActionListener(this);
            queue.addMouseWheelListener(mwl);
            tabs.add(queue);
        }
        lastRefreshed = tab;
    }

    private void closeTab(int id) {
        if(id==0)
            appendText("[KONSOLE] Sorry, but you can't close the Console tab");
        else if(id > tabList.size())
            appendText("[KONSOLE] Sorry, but the index: " +id + " doesn't exist; no action taken");
        else
            tabList.remove(id);
        refreshTabs(lastRefreshed);
    }

    private void closeTab(String name) {
        if(name.equals("Console"))
            appendText("[KONSOLE]Sorry, but you can't close the Console tab");
        else if(tabList.contains(name) == false)
            appendText("[KONSOLE] Sorry, but the tab: " + name + " doesn't exist; no action taken");
        else
            tabList.remove(name);
        refreshTabs(lastRefreshed);
    }

    public void setFirst(int value) {
        firstTab = value;
    }

// Adds the output textarea //
    private JScrollPane jsp;
    private void infoWindow() {
        info.setLayout(new BorderLayout());
        
        infoLog.setBackground(cl.getBackground());
        infoLog.setForeground(Color.BLACK);
        infoLog.setLineWrap(true);
        infoLog.setWrapStyleWord(true);
        infoLog.setEditable(false);
        jsp = new JScrollPane(infoLog);
        jsp.setBorder(new EmptyBorder(1,1,1,1));

        jsp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        jsp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        info.add(jsp, BorderLayout.CENTER);
    }

    public static void appendText(String input) {
        Calendar cal = Calendar.getInstance();
        DateFormat df = DateFormat.getDateTimeInstance(DateFormat.SHORT,
                        DateFormat.MEDIUM);

        infoLog.append(df.format(cal.getTime()));
        infoLog.append(" - ");
        infoLog.append(input);
        infoLog.append("\n");
    }

// Adds the input textfield //
    FocusListener fl = new focusAdapter();
    private void commandLine() {
        cl.setLayout(new BorderLayout());
        ta.setBorder(new EmptyBorder(1,1,1,1));
        ta.setBackground(cl.getBackground());
        ta.setForeground(Color.GRAY);
        ta.addActionListener(this);
        ta.addFocusListener(fl);
        cl.add(ta, BorderLayout.CENTER);
        ta.setText("Input goes here...");
    }

    private class focusAdapter extends FocusAdapter {
        public void focusGained(FocusEvent e) {
            if(ta.getText().equals("") || ta.getText() == null ||
               ta.getText().equals("Input goes here...")) {
                ta.setForeground(Color.BLUE);
                ta.setText(null);
            }
        }
        public void focusLost(FocusEvent e) {
            if(ta.getText().equals("") || ta.getText() == null) {
                ta.setForeground(Color.GRAY);
                ta.setText("Input goes here...");
            }
        }
    }
    
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == ta) {
            Send.sendPacket(MessengerMain.socketList.get(0), Send.encodePacket(ta.getText()));
            appendText("erik says: " + ta.getText());
            ta.setForeground(Color.BLUE);
            ta.setText(null);
        }
        else {
            System.out.println(e.getActionCommand());
        }

    }

    public class dataStore {
        ArrayList<String> data;
        ArrayList<String> timestamp;
        ArrayList<String> direction;
        public dataStore() {
           data = new ArrayList<String>();
           timestamp = new ArrayList<String>();
           direction = new ArrayList<String>();
        }
// Method to save tab logs //
        public void addData(String input, String time, String sender, String receiver) {
            timestamp.add("t " + time);
            data.add("l " + input);
            direction.add("r " + sender + " -> " + receiver);
        }
// Method to save logs to file //
        public void writeData() {

        }
    }
   /*     public static void main(String[] args) {
        new GraphicalInterface();
    }*/
}