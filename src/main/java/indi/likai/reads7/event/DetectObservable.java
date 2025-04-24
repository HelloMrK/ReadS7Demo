package indi.likai.reads7.event;

import lombok.Data;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

@Data
public class DetectObservable {

    private String text;


    /**
     * 下面的都是触发器配置.不用管.
     */
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

    public void setText(String newText) {
        String oldText = this.text;
        this.text = newText;
        pcs.firePropertyChange("text", oldText, newText);
    }
}
