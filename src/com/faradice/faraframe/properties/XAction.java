package com.faradice.faraframe.properties;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 * Action that shows an Exception in a dilog if actionPerformed
 * throws and exception
 * @author ragnar.valdimarsson
 *
 */
public class XAction extends AbstractAction {
	private static final long serialVersionUID = 1L;
	Logger logger = Logger.getLogger(AbstractAction.class.getName());
	private Component parent = null;

	public XAction(String name, Icon icon, String tooltip) {
		this (null, name, icon, tooltip);
	}

	
	public boolean isSeparator() {
		return getValue(Action.NAME).toString().toLowerCase().startsWith("sep");
	}

	public XAction(Component parent, String name, Icon icon, String tooltip) {
		super (name, icon);
		this.putValue(Action.SHORT_DESCRIPTION, tooltip);
		this.parent = parent;
	}

	public void perform(ActionEvent e) throws Exception {
		logger.info("Unimplemented action "+this.getValue(NAME));
	}

	public void actionPerformed(ActionEvent e) {
		try {
			perform(e);
		} catch (Exception ex) {
		   handleException(ex);
		}
	}

	public void handleException(final Exception e) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				logger.log(Level.INFO, e.getMessage(), e);
				String message = e.getMessage();
				if (message == null || message.trim().length() < 1) {
					message = "Unexpected error\n"+e.toString();
				}
				JOptionPane.showMessageDialog(parent, e.getMessage());
			}
		});
	}
}
