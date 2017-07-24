package pap.exam.rxJavaEx;

import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.text.NumberFormat;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import rx.Observable;
import rx.Subscription;

public class MonitorWindow {
	private JFrame frame;
	private JButton start = new JButton("Start");
	private JButton stop = new JButton("Stop");;
	private NumberFormat amountFormat = NumberFormat.getNumberInstance();
	private JFormattedTextField alarmThreashold = new JFormattedTextField(amountFormat);
	private JFormattedTextField alarmTimeout = new JFormattedTextField(amountFormat);
	private JFormattedTextField currentValue = new JFormattedTextField(amountFormat);
	private JFormattedTextField minimumValue = new JFormattedTextField(amountFormat);
	private JFormattedTextField maximumValue = new JFormattedTextField(amountFormat);
	private Subscription currentSub, minimumSub, maximumSub;
	private JLabel warning = new JLabel("Temperature is too high!");

	public MonitorWindow() {
		SwingUtilities.invokeLater(() -> {
			frame = new JFrame("Monitor sensors");
			frame.setSize(1000, 700);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			JPanel panel = new JPanel();
			panel.setBounds(61, 11, 81, 140);
			panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
			frame.add(panel);
			JLabel threasholdLabel = new JLabel("Alarm threashold");
			alarmThreashold.setMaximumSize(new Dimension(200, 25));
			alarmThreashold.setColumns(10);
			alarmThreashold.setText(Double.toString(Timer.DEFAULT_THREASHOLD));
			JLabel timeoutLabel = new JLabel("Alarm timeout (ms)");
			alarmTimeout.setMaximumSize(new Dimension(200, 25));
			alarmTimeout.setColumns(10);
			alarmTimeout.setText(Double.toString(Timer.DEFAULT_TIMEOUT_MS));
			JLabel currentValueLabel = new JLabel("Current value");
			currentValue.setEditable(false);
			currentValue.setMaximumSize(new Dimension(200, 25));
			minimumValue.setEditable(false);
			minimumValue.setMaximumSize(new Dimension(200, 25));
			maximumValue.setEditable(false);
			maximumValue.setMaximumSize(new Dimension(200, 25));
			panel.add(threasholdLabel);
			panel.add(alarmThreashold);
			panel.add(timeoutLabel);
			panel.add(alarmTimeout);
			panel.add(currentValueLabel);
			panel.add(currentValue);
			panel.add(new JLabel("Minimum temperature"));
			panel.add(minimumValue);
			panel.add(new JLabel("Maximum temperature"));
			panel.add(maximumValue);
			panel.add(start);
			panel.add(stop);
			panel.add(warning);
			warning.setVisible(false);
			// Setting the frame visibility to true
			frame.setVisible(true);
			setListeners();
		});
	}

	public synchronized void observeAverage(Observable<Double> latestAverage) {
		currentSub = latestAverage.subscribe((v) -> {
			SwingUtilities.invokeLater(() -> {
				currentValue.setText(String.format("%.2f", v));
			});
		});
	}

	public synchronized void observeMinimum(Observable<Double> minimumAverage) {
		minimumSub = minimumAverage.subscribe((v) -> {
			SwingUtilities.invokeLater(() -> {
				minimumValue.setText(String.format("%.2f", v));
			});
		});
	}

	public synchronized void observeMaximum(Observable<Double> maximumAverage) {
		maximumSub = maximumAverage.subscribe((v) -> {
			SwingUtilities.invokeLater(() -> {
				maximumValue.setText(String.format("%.2f", v));
			});
		});
	}

	private void setListeners() {
		start.addActionListener(a -> {
			ExamRxJava.startListening();
		});
		stop.addActionListener(a -> {
			ExamRxJava.stopListening();
		});
	}

	public synchronized void stopOservingAverage() {
		if (currentSub != null) {
			currentSub.unsubscribe();
		}
	}

	public synchronized void stopOservingMinimum() {
		if (minimumSub != null) {
			minimumSub.unsubscribe();
		}

	}

	public synchronized void stopOservingMaximum() {
		if (maximumSub != null) {
			maximumSub.unsubscribe();
		}
	}

	/**
	 * Links start and stop buttons with timer
	 * @param timer
	 */
	public void setTimer(Timer timer) {
		alarmThreashold.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent arg0) {
			}

			@Override
			public void keyReleased(KeyEvent arg0) {
				try {
					timer.changedThreashold(Integer.parseInt(alarmThreashold.getText()));
				} catch (Exception e) {
					synchronized(System.out) {
						System.out.println("Error threashold format");
					}
				}
			}

			@Override
			public void keyTyped(KeyEvent arg0) {
			}

		});
		alarmTimeout.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent arg0) {
			}

			@Override
			public void keyReleased(KeyEvent arg0) {
				try {
					timer.changedTimeout(Integer.parseInt(alarmTimeout.getText()));
				} catch (Exception e) {
					synchronized(System.out) {
						System.out.println("Error timeout format");
					}
				}
			}

			@Override
			public void keyTyped(KeyEvent arg0) {
			}

		});
	}

	public void displayWarning(boolean bool) {
		SwingUtilities.invokeLater(() -> {
			warning.setVisible(bool);
		});
	}
}
