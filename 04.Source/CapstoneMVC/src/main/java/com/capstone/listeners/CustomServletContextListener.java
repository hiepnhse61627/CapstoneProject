package com.capstone.listeners;

import com.capstone.controllers.ScheduleList;
import com.capstone.entities.RealSemesterEntity;
import com.capstone.models.Global;
import com.capstone.models.Ultilities;
import com.capstone.services.IRealSemesterService;
import com.capstone.services.RealSemesterServiceImpl;

import javax.mail.Session;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class CustomServletContextListener implements ServletContextListener {
    private ScheduledExecutorService scheduler;
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
//        IRealSemesterService service = new RealSemesterServiceImpl();
//        List<RealSemesterEntity> list = service.getAllSemester();
//        List<RealSemesterEntity> sortedList = Ultilities.SortSemesters(list);

        List<RealSemesterEntity> sortedList =  Global.getSortedList();
       List<RealSemesterEntity> activeList = sortedList.stream().filter(q -> q.isActive() == true)
               .collect(Collectors.toList());
        RealSemesterEntity current = activeList.get(activeList.size() - 1);
        Global.setCurrentSemester(current);
        Global.setTemporarySemester(current);
        System.out.println("Current Semester is " + current.getSemester());

        scheduler = Executors.newSingleThreadScheduledExecutor();

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                System.out.println("Start syncing...");
                ScheduleList syncFapChangedScheduleService = new ScheduleList();
                syncFapChangedScheduleService.syncChangedScheduleFapImpl();

            }
        };

        scheduler.scheduleAtFixedRate(runnable, 0, 3, TimeUnit.HOURS);
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        System.out.println("destroyed!");
    }
}
