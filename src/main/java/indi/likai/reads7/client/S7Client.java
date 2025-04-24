package indi.likai.reads7.client;

import cn.hutool.core.thread.ThreadUtil;
import com.github.xingshuangs.iot.protocol.s7.enums.EPlcType;
import com.github.xingshuangs.iot.protocol.s7.serializer.S7Serializer;
import com.github.xingshuangs.iot.protocol.s7.service.S7PLC;
import indi.likai.reads7.config.PLCConfig;
import indi.likai.reads7.dto.DetectBean;
import indi.likai.reads7.dto.HeartBeatsBean;
import indi.likai.reads7.event.DetectObservable;
import indi.likai.reads7.listener.DetectListener;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
/**
 * S7连接客户端
 */
@Component
public class S7Client {
    @Resource
    private ScheduledExecutorService executorService;
    @Resource
    private PLCConfig plcConfig;

    //TODO PLC状态 这个要进redis缓存.可以增加上次plc离线时间,上次plc上线时间,累计离线次数.具体相关明细
    private Boolean plcStatus=false;

    private S7Serializer s7Serializer;

    private S7PLC s7PLC;

    private S7Serializer getS7Serializer(){
        if (s7Serializer==null){
            s7Serializer = S7Serializer.newInstance(s7PLC);
        }
        return s7Serializer;
    }




    /**
     * 初始化设备信息
     */
    @PostConstruct
    public void initial() {
        executorService = new ScheduledThreadPoolExecutor(1);
        s7PLC = new S7PLC(EPlcType.S1200,
                plcConfig.getHost(),
                plcConfig.getPort(),
                plcConfig.getRack(),
                plcConfig.getSlot());
        //TODO上线 将状态更新至redis
        //持续开始心跳检测
        new Thread(()->{
            try{
                heartBeats();
            }
            catch (Exception e){
                //TODO 上线失败,将状态更新至redis并启用重试 / 告警
            }
        }).start();
    }

    //DetectEvent监听对象
    DetectObservable detectObservable = new DetectObservable();
    /**
     * 循环读数
     */
    @PostConstruct
    public void startReadStatus() {
        //初始化监听对象.
        detectObservable.addPropertyChangeListener(new DetectListener());
        //TODO 此处可以初始化计数器.每个方法的执行次数,执行时间,执行结果条数之类的,汇总到一个map存到redis方便排查问题
        new Thread(() -> {
            readBeanModeWhile();
        }).start();
    }


    /**
     * 具体读数方法.
     */
    private void readBeanModeWhile(){
        while(true){
            //因为整个方法不能停止,所以全局try包裹.
            try{
                //获取在线状态.如果离线则等五秒再读
                if (!plcStatus){
                    ThreadUtil.safeSleep(5000L);
                    continue;
                }
                //使用异步观察者模式
                //监听对象
                DetectBean detectBean=(DetectBean)read(DetectBean.class);
                detectObservable.setText(detectBean.getUid());//瞎几把写的.这里如果变化了的话,就会触发listener(异步的)

                //第二个业务
//                DetectBean detectBean=(DetectBean)read(DetectBean.class);
//                detectObservable.setText(detectBean.getUid());//瞎几把写的.这里如果变化了的话,就会触发listener(异步的)

                //第三个业务
//                DetectBean detectBean=(DetectBean)read(DetectBean.class);
//                detectObservable.setText(detectBean.getUid());//瞎几把写的.这里如果变化了的话,就会触发listener(异步的)

            }catch (Exception e){
                //出错逻辑

            }

            ThreadUtil.safeSleep(1);//CPUsleep1毫秒.
        }
    }


    /**
     * 心跳检测
     */
    private void heartBeats(){
        executorService.scheduleWithFixedDelay(() -> {
            //TODO 处理redis相关逻辑
            try{
                getS7Serializer().read(HeartBeatsBean.class);
                //TODO 读取成功,在线逻辑
                //从离线编程在线:连接成功
                if (!plcStatus){
                    //log.info("PLC连接成功")
                }
                plcStatus=true;

            }catch (Exception e){
                //TODO 读取失败,离线逻辑.
                plcStatus=false;
                //log.error("PLC连接掉线...");
            }
        //500毫秒一次.
        }, 0, 500, TimeUnit.MILLISECONDS);
    }

    private Object read(Class<?> s7BeanClass) {
        Object o=new Object();
        try {
            o=getS7Serializer().read(s7BeanClass);
        }catch (Exception e){
            //TODO 如果读取出错 则视为PLC离线
            plcStatus=false;
            //log.error("PLC连接掉线...");
        }
        return o;
    }

    private void write(Object value) {
        try {
            getS7Serializer().write(value);
        }catch (Exception e){
            //TODO 如果写入出错 则视为PLC离线
            plcStatus=false;
            //log.error("PLC连接掉线...");
        }
    }

}
