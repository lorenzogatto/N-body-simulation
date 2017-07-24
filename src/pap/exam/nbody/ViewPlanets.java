package pap.exam.nbody;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import javax.swing.*;
import javafx.util.Pair;
import pap.exam.nbody.NBodyExecutors.Events;

public class ViewPlanets extends Observable {

	volatile List<Planet> planets = new ArrayList<Planet>();
	private JFrame frame;
	private JPanel panel;
	private JButton start;
	private JButton pause;

	/**
	 * Draw a window with a canvas and two buttons
	 */
	public ViewPlanets() {
		frame = new JFrame("N-body simulation");
		frame.setSize(1200, 700);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new FlowLayout());
		frame.setBackground(Color.WHITE);
		panel = new JPanel() {
			private static final long serialVersionUID = -7839659773493319680L;

			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.setColor(Color.BLUE);
				Graphics2D g2d = (Graphics2D) g;
				for (Planet p : planets) {
					Pair<Float, Float> position = p.getPosition();
					double diameter = 10;// Math.sqrt(p.getMass()/Math.PI)/10000000000L;
					Ellipse2D.Double circle = new Ellipse2D.Double(position.getKey() / 1e9, position.getValue() / 1e9,
							diameter, diameter);
					g2d.fill(circle);
				}
				System.out.println("Painting");
			}
		};
		panel.setPreferredSize(new Dimension(700, 700));
		panel.setBackground(Color.LIGHT_GRAY);
		start = new JButton("Start");
		pause = new JButton("Pause");
		frame.add(panel);
		frame.getContentPane().add(start);
		frame.getContentPane().add(pause);

		frame.setVisible(true);

		start.addActionListener((e) -> {
			ViewPlanets.this.setChanged();
			notifyObservers(Events.START_BUTTON);
		});
		pause.addActionListener((e) -> {
			ViewPlanets.this.setChanged();
			notifyObservers(Events.PAUSE_BUTTON);
		});
	}

	/**
	 * Change the list of planets that should be drawn from that point in time
	 * @param planets
	 */
	public void setPlanets(List<Planet> planets) {
		this.planets = planets;
	}
	/**
	 * Draw the last list of planets submitted with the setPlanets method
	 */
	public void repaint() {
		SwingUtilities.invokeLater(() -> {
			frame.repaint();
		});
	}
}
