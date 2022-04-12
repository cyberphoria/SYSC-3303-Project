package elevatorsystem;

import java.util.ArrayList;
import javax.swing.*;

/**
 *  FaultInjector is the GUI that helps to inject Door Stuck faults using a push button
 * 
 * @author Julian
 * */
public class FaultInjector {

	//For elevator subsystem
    private ArrayList<FaultButton> faultButtons;
    private JPanel buttonListPanel;
    private JPanel containerPanel;
	
	public FaultInjector(ArrayList<Elevator> elevatorList) {
        int numberOfElevators = elevatorList.size();
        //Initializing the buttons
		this.faultButtons = new ArrayList<>();
        buttonListPanel = new JPanel();
        for (int i = 0; i < numberOfElevators; i++) {
            faultButtons.add(new FaultButton(elevatorList.get(i)));
            buttonListPanel.add(faultButtons.get(i).getPanel());
        }

        JScrollPane scrollPane = new JScrollPane(buttonListPanel);
        scrollPane.setBorder(BorderFactory.createEtchedBorder());
        containerPanel = new JPanel();
        containerPanel.add(scrollPane);
        
        JFrame frame = new JFrame("Elevator Simulation");
        frame.add(containerPanel);
        frame.pack();
        frame.setVisible(true);
	}
}
