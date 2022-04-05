package scheduler;

import requests.ElevatorMonitor;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import java.awt.*;

/**
 * ElevatorView presents the status information of an Elevator.
 *
 * @author Liam Tripp
 */
public class ElevatorView {

    private JPanel elevatorPanel;
    private JPanel statusPanelContainer;
    private JPanel[] statusPanels;
    private JTextPane[] statusPanes;
    private final static int NUMBER_OF_STATUS_PANES = 8;

    /**
     * Constructor for ElevatorView.
     *
     * @param elevatorNumber the number of the Elevator that this View corresponds to
     */
    public ElevatorView(int elevatorNumber) {
        this.statusPanelContainer = new JPanel(new GridLayout(1, NUMBER_OF_STATUS_PANES));
        this.statusPanels = new JPanel[NUMBER_OF_STATUS_PANES];
        this.statusPanes = new JTextPane[NUMBER_OF_STATUS_PANES];

        // TODO: elevatorPanel contains the statusPanelContainer, also has the border and title
        //    (distinguishing from statusPanelContainer allows more features to be added later)
        //  StatusPanelContainer contains the statusPanels
        //  StatusPanels contain a JTextPane and an information JLabel
        //  We want direct access to each JTextPane to let us update them directly
        elevatorPanel = new JPanel();
        elevatorPanel.add(statusPanelContainer);

        // GUI stuff
        // https://docs.oracle.com/javase/tutorial/uiswing/components/border.html
        // give elevatorPanel a title reading "Elevator X" on the top left
        Border border = BorderFactory.createLineBorder(Color.BLACK);
        TitledBorder titledBorder = BorderFactory.createTitledBorder(border, "Elevator " + elevatorNumber);
        titledBorder.setTitleJustification(TitledBorder.DEFAULT_JUSTIFICATION);
        titledBorder.setTitlePosition(TitledBorder.ABOVE_TOP);
        elevatorPanel.setBorder(titledBorder);
    }

    /**
     * Returns the JPanel of ElevatorView.
     *
     * @return the ElevatorView's JPanel
     */
    public JPanel getPanel() {
        return elevatorPanel;
    }

    /**
     * Returns the ElevatorView's array of statusPanes. Used for testing
     * updates to ElevatorView.
     *
     * @return a JTextPane array containing information about the Elevator's status
     */
    public JTextPane[] getStatusPanes() {
        return statusPanes;
    }

    /**
     * Updates the ElevatorView with information from an ElevatorMonitor.
     *
     * @param elevatorMonitor contains the status information of an Elevator
     */
    public void update(ElevatorMonitor elevatorMonitor) {
        // TODO: update each of the statusPanes
        elevatorPanel.repaint();
        elevatorPanel.revalidate();
    }
}