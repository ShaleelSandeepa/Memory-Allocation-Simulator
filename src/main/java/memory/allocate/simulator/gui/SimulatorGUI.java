package memory.allocate.simulator.gui;

import memory.allocate.simulator.MainServer;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class SimulatorGUI extends JFrame {

    private MainServer mainServer = null;
    private JPanel jContentPane = null;
    private JPanel mainPanel = null;
    private JPanel jPanel = null;
    private JPanel jPanel1 = null;
    private JPanel jPanel2 = null;

    private JPanel inputPanel = null;



    private JPanel getJContentPane() {
        if (jContentPane == null) {
            GridLayout gridLayout = new GridLayout();
            gridLayout.setRows(1);
            jContentPane = new JPanel();
            jContentPane.setLayout(gridLayout);
            jContentPane.add(getMainPanel(), null);
        }
        return jContentPane;
    }

    private JPanel getMainPanel() {
        if (mainPanel == null) {
            GridLayout gridLayout1 = new GridLayout();
            gridLayout1.setRows(1);
            mainPanel = new JPanel();
            mainPanel.setLayout(gridLayout1);
            mainPanel.add(getJPanel(), null);
        }
        return mainPanel;
    }

    private JPanel getJPanel() {
        if (jPanel == null) {
            jPanel = new JPanel();
            jPanel.setLayout(new BoxLayout(getJPanel(), BoxLayout.Y_AXIS));
            jPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            jPanel.add(getJPanel1(), null);
        }
        return jPanel;
    }

    private JPanel getJPanel1() {
        if (jPanel1 == null) {
            BorderLayout borderLayout = new BorderLayout();
            borderLayout.setHgap(5);
            borderLayout.setVgap(5);
            jPanel1 = new JPanel();
            jPanel1.setLayout(borderLayout);

            // Add components to the panel
            jPanel1.add(getInputPanel(), BorderLayout.NORTH); // Panel for input fields and buttons
            jPanel1.add(getJobTablePanel(), BorderLayout.WEST); // Panel for the job table
            jPanel1.add(getExecutionPanel(), BorderLayout.CENTER); // Panel for execution information
            jPanel1.add(getStackPanel(), BorderLayout.EAST); // Panel for stack visualization

            // Optional: Add a border to the panel itself
            jPanel1.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        }
        return jPanel1;
    }


    private JPanel getInputPanel() {

        if (jPanel2 == null) {
            BorderLayout borderLayout = new BorderLayout();
            borderLayout.setHgap(5);
            borderLayout.setVgap(5);
            jPanel2 = new JPanel();
            jPanel2.setLayout(borderLayout);
            jPanel2.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

            jPanel2.add(getInputPanelInsidePanel(), BorderLayout.WEST); // Panel for input fields and buttons
        }
        return jPanel2;
    }

    private JPanel getInputPanelInsidePanel() {
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new FlowLayout());
        inputPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));

        JLabel jobNameLabel = new JLabel("Job Name:");
        JTextField jobNameField = new JTextField(20);

        JLabel jobSizeLabel = new JLabel("Job Size:");
        JTextField jobSizeField = new JTextField(5);

        JButton insertButton = new JButton("Insert Job");
        insertButton.addActionListener(e -> {
            String jobName = jobNameField.getText();
            String jobSize = jobSizeField.getText();

            if (!jobName.isEmpty() && !jobSize.isEmpty()) {
                addJobToTable(jobName, jobSize, "Pending");
                jobNameField.setText("");
                jobSizeField.setText("");
            }
        });

        inputPanel.add(jobNameLabel);
        inputPanel.add(jobNameField);
        inputPanel.add(jobSizeLabel);
        inputPanel.add(jobSizeField);
        inputPanel.add(insertButton);

        return inputPanel;
    }

    private JPanel getExecutionPanel() {
        JPanel executionPanel = new JPanel();
        executionPanel.setBorder(BorderFactory.createTitledBorder("Execution Details"));
        executionPanel.setLayout(new FlowLayout());

        JLabel executionLabel = new JLabel("Execution Panel: Add custom execution logic here");
        executionPanel.add(executionLabel);

        return executionPanel;
    }


    // Panel for the job table
    private JPanel getJobTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());

        String[] columnNames = {"Name", "Size", "Status"};
        Object[][] data = {};
        JTable jobTable = new JTable(new DefaultTableModel(data, columnNames));
        JScrollPane scrollPane = new JScrollPane(jobTable);

        tablePanel.add(scrollPane, BorderLayout.CENTER);

        // Store table model for later updates
        jobTable.setName("jobTable"); // Optional: Name for identification

        return tablePanel;
    }

    // Add a job to the job table
    private void addJobToTable(String name, String size, String status) {
        JTable jobTable = getTableFromPanel(jPanel1, "jobTable");
        DefaultTableModel model = (DefaultTableModel) jobTable.getModel();
        model.addRow(new Object[]{name, size, status});
    }

    // Retrieve JTable from the panel using the name
    private JTable getTableFromPanel(Container panel, String tableName) {
        for (Component component : panel.getComponents()) {
            if (component instanceof JScrollPane) {
                Component view = ((JScrollPane) component).getViewport().getView();
                if (view instanceof JTable && tableName.equals(view.getName())) {
                    return (JTable) view;
                }
            } else if (component instanceof Container) {
                JTable table = getTableFromPanel((Container) component, tableName);
                if (table != null) {
                    return table;
                }
            }
        }
        return null;
    }


    // Panel for stack visualization
    private JPanel getStackPanel() {
        JPanel stackPanel = new JPanel();
        stackPanel.setLayout(new BoxLayout(stackPanel, BoxLayout.Y_AXIS));
        stackPanel.setBorder(BorderFactory.createTitledBorder("Stack Visualization"));

        // Example block representation
        int[] blockSizes = {50, 30, 20, 40}; // Sample block sizes

        for (int blockSize : blockSizes) {
            JLabel block = new JLabel("Block Size: " + blockSize);
            block.setOpaque(true);
//            block.setBackground(Color.BLUE);
            block.setForeground(Color.BLUE);
            block.setPreferredSize(new Dimension(100, blockSize));
            block.setHorizontalAlignment(SwingConstants.CENTER);
            stackPanel.add(block);
        }

        return stackPanel;
    }


    public void loadGui(MainServer mainServer){
        this.mainServer=mainServer;
        initialize();
//        loadData();
    }

    private void initialize() {
        this.setSize(600, 600);

        Toolkit tk=this.getToolkit();
        Dimension dm=tk.getScreenSize();

        this.setLocation(((dm.width-600)/2), ((dm.height-600)/2));
        this.setContentPane(getJContentPane());
        this.setTitle("Memory Allocation Simulator By Shaleel Sandeepa ");
        this.setVisible(true);
        this.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent e) {
                System.out.println("Simulator Stopped.");
                System.exit(0);
            }
        });
    }

}
