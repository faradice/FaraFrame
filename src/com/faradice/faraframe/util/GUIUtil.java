package com.faradice.faraframe.util;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.Window;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class GUIUtil {
    /**
     * Get the application root frame
     * @return the root frame
     */
    public static Frame getRootFrame() {
        return JOptionPane.getRootFrame();
    }
    
    /**
     * Root frame is the application's frame.
     * This frame is used as a default parent frame if null
     * is sent as parent component to mehtods that display dialogs.
     * @param frame The application frame
     */
    public static void setRootFrame(Frame frame) {
        JOptionPane.setRootFrame(frame);
    }

    /**
     * Find the parent frame of the specified container.
     * @param container The container to find the frame for.
     * @return The parent frame
     */
    public static Frame getParentFrame(Container container) {
        while ((container != null) && !(container instanceof Frame)) {
            container = container.getParent();
        }
        return container != null ? (Frame) container : getRootFrame();
    }
    
    /**
     * Find the parent window of the specified container.
     * @param container The container to find the frame for.
     * @return The parent frame
     */
    public static Window getParentWindow(Container container) {
        while ((container != null) && !(container instanceof Window)) {
            container = container.getParent();
        }
        return container != null ? (Window)container : getRootFrame();
    }

    /**
     * Find the parent frame of the specified container.
     * @param container The container to find the frame for.
     * @return The parent frame
     */
    public static JFrame getParentJFrame(Container container) {
        while ((container != null) && !(container instanceof JFrame)) {
            container = container.getParent();
        }
        return container != null ? (JFrame) container : (JFrame)getRootFrame();
    }

    /**
     * Find the parent dialog of the specified container.
     * @param container The container to find the dialog for.
     * @return The parent dialog.
     */
    public static JDialog getParentDialog(Container container) {
        while (!(container instanceof JDialog) && (container != null)) {
            container = container.getParent();
        }
        return (JDialog) container;
    }
    

    /**
     * Center the specified container with in the provided comparision container.
     * @param container The container to center
     * @param ocomparison The parent to center the container with in.
     */
    public static void centerWindow(Container container, Container ocomparison) {
        Dimension dimParentSize = ocomparison.getSize();
        Dimension dimSize = container.getSize();
        Point oPoint = ocomparison.getLocationOnScreen();
        int nX = oPoint.x + (dimParentSize.width / 2 - dimSize.width / 2);
        int nY = oPoint.y + (dimParentSize.height / 2 - dimSize.height / 2);
        container.setLocation(nX, nY);
    }

    /**
     * Center the specfied container on the primary display
     * @param container The container to center
     */
    public static void centerWindow(Container container) {
        centerWindow(container, true);
    }
    
    /**
     * CenterWindow over screen or founderframe
     * If screen width and height indicateds use of x2 monitors then center one left or lower screen.
     *
     * @param container         the container to be centered
     * @param centerOverFounder false for center over screen, true for centering over container
     *                          parent window.
     */
    public static void centerWindow(Container container, boolean centerOverFounder) {
        Dimension frameSize = container.getSize();
        Container parent = getParentFrame(container);
        Dimension size = parent != null ? parent.getSize() : null;
        
        if (centerOverFounder && parent != container && size != null && size.width > 0 && size.height > 0) {
            if (frameSize.height > size.height) frameSize.height = size.height;
            if (frameSize.width > size.width) frameSize.width = size.width;
            assert parent != null;
            Point point = parent.getLocation();
            container.setLocation(((size.width - frameSize.width) / 2) + point.x, ((size.height - frameSize.height) / 2) + point.y);
        } else {
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            if (frameSize.height > screenSize.height) {
                frameSize.height = screenSize.height;
            }
            if (frameSize.width > screenSize.width) {
                frameSize.width = screenSize.width;
            }
            // if screen width and height indicates use of x2 monitors then center one left or lower screen.
            if (screenSize.width / (float) screenSize.height > 2.5) {
                container.setLocation((screenSize.width / 2 - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
            } else if (screenSize.width / (float) screenSize.height < 1) {
                container.setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height / 2 - frameSize.height) / 2);
            } else {
                container.setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
            }
        }
    } // centerWindow()

    /**
     * Center the provided component on the primary display
     * @param component The component to center
     */
    public static void centerComponent(Component component) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = component.getSize();
        if (frameSize.height > screenSize.height) frameSize.height = screenSize.height;
        if (frameSize.width > screenSize.width) frameSize.width = screenSize.width;
        Point pt = new Point((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
        SwingUtilities.convertPointFromScreen(pt, component);
        component.setLocation(pt.x, pt.y);
    }
    

}
