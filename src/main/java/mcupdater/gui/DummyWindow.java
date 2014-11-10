package mcupdater.gui;

/**
 * Dummy window in case of headless servers
 */
public class DummyWindow implements UpdateWindow {

    @Override
    public void setString(String text) {
    }

    @Override
    public void setMaximum(int n) {
    }

    @Override
    public void release() {
    }

    @Override
    public void setCurrentTask(String newString, boolean increment) {
    }

    @Override
    public void setVisible(boolean b) {
    }

    @Override
    public void dispose() {
    }

}
