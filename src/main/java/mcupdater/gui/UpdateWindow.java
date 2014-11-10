package mcupdater.gui;

public interface UpdateWindow {

    void setString(String text);

    void setMaximum(int n);

    void release();

    void setCurrentTask(String newString, boolean increment);

    void setVisible(boolean b);

    void dispose();

}
