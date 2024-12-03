package memory.allocate.simulator;

import memory.allocate.simulator.gui.SimulatorGUI;

public class MainServer {
    public static void main(String[] args) {
        try {
            System.out.println("Simulator Started");
            MainServer mainServer = new MainServer();

            mainServer.loadGUI();

            System.out.println("Simulator Running...");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadGUI() {
        SimulatorGUI simulatorGUI = new SimulatorGUI();
        simulatorGUI.loadGui(this);
    }
}