package mcupdater.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.SystemColor;
import java.awt.Toolkit;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import mcupdater.logging.LogHelper;

public class ProgressWindow extends JDialog implements UpdateWindow {

    private static final long serialVersionUID = 6963326800689896105L;
    private static final boolean HEADLESS = GraphicsEnvironment.isHeadless();
    private JProgressBar progressBar;
    private JLabel lblCurrentItem;
    static {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            LogHelper.getLogger().warn("Unable to set the System Look and Feel.", e);
        }
    }

    public static UpdateWindow newWindow() {
        UpdateWindow instance;
        if (!HEADLESS) {
            instance = new ProgressWindow();
            ((JDialog) instance).setDefaultCloseOperation(HIDE_ON_CLOSE);
        } else {
            instance = new DummyWindow();
        }
        return instance;
    }

    /**
     * Create the dialog.
     */
    private ProgressWindow() {
        setBackground(SystemColor.control);
        setResizable(false);
        setTitle("MCUpdater Plus");
        setType(Type.POPUP);
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(screen.width / 3, screen.height / 3, 450, 112);
        getContentPane().setLayout(new BorderLayout());
        {
            JPanel contentPanel = new JPanel();
            contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
            getContentPane().add(contentPanel, BorderLayout.CENTER);
            contentPanel.setLayout(new BorderLayout(0, 0));
            {
                JPanel panel = new JPanel();
                contentPanel.add(panel, BorderLayout.CENTER);
                panel.setLayout(new BorderLayout(0, 0));
                {
                    progressBar = new JProgressBar();
                    panel.add(progressBar, BorderLayout.SOUTH);
                    progressBar.setMaximum(1);
                    progressBar.setIndeterminate(true);
                    progressBar.setStringPainted(true);
                }
                {
                    lblCurrentItem = new JLabel("Current Item");
                    panel.add(lblCurrentItem, BorderLayout.CENTER);
                    lblCurrentItem.setBackground(SystemColor.control);
                    lblCurrentItem.setHorizontalAlignment(SwingConstants.CENTER);
                    lblCurrentItem.setLabelFor(progressBar);
                }
            }
            // Padding
            contentPanel.add(new JPanel(), BorderLayout.WEST);
            contentPanel.add(new JPanel(), BorderLayout.EAST);
            contentPanel.add(new JPanel(), BorderLayout.SOUTH);
            contentPanel.add(new JPanel(), BorderLayout.NORTH);

        }
    }

    public JProgressBar getProgressBar() {
        return progressBar;
    }

    @Override
    public void setCurrentTask(String newString, boolean increment) {
        if (increment)
            progressBar.setValue(progressBar.getValue() + 1);
        setString(newString + String.format(" (%s of %s)", progressBar.getValue() + 1, progressBar.getMaximum()));
    }

    @Override
    public void release() {
        progressBar.setIndeterminate(false);
    }

    @Override
    public void setMaximum(int n) {
        progressBar.setMaximum(n);
    }

    @Override
    public void setString(String text) {
        lblCurrentItem.setText(text);
    }
}
