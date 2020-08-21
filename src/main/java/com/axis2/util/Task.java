package com.axis2.util;

import com.axis2.impl.NsnServiceImpl;
import org.apache.log4j.Logger;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.Executors.newSingleThreadScheduledExecutor;

/**
* Description 定时任务类
* @param
* @Author junwei
* @Date 16:16 2020/8/21
**/
public class Task {


    private String time="23";
    private Boolean timeFlag=true;

    private static final Logger log = Logger.getLogger(NsnServiceImpl.class.getClass());

//    public static void main(String[] args) {
//        log.info("开启定时器-时间："+time+"小时");
//        Task task=new Task();
//        task.startTask();
//    }

    public void startTask(){
        log.info("开启定时器-时间："+time+"小时");

        // ScheduledExecutorService:是从Java SE5的java.util.concurrent里，
        // 做为并发工具类被引进的，这是最理想的定时任务实现方式。
        ScheduledExecutorService service = newSingleThreadScheduledExecutor();
        // 第二个参数为首次执行的延时时间，第三个参数为定时执行的间隔时间
        // 10：秒   5：秒
        // 第一次执行的时间为10秒，然后每隔600秒执行一次
        service.scheduleAtFixedRate(runnable, 10, 600, TimeUnit.SECONDS);
    }


    /**
     * Runnable：实现了Runnable接口，jdk就知道这个类是一个线程
     */
    Runnable runnable = new Runnable() {
        //创建 run 方法
        @Override
        public void run() {
            SimpleDateFormat sf = new SimpleDateFormat("HH");
//            SimpleDateFormat sf = new SimpleDateFormat("mm");
            String hh = sf.format(new Date());
                if (time.equals(hh) && timeFlag) {
                    log.info("晚上"+hh+"点，开始调用预分析重传机制");
                    PeriodTask.re_post_ordernum();
                    timeFlag=false;
                }else{
                    if(!time.equals(hh)){
                        timeFlag=true;
                    }
                }
        }
    };




}
