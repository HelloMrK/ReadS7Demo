package indi.likai.reads7.listener;

import org.springframework.stereotype.Component;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
@Component
// 监听器
public class DetectListener implements PropertyChangeListener {
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        System.out.println("属性变化了: " + evt.getPropertyName() +
                " 旧值: " + evt.getOldValue() +
                " 新值: " + evt.getNewValue());
    }
}