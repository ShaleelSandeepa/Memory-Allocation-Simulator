package memory.allocate.simulator.gui;

import memory.allocate.simulator.MainServer;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class SimulatorGUI extends JFrame {

    private MainServer mainServer;

    // GUI Components
    private JTextField jobNameField;
    private JTextField jobSizeField;
    private DefaultTableModel tableModel;

    public SimulatorGUI() {
        initialize();
    }

    private void initialize() {
        setTitle("Memory Allocation Simulator By Shaleel Sandeepa");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(null); // Centers the window
        setLayout(new BorderLayout());

        // Add main panels
        add(createInputPanel(), BorderLayout.NORTH);
        add(createMainContentPanel(), BorderLayout.CENTER);

        setVisible(true);
    }

    private JPanel createInputPanel() {
        JPanel inputPanel = new JPanel();
        inputPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(),
                "Add Job",
                TitledBorder.CENTER,
                TitledBorder.TOP
        ));

        JLabel jobNameLabel = new JLabel("Job Name:");
        jobNameField = new JTextField(15);

        JLabel jobSizeLabel = new JLabel("Job Size:");
        jobSizeField = new JTextField(10);

        JButton insertButton = new JButton("Insert Job");
        insertButton.addActionListener(e -> {
            String jobName = jobNameField.getText();
            String jobSize = jobSizeField.getText();

            if (!jobName.isEmpty() && !jobSize.isEmpty()) {
                addJobToTable(jobName, jobSize, "Pending");
                jobNameField.setText("");
                jobSizeField.setText("");
            } else {
                JOptionPane.showMessageDialog(this, "Please fill out both fields.", "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Layout for input panel
        GroupLayout layout = new GroupLayout(inputPanel);
        inputPanel.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        layout.setHorizontalGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                        .addComponent(jobNameLabel)
                        .addComponent(jobSizeLabel))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(jobNameField)
                        .addComponent(jobSizeField))
                .addComponent(insertButton)
        );

        layout.setVerticalGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(jobNameLabel)
                        .addComponent(jobNameField))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(jobSizeLabel)
                        .addComponent(jobSizeField)
                        .addComponent(insertButton))
        );

        return inputPanel;
    }

    private JPanel createMainContentPanel() {
        JPanel mainContentPanel = new JPanel(new BorderLayout());

        // Add panels to the main content area
        mainContentPanel.add(createJobTablePanel(), BorderLayout.WEST);
        mainContentPanel.add(createExecutionPanel(), BorderLayout.CENTER);
        mainContentPanel.add(createStackPanel(), BorderLayout.EAST);

        return mainContentPanel;
    }

    private JPanel createJobTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder("Job Table"));

        String[] columnNames = {"Name", "Size", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0);
        JTable jobTable = new JTable(tableModel);

        JScrollPane scrollPane = new JScrollPane(jobTable);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        return tablePanel;
    }

    private JPanel createExecutionPanel() {
        JPanel executionPanel = new JPanel();
        executionPanel.setBorder(BorderFactory.createTitledBorder("Execution Details"));
        executionPanel.setLayout(new BoxLayout(executionPanel, BoxLayout.Y_AXIS));

        JLabel executionLabel = new JLabel("Execution Panel: Add custom execution logic here");
        executionPanel.add(executionLabel);

        return executionPanel;
    }

    private JPanel createStackPanel() {
        JPanel stackPanel = new JPanel();
        stackPanel.setBorder(BorderFactory.createTitledBorder("Stack Visualization"));
        stackPanel.setLayout(new BoxLayout(stackPanel, BoxLayout.Y_AXIS));
        stackPanel.setPreferredSize(new Dimension(400, 0)); // Increased width of the stack panel

        String[][] blockData = {
                {"50", "Allocated", "75"},
                {"30", "Free", "33"},
                {"20", "Full", "100"},
                {"40", "Free", "0"}
        };

        for (String[] data : blockData) {
            String blockSize = data[0];
            String blockStatus = data[1];
            int fillPercentage = Integer.parseInt(data[2]);

            // Create a container panel for each block
            JPanel blockContainer = new JPanel();
            blockContainer.setLayout(new BorderLayout());
            blockContainer.setPreferredSize(new Dimension(350, 50)); // Width: 350px, Height: 50px (static height)
            blockContainer.setMaximumSize(new Dimension(350, 50));

            // Create size label (left side)
            JLabel sizeLabel = new JLabel("Size: " + blockSize, SwingConstants.LEFT);
            sizeLabel.setPreferredSize(new Dimension(70, 50)); // Fixed width for size label
            sizeLabel.setHorizontalAlignment(SwingConstants.LEFT);

            // Create block panel (center) using JLayeredPane for layering
            JLayeredPane block = new JLayeredPane();
            block.setPreferredSize(new Dimension(200, 50));
            block.setBorder(BorderFactory.createLineBorder(Color.BLACK)); // Add a border for visibility

            // Add inner padding container
            JPanel paddingPanel = new JPanel();
            paddingPanel.setLayout(null); // Absolute positioning for children
            paddingPanel.setBounds(5, 5, 200, 40); // Leave a 5px padding on all sides
            paddingPanel.setBackground(Color.LIGHT_GRAY); // Optional: Background color for visibility

            // Filled portion
            JPanel filledPanel = new JPanel();
            filledPanel.setBackground(Color.GREEN); // Color for the filled portion
            filledPanel.setBounds(0, 0, (int) (200 * (fillPercentage / 100.0)), 40); // Adjust width based on inner padding
            paddingPanel.add(filledPanel);

            // Percentage label on top of the block
            JLabel fillLabel = new JLabel(fillPercentage + "%", SwingConstants.CENTER);
            fillLabel.setBounds(0, 0, 200, 50); // Centered inside the padding panel
            fillLabel.setHorizontalAlignment(SwingConstants.CENTER);
            fillLabel.setVerticalAlignment(SwingConstants.CENTER);
            filledPanel.add(fillLabel, Integer.valueOf(1)); // Add fill label to filled panel

            block.add(paddingPanel, Integer.valueOf(0)); // Add padding panel to block

            // Create status label (right side)
            JLabel statusLabel = new JLabel(blockStatus, SwingConstants.RIGHT);
            statusLabel.setPreferredSize(new Dimension(70, 50)); // Fixed width for status label
            statusLabel.setHorizontalAlignment(SwingConstants.LEFT);


            if (statusLabel.getText().equals("Allocated")) {
                statusLabel.setForeground(Color.BLUE);
            } else if (statusLabel.getText().equals("Free")) {
                statusLabel.setForeground(Color.GREEN);
            } else if (statusLabel.getText().equals("Full")) {
                statusLabel.setForeground(Color.RED);
            }

            // Add components to the container
            blockContainer.add(sizeLabel, BorderLayout.WEST);
            blockContainer.add(block, BorderLayout.CENTER);
            blockContainer.add(statusLabel, BorderLayout.EAST);

            // Add spacing between blocks
            stackPanel.add(Box.createVerticalStrut(10));
            stackPanel.add(blockContainer);
        }

        return stackPanel;
    }







    private void addJobToTable(String name, String size, String status) {
        tableModel.addRow(new Object[]{name, size, status});
    }

    public void loadGui(MainServer mainServer) {
        this.mainServer = mainServer;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SimulatorGUI::new);
    }
}
