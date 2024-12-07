package memory.allocate.simulator.gui;

import memory.allocate.simulator.MainServer;
import memory.allocate.simulator.model.BlockModel;
import memory.allocate.simulator.model.JobModel;
import memory.allocate.simulator.service.AllocatorService;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class SimulatorGUI extends JFrame {

    private MainServer mainServer;
    int jobId = 1;
    Color GREEN = new Color(0, 210, 0);
    List<JobModel> jobs = new ArrayList<>();
    List<BlockModel> blocks = new ArrayList<>();

    // GUI Components
    private JTextField jobNameField;
    private JTextField jobSizeField;
    private DefaultTableModel tableModel;

    JPanel executionPanel;
    JPanel stackPanel;
    JPanel mainContentPanel;
    JLabel statusLabel;
    JTable jobTable;

    public SimulatorGUI() {
        initialize();
    }

    private void initialize() {
        setTitle("Memory Allocation Simulator for NEXT-FIT Algorithm | Developed By Shaleel Sandeepa");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 600);
        setLocationRelativeTo(null);
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
        jobNameField = new JTextField(10);

        JLabel jobSizeLabel = new JLabel("Job Size:");
        jobSizeField = new JTextField(10);

        JButton insertButton = new JButton("Insert Job");
        insertButton.addActionListener(e -> {
            String jobName = jobNameField.getText().trim();
            String jobSize = jobSizeField.getText().trim();

            if (jobName.isEmpty() || jobSize.isEmpty()) {
                JOptionPane.showMessageDialog(inputPanel, "Please fill out both fields.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                int size = Integer.parseInt(jobSize);
                addJobToTable(new JobModel(jobId, size, jobName));
                jobNameField.setText("");
                jobSizeField.setText("");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(inputPanel, "Job Size must be a valid number.", "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Inside createInputPanel() method
        JButton resetButton = new JButton("Reset");
        resetButton.addActionListener(e -> {

            jobId = 1;
            jobNameField.setText("");
            jobSizeField.setText("");
            jobs.clear();
            tableModel.setRowCount(0);
            stackPanel.removeAll();
            blocks.clear();
            AllocatorService.resetCurrentBlockIndex(0);

            // Reinitialize blocks
            blocks.add(new BlockModel(1, 128));
            blocks.add(new BlockModel(2, 128));
            blocks.add(new BlockModel(3, 512));
            blocks.add(new BlockModel(4, 256));
            blocks.add(new BlockModel(5, 1024));
            blocks.add(new BlockModel(6, 256));
            blocks.add(new BlockModel(7, 128));

            // Recreate the stack panel UI
            for (BlockModel blockModel : blocks) {
                int blockSize = blockModel.getSize();

                // Create a container panel for each block
                JPanel blockContainer = new JPanel();
                blockContainer.setLayout(new BorderLayout());
                blockContainer.setPreferredSize(new Dimension(350, 50));
                blockContainer.setMaximumSize(new Dimension(400, 50));

                // Create size label (left side)
                JLabel sizeLabel = new JLabel("Size: " + blockSize, SwingConstants.LEFT);
                sizeLabel.setPreferredSize(new Dimension(70, 50));
                sizeLabel.setHorizontalAlignment(SwingConstants.LEFT);

                // Create block panel (center)
                JPanel block = new JPanel();
                block.setLayout(new BorderLayout());
                block.setPreferredSize(new Dimension(250, 50));
                block.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));

                // Set the text color of the percentage to black
                UIManager.put("ProgressBar.selectionForeground", Color.BLACK);
                UIManager.put("ProgressBar.selectionBackground", Color.BLACK);

                // Create a JProgressBar
                JProgressBar progressBar = new JProgressBar(0, 100);
                progressBar.setName(String.valueOf(blockModel.getBlockId()));
                progressBar.setValue(0);
                progressBar.setStringPainted(true);
                progressBar.setBackground(Color.WHITE);
                progressBar.setForeground(Color.GREEN);
                progressBar.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
                block.add(progressBar, BorderLayout.CENTER);

                // Create status label (right side)
                JLabel statusLabel = new JLabel("   Empty", SwingConstants.RIGHT);
                statusLabel.setName(String.valueOf(blockModel.getBlockId()));
                statusLabel.setPreferredSize(new Dimension(120, 100));
                statusLabel.setHorizontalAlignment(SwingConstants.LEFT);
                statusLabel.setForeground(Color.BLUE);

                // Add components to the container
                blockContainer.add(sizeLabel, BorderLayout.WEST);
                blockContainer.add(block, BorderLayout.CENTER);
                blockContainer.add(statusLabel, BorderLayout.EAST);

                // Add spacing between blocks
                stackPanel.add(Box.createVerticalStrut(10));
                stackPanel.add(blockContainer);
            }

            // Repaint stack panel
            stackPanel.revalidate();
            stackPanel.repaint();

            // Reset execution panel
            executionPanel.removeAll();
            executionPanel.revalidate();
            executionPanel.repaint();

            JOptionPane.showMessageDialog(this, "UI has been reset to initial state.", "Reset", JOptionPane.INFORMATION_MESSAGE);
        });


        JButton exampleJobsButton = new JButton("Example Jobs");
        exampleJobsButton.addActionListener(e -> {
            if (jobs.size() == 0) {
                addJobToTable(new JobModel(jobId, 64, "job 1"));
                addJobToTable(new JobModel(jobId, 100, "job 2"));
                addJobToTable(new JobModel(jobId, 12, "job 3"));
                addJobToTable(new JobModel(jobId, 1024, "job 4"));
                addJobToTable(new JobModel(jobId, 56, "job 5"));
                addJobToTable(new JobModel(jobId, 256, "job 6"));
                addJobToTable(new JobModel(jobId, 420, "job 7"));
                addJobToTable(new JobModel(jobId, 128, "job 8"));
                addJobToTable(new JobModel(jobId, 128, "job 9"));
                addJobToTable(new JobModel(jobId, 100, "job 10"));

            } else {
                JOptionPane.showMessageDialog(inputPanel, "Already added example jobs.", "Alert", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        // Layout for input panel
        GroupLayout layout = new GroupLayout(inputPanel);
        inputPanel.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        // Horizontal group
        layout.setHorizontalGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                        .addComponent(jobNameLabel)
                        .addComponent(jobSizeLabel))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(jobNameField)
                        .addComponent(jobSizeField)
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(insertButton)
                                .addComponent(exampleJobsButton)
                                .addComponent(resetButton))
                )
        );

        // Vertical group
        layout.setVerticalGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(jobNameLabel)
                        .addComponent(jobNameField))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(jobSizeLabel)
                        .addComponent(jobSizeField))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(insertButton)
                        .addComponent(exampleJobsButton)
                        .addComponent(resetButton))
        );

        return inputPanel;
    }


    private JPanel createMainContentPanel() {
        mainContentPanel = new JPanel(new BorderLayout());

        // Add panels to the main content area
        mainContentPanel.add(createJobTablePanel(), BorderLayout.WEST);
        mainContentPanel.add(createExecutionPanel(), BorderLayout.CENTER);
        mainContentPanel.add(createStackPanel(), BorderLayout.EAST);

        return mainContentPanel;
    }

    private JPanel createJobTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder("Job Table"));

        String[] columnNames = {"Job Id", "Name", "Size", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0);
        jobTable = new JTable(tableModel);

        JScrollPane scrollPane = new JScrollPane(jobTable);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        return tablePanel;
    }

    private JPanel createExecutionPanel() {
        // Outer panel with its own BoxLayout
        JPanel executionPanelOuter = new JPanel();
        executionPanelOuter.setBorder(BorderFactory.createTitledBorder("Execution"));
        executionPanelOuter.setLayout(new BorderLayout()); // Separate BoxLayout

        // Inner panel for scrollable content
        executionPanel = new JPanel();
        executionPanel.setLayout(new BoxLayout(executionPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(executionPanel);

        // Execute button
        JButton executeButton = new JButton("Execute Selected Job");
        executeButton.addActionListener(e -> {
            int selectedRow = jobTable.getSelectedRow(); // Get the selected row index
            if (selectedRow == -1) {
                // No row selected
                JOptionPane.showMessageDialog(this, "Please select a job to execute.", "Selection Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                // Retrieve job details from the selected row
                int jobId = (int) tableModel.getValueAt(selectedRow, 0);
                String jobName = (String) tableModel.getValueAt(selectedRow, 1);
                int jobSize = (int) tableModel.getValueAt(selectedRow, 2);

                // Create a new JobModel and add it to a list
                List<JobModel> selectedJobList = new ArrayList<>();
                selectedJobList.add(new JobModel(jobId, jobSize, jobName));

                // Pass the selected job list to the allocation method
                AllocatorService.allocateJob(selectedJobList, blocks, tableModel, executionPanel, stackPanel);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "An error occurred while executing the job.", "Execution Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton executeAllButton = new JButton("Execute All Jobs");
        executeAllButton.addActionListener(e -> {
            try {
                AllocatorService.allocateJob(jobs, blocks, tableModel, executionPanel, stackPanel);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        });

        // Button panel to organize buttons
        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.add(executeButton, BorderLayout.WEST);
        buttonPanel.add(executeAllButton, BorderLayout.EAST);

        // Add button panel and scroll pane to outer panel
        executionPanelOuter.add(buttonPanel, BorderLayout.NORTH);
        executionPanelOuter.add(scrollPane, BorderLayout.CENTER);

        return executionPanelOuter;
    }


    private JPanel createStackPanel() {
        stackPanel = new JPanel();
        stackPanel.setBorder(BorderFactory.createTitledBorder("Stack Visualization"));
        stackPanel.setLayout(new BoxLayout(stackPanel, BoxLayout.Y_AXIS));
        stackPanel.setPreferredSize(new Dimension(400, 0));

        blocks.add(new BlockModel(1, 128));
        blocks.add(new BlockModel(2, 128));
        blocks.add(new BlockModel(3, 512));
        blocks.add(new BlockModel(4, 256));
        blocks.add(new BlockModel(5, 1024));
        blocks.add(new BlockModel(6, 256));
        blocks.add(new BlockModel(7, 128));

        for (BlockModel blockModel : blocks) {
            int blockSize = blockModel.getSize();
            boolean isFull = blockModel.isFull();
            String blockStatus = "Empty";
            if (isFull) {
                blockStatus = "Full";
            } else if (!(blockModel.getUsedSize() == 0)) {
                blockStatus = "Allocated";
            }

            int fillPercentage = (blockModel.getUsedSize()/blockSize)*100;

            // Create a container panel for each block
            JPanel blockContainer = new JPanel();
            blockContainer.setLayout(new BorderLayout());
            blockContainer.setPreferredSize(new Dimension(350, 50));
            blockContainer.setMaximumSize(new Dimension(400, 50));

            // Create size label (left side)
            JLabel sizeLabel = new JLabel("Size: " + blockSize, SwingConstants.LEFT);
            sizeLabel.setPreferredSize(new Dimension(70, 50));
            sizeLabel.setHorizontalAlignment(SwingConstants.LEFT);

            // Create block panel (center) using JLayeredPane for layering
            JPanel block = new JPanel();
            block.setLayout(new BorderLayout());
            block.setPreferredSize(new Dimension(250, 50));
            block.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));

            // Set the text color of the percentage to black
            UIManager.put("ProgressBar.selectionForeground", Color.BLACK);
            UIManager.put("ProgressBar.selectionBackground", Color.BLACK);

            // Create a JProgressBar
            JProgressBar progressBar = new JProgressBar(0, 100);
            progressBar.setName(String.valueOf(blockModel.getBlockId()));
            progressBar.setValue(fillPercentage);
            progressBar.setStringPainted(true);
            progressBar.setBackground(Color.WHITE);
            progressBar.setForeground(GREEN);
            progressBar.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
            block.add(progressBar, BorderLayout.CENTER);

            // Create status label (right side)
            statusLabel = new JLabel("   "+blockStatus, SwingConstants.RIGHT);
            statusLabel.setName(String.valueOf(blockModel.getBlockId()));
            statusLabel.setPreferredSize(new Dimension(120, 100));
            statusLabel.setHorizontalAlignment(SwingConstants.LEFT);

            // Add components to the container
            blockContainer.add(sizeLabel, BorderLayout.WEST);
            blockContainer.add(block, BorderLayout.CENTER);
            blockContainer.add(statusLabel, BorderLayout.EAST);

            // Add spacing between blocks
            stackPanel.add(Box.createVerticalStrut(10));
            stackPanel.add(blockContainer);

            // logic
            if (blockStatus.equals("Allocated")) {
                statusLabel.setForeground(GREEN); // Dark green
            } else if (blockStatus.equals("Empty")) {
                statusLabel.setForeground(Color.BLUE);
            } else if (blockStatus.equals("Full")) {
                statusLabel.setForeground(Color.RED);
                progressBar.setForeground(Color.RED);
            }
        }

        return stackPanel;
    }







    private void addJobToTable(JobModel jobModel) {
        tableModel.addRow(new Object[]{jobModel.getJobId(), jobModel.getJobName(), jobModel.getSize(), "Pending..."});
        jobs.add(jobModel);
        jobId++;
    }

    public void loadGui(MainServer mainServer) {
        this.mainServer = mainServer;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SimulatorGUI::new);
    }
}
