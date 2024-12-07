package memory.allocate.simulator.service;

import memory.allocate.simulator.model.BlockModel;
import memory.allocate.simulator.model.JobModel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AllocatorService {

    private static List<JobModel> jobModelList;
    private static List<BlockModel> blockModelList;
    private static String id;
    private static int availableSize;
    private static int progress;
    private static final Color GREEN = new Color(0, 210, 0);
    private static boolean allocated;
    private static int currentBlockIndex = 0; // Start with the first block

    public static void resetCurrentBlockIndex(int index) {
        currentBlockIndex = index;
    }

    public static void allocateJob(List<JobModel> jobs, List<BlockModel> blocks,
                                   DefaultTableModel tableModel, JPanel executionPanel, JPanel stackPanel) throws InterruptedException {
        jobModelList = jobs;
        blockModelList = blocks;

        if (jobModelList.isEmpty()) {
            setExecutionMessage(executionPanel, "No jobs available for allocation.");
            return;
        } else if (blockModelList.isEmpty()) {
            setExecutionMessage(executionPanel, "No blocks available for allocation.");
            return;
        }

        new Thread(() -> {
            try {
                setExecutionMessage(executionPanel, "========== Simulation started ! ==========");
                setExecutionMessage(executionPanel, "<html><br></html>");
                for (JobModel job : jobModelList) {
                    //check job is completed
                    String status = (String) tableModel.getValueAt(job.getJobId()-1, 3);
                    if (status.equals("Completed ✅") || status.equals("Canceled ❌")) {
                        job.setDone(true);
                    }
                    if (!job.isDone()) {
                        allocated = false;

                        // Iterate through blocks, starting from the current index
                        for (int i = 0; i < blockModelList.size(); i++) {
                            BlockModel block = blockModelList.get(currentBlockIndex);
                            setExecutionMessage(executionPanel, "Checking if "+job.getJobName()+" is fits into the Block " + block.getBlockId()+"...");
                            updateProgressBar(stackPanel, block, true); // Update border

                            // Check if the job fits into the block
                            if (!block.isFull() && (block.getSize() - block.getUsedSize() >= job.getSize())) {
                                block.setUsedSize(block.getUsedSize() + job.getSize()); // Update block size

                                List<Integer> newProcessIdList = block.getProcessIds();
                                newProcessIdList.add(job.getJobId());
                                block.setProcessIds(newProcessIdList);

                                job.setBlockId(block.getBlockId()); // Assign block ID to job
                                job.setDone(true); // Mark job as done

                                setExecutionMessage(executionPanel, job.getJobName() + " allocated to Block " + block.getBlockId()+" ✅");

                                // Check if the block is now full
                                if (block.getSize() - block.getUsedSize() == 0) {
                                    block.setFull(true);
                                    setExecutionMessage(executionPanel, "Block "+block.getBlockId() + " is now full.");
                                }
                                setExecutionMessage(executionPanel, "<html><br></html>");
                                updateProgressBar(stackPanel, block, false); // Update progress bar

                                allocated = true;

                                // Update the table model to reflect the job is "Complete"
                                SwingUtilities.invokeLater(() -> {
                                    for (int row = 0; row < tableModel.getRowCount(); row++) {
                                        if ((int) tableModel.getValueAt(row, 0) == job.getJobId()) {
                                            tableModel.setValueAt("Completed ✅", row, 3);
                                            break;
                                        }
                                    }
                                });

                                break;
                            }

                            // Move to the next block (circular iteration)
                            currentBlockIndex = (currentBlockIndex + 1) % blockModelList.size();
                        }

                        if (!allocated) {
                            setExecutionMessage(executionPanel, "Unable to allocate " + job.getJobName()+" ⚠️");
                            setExecutionMessage(executionPanel, "<html><br></html>");
                            // Update the table model to reflect the job is "Complete"
                            SwingUtilities.invokeLater(() -> {
                                for (int row = 0; row < tableModel.getRowCount(); row++) {
                                    if ((int) tableModel.getValueAt(row, 0) == job.getJobId()) {
                                        tableModel.setValueAt("Canceled ❌", row, 3);
                                        break;
                                    }
                                }
                            });
                        }
                    }
                }
                setExecutionMessage(executionPanel, "========== Simulation stopped ! ==========");
                setExecutionMessage(executionPanel, "<html><br></html>");
            } catch (InterruptedException ex) {
                try {
                    setExecutionMessage(executionPanel, "<html><br></html>");
                    setExecutionMessage(executionPanel, "========== Simulation stopped ! ==========");
                    setExecutionMessage(executionPanel, "<html><br></html>");
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                ex.printStackTrace();
            }
        }).start();
    }

    private static synchronized void updateProgressBar(JPanel stackPanel, BlockModel blockModel, boolean isBorder) throws InterruptedException {

        if (stackPanel != null) {
            for (Component component : stackPanel.getComponents()) {
                if (component instanceof JPanel) {
                    for (Component component2 : ((JPanel) component).getComponents()) {
                        if (component2 instanceof JPanel) {
                            for (Component component3 : ((JPanel) component2).getComponents()) {
                                if (component3 instanceof JProgressBar progressBar) {
                                    //update border
                                    if (isBorder) {
                                        if (!progressBar.getName().isEmpty()  && progressBar.getName().equals(String.valueOf(blockModel.getBlockId()))) {
                                            progressBar.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 4));
                                        } else {
                                            progressBar.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
                                        }
                                    }
                                    // Update progress bar value
                                    if (!progressBar.getName().isEmpty()  && progressBar.getName().equals(String.valueOf(blockModel.getBlockId())) && !isBorder) {
                                        id = progressBar.getName();
                                        availableSize = blockModel.getSize() - blockModel.getUsedSize();
                                        progress = (int) ((double) blockModel.getUsedSize() / blockModel.getSize() * 100);
                                        progressBar.setValue(progress);

                                        if (progress == 100) {
                                            progressBar.setForeground(Color.RED);
                                        }
                                        break;
                                    }
                                }
                            }
                        }
                        if (component2 instanceof JLabel statusLabel && !isBorder) {
                            // Update label status
                            if (statusLabel.getName() != null && !statusLabel.getName().isEmpty()  && statusLabel.getName().equals(String.valueOf(blockModel.getBlockId()))) {

                                if (progress == 0) {
                                    statusLabel.setText("   Empty");
                                    statusLabel.setForeground(Color.BLUE);
                                } else if (progress < 100) {
                                    statusLabel.setText("<html>&nbsp;&nbsp;&nbsp;&nbsp;Allocated<br>&nbsp;&nbsp;&nbsp;&nbsp;Jobs: " + blockModel.getProcessIds() +
                                            "<br>&nbsp;&nbsp;&nbsp;&nbsp;[available: " + availableSize + "]</html>");
                                    statusLabel.setForeground(GREEN); // Dark green
                                } else if (progress == 100) {
                                    statusLabel.setText("<html>&nbsp;&nbsp;&nbsp;&nbsp;Full<br>&nbsp;&nbsp;&nbsp;&nbsp;Jobs: "+blockModel.getProcessIds()+"</html>");
                                    statusLabel.setForeground(Color.RED);
                                }
                                break;

                            }
                        }
                    }
                }
            }

            // Refresh the panel
            stackPanel.revalidate(); // Update layout
            stackPanel.repaint();    // Redraw the panel
            Thread.sleep(2000); // Simulate delay for progress update
        }
    }

    private static void setExecutionMessage(JPanel executionPanel, String result) throws InterruptedException {
        SwingUtilities.invokeLater(() -> {
            JLabel executionLabel = new JLabel(result);
            executionPanel.add(executionLabel);
            executionPanel.revalidate();
            executionPanel.repaint();
        });
        Thread.sleep(1000);
    }
}

