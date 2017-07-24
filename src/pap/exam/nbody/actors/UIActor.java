package pap.exam.nbody.actors;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import akka.actor.UntypedActor;
import javafx.util.Pair;
import pap.exam.nbody.Planet;
import pap.exam.nbody.actors.MessageForUI.Type;

public class UIActor extends UntypedActor {

	private List<Planet> planets = new ArrayList<Planet>();
	private JFrame frame;
	private JPanel panel;
	private JButton start;
	private JButton pause;

	public void preStart() {
		SwingUtilities.invokeLater(() -> {
			frame = new JFrame("N-body simulation");
			frame.setSize(1000, 700);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setLayout(new FlowLayout());
			frame.setBackground(Color.WHITE);
			panel = new JPanel() {
				private static final long serialVersionUID = -3165937941760852184L;

				public void paintComponent(Graphics g) {
					super.paintComponent(g);
					g.setColor(Color.BLUE);
					Graphics2D g2d = (Graphics2D) g;
					for (Planet p : planets) {
						Pair<Float, Float> position = p.getPosition();
						double diameter = 10;// Math.sqrt(p.getMass()/Math.PI)/10000000000L;
						Ellipse2D.Double circle = new Ellipse2D.Double(position.getKey() / 1e9,
								position.getValue() / 1e9, diameter, diameter);
						g2d.fill(circle);
					}
					synchronized (System.out) {
						System.out.println("Painting");
					}
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
				MessageForController message = new MessageForController(MessageForController.Type.START, null);
				getContext().parent().tell(message, getContext().self());
			});
			pause.addActionListener((e) -> {
				MessageForController message = new MessageForController(MessageForController.Type.PAUSE, null);
				getContext().parent().tell(message, getContext().self());
			});
		});
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onReceive(Object arg0) throws Exception {
		MessageForUI message = (MessageForUI) arg0;
		Type type = message.getType();
		if (type.equals(MessageForUI.Type.RENDER)) {
			SwingUtilities.invokeLater(() -> {
				planets = (List<Planet>) message.getExtra();
				frame.repaint();
			});
		}
	}

}
