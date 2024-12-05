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

    public SimulatorGUI() {
        initialize();
    }

    private void initialize() {
        setTitle("Memory Allocation Simulator using NEXT-FIT Algorithm | Developed By Shaleel Sandeepa");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 600);
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
                int size = Integer.parseInt(jobSize); // Validate that jobSize is numeric
                addJobToTable(new JobModel(jobId, size, jobName)); // Pass the size as an integer
                jobNameField.setText("");
                jobSizeField.setText("");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(inputPanel, "Job Size must be a valid number.", "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Inside createInputPanel() method
        JButton resetButton = new JButton("Reset");
        resetButton.addActionListener(e -> {
            // Step 1: Clear the job list and block list
            if (jobs != null) {
                jobs.clear();
            }
            if (blocks != null) {
                for (BlockModel block : blocks) {
                    block.setUsedSize(0);
                    block.setFull(false); // Reset block to empty
                }
            }

            // Step 2: Clear the job table
            tableModel.setRowCount(0);

            // Step 3: Reset execution panel
            if (executionPanel != null) {
                for (Component component : executionPanel.getComponents()) {
                    if (component instanceof JLabel) {
                        executionPanel.remove(component); // Remove JLabel components
                    }
                }
                executionPanel.revalidate();
                executionPanel.repaint();
            }

            // Step 4: Reset stack panel (remove old components and recreate)
            stackPanel.removeAll();
            createStackPanel(); // Recreate the stack panel with default blocks

            // Step 5: Reset job name and size fields
            jobNameField.setText("");
            jobSizeField.setText("");

            // Step 6: Remove the current main content panel
            remove(mainContentPanel);  // Remove the existing content pane

            // Step 7: Recreate and add the main content panel again
            mainContentPanel = createMainContentPanel();
            add(mainContentPanel, BorderLayout.CENTER);

            // Ensure the GUI is updated after resetting
            revalidate();
            repaint();
        });




        JButton exampleJobsButton = new JButton("Example Jobs");
        exampleJobsButton.addActionListener(e -> {
            if (jobs.size() == 0) {
                addJobToTable(new JobModel(jobId, 64, "job 1"));
                addJobToTable(new JobModel(jobId, 64, "job 2"));
                addJobToTable(new JobModel(jobId, 12, "job 3"));
                addJobToTable(new JobModel(jobId, 1024, "job 4"));
                addJobToTable(new JobModel(jobId, 56, "job 5"));
                addJobToTable(new JobModel(jobId, 256, "job 6"));

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

        String[] columnNames = {"Name", "Size", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0);
        JTable jobTable = new JTable(tableModel);

        JScrollPane scrollPane = new JScrollPane(jobTable);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        return tablePanel;
    }

    private JPanel createExecutionPanel() {
        executionPanel = new JPanel();
        executionPanel.setBorder(BorderFactory.createTitledBorder("Execution"));
        executionPanel.setLayout(new BoxLayout(executionPanel, BoxLayout.Y_AXIS));

        JButton executeButton = new JButton("Execute All Jobs");
        executeButton.addActionListener(e -> {
            try {
                AllocatorService.allocateJob(jobs, blocks, tableModel, executionPanel, stackPanel);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        });

        executionPanel.add(executeButton);

        return executionPanel;
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
            blockContainer.setMaximumSize(new Dimension(350, 50));

            // Create size label (left side)
            JLabel sizeLabel = new JLabel("Size: " + blockSize, SwingConstants.LEFT);
            sizeLabel.setPreferredSize(new Dimension(70, 50));
            sizeLabel.setHorizontalAlignment(SwingConstants.LEFT);

            // Create block panel (center) using JLayeredPane for layering
            JPanel block = new JPanel();
            block.setLayout(new BorderLayout());
            block.setPreferredSize(new Dimension(200, 50));

            // Set the text color of the percentage to black
            UIManager.put("ProgressBar.selectionForeground", Color.BLACK);
            UIManager.put("ProgressBar.selectionBackground", Color.BLACK);

            // Create a JProgressBar
            JProgressBar progressBar = new JProgressBar(0, 100);
            progressBar.setName(String.valueOf(blockModel.getBlockId()));
            progressBar.setValue(fillPercentage);
            progressBar.setStringPainted(true);
            progressBar.setForeground(Color.BLACK);
            progressBar.setBackground(Color.LIGHT_GRAY);
            progressBar.setForeground(GREEN);
            block.add(progressBar, BorderLayout.CENTER);

            // Create status label (right side)
            statusLabel = new JLabel("   "+blockStatus, SwingConstants.RIGHT);
            statusLabel.setName(String.valueOf(blockModel.getBlockId()));
            statusLabel.setPreferredSize(new Dimension(70, 50));
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
        tableModel.addRow(new Object[]{jobModel.getJobName(), jobModel.getSize(), "Pending", jobModel.getJobId()});
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
