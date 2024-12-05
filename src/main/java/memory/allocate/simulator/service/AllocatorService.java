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
    private static int progress;

    public static void allocateJob(List<JobModel> jobs, List<BlockModel> blocks,
                                   DefaultTableModel tableModel, JPanel executionPanel, JPanel stackPanel) throws InterruptedException {
        jobModelList = jobs;
        blockModelList = blocks;

        if (jobModelList.isEmpty() || blockModelList.isEmpty()) {
            setExecutionMessage(executionPanel, "No jobs or blocks available for allocation.");
            return;
        }

        new Thread(() -> {
            try {
                int currentBlockIndex = 0; // Start with the first block
                for (JobModel job : jobModelList) {
                    if (!job.isDone()) {
                        boolean allocated = false;

                        // Iterate through blocks, starting from the current index
                        for (int i = 0; i < blockModelList.size(); i++) {
                            BlockModel block = blockModelList.get(currentBlockIndex);

                            // Check if the job fits into the block
                            if (!block.isFull() && (block.getSize() - block.getUsedSize() >= job.getSize())) {
                                block.setUsedSize(block.getUsedSize() + job.getSize()); // Update block size

                                List<Integer> newProcessIdList = block.getProcessIds();
                                newProcessIdList.add(job.getJobId());
                                block.setProcessIds(newProcessIdList);

                                job.setBlockId(block.getBlockId()); // Assign block ID to job
                                job.setDone(true); // Mark job as done
                                setExecutionMessage(executionPanel, job.getJobName() + " allocated to " + block.getBlockId());

                                // Check if the block is now full
                                if (block.getSize() - block.getUsedSize() == 0) {
                                    block.setFull(true);
                                    setExecutionMessage(executionPanel, block.getBlockId() + " is now full.");
                                }

                                updateProgressBar(stackPanel, block); // Update progress bar
                                allocated = true;
                                break;
                            }

                            // Move to the next block (circular iteration)
                            currentBlockIndex = (currentBlockIndex + 1) % blockModelList.size();
                        }

                        if (!allocated) {
                            setExecutionMessage(executionPanel, "Unable to allocate " + job.getJobName());
                        }
                    }
                }
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }).start();
    }

    private static synchronized void updateProgressBar(JPanel stackPanel, BlockModel blockModel) throws InterruptedException {

        if (stackPanel != null) {
            for (Component component : stackPanel.getComponents()) {
                if (component instanceof JPanel) {
                    for (Component component2 : ((JPanel) component).getComponents()) {
                        if (component2 instanceof JPanel) {
                            for (Component component3 : ((JPanel) component2).getComponents()) {
                                if (component3 instanceof JProgressBar) {
                                    // Update progress bar value
                                    JProgressBar progressBar = (JProgressBar) component3;
                                    if (!progressBar.getName().isEmpty()  && progressBar.getName().equals(String.valueOf(blockModel.getBlockId()))) {
                                        id = progressBar.getName();
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
                        if (component2 instanceof JLabel) {
                            // Update label status
                            JLabel statusLabel = (JLabel) component2;
                            if (statusLabel.getName() != null && !statusLabel.getName().isEmpty()  && statusLabel.getName().equals(id)) {

                                if (progress == 0) {
                                    statusLabel.setText("   Empty");
                                    statusLabel.setForeground(Color.BLUE);
                                } else if (progress < 100) {
                                    statusLabel.setText("   Allocated");
                                    statusLabel.setForeground(new Color(0, 190, 0)); // Dark green
                                } else if (progress == 100) {
                                    statusLabel.setText("   Full");
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

    private static void setExecutionMessage(JPanel executionPanel, String result) {
        SwingUtilities.invokeLater(() -> {
            JLabel executionLabel = new JLabel(result);
            executionPanel.add(executionLabel);
            executionPanel.revalidate();
            executionPanel.repaint();
        });
    }
}

