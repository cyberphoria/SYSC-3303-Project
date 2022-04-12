package elevatorsystem;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * FaultButton is the button class used to inject door stuck faults.
 *
 * @author Julian
 */
public class FaultButton implements ActionListener {

    //For each elevator
    private JPanel faultPanel;
    private JToggleButton doorFaultButton;
    private Elevator elevator;
    private boolean malfunction;

    public FaultButton(Elevator elevator) {
        this.elevator = elevator;
        this.malfunction = false;
        int elevatorNumber = elevator.getElevatorNumber();
        faultPanel = new JPanel();
        doorFaultButton = new JToggleButton("Door Fault");

        faultPanel.add(doorFaultButton);
        doorFaultButton.addActionListener(this);

        Border border = BorderFactory.createLineBorder(Color.BLACK);
        TitledBorder titledBorder = BorderFactory.createTitledBorder(border, "Elevator " + elevatorNumber);
        titledBorder.setTitleJustification(TitledBorder.CENTER);
        titledBorder.setTitlePosition(TitledBorder.ABOVE_TOP);
        faultPanel.setBorder(titledBorder);
    }

    /**
     * Returns the JPanel of ElevatorView.
     *
     * @return the ElevatorView's JPanel
     */
    public JPanel getPanel() {
        return faultPanel;
    }

    //Overriding actionPerformed() method
    @Override
    public void actionPerformed(ActionEvent e) {
        malfunction = !malfunction;
        elevator.setDoorsMalfunctioning(malfunction);
    }
}
