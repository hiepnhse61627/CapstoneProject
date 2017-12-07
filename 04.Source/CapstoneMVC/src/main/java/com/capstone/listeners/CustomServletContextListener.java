package com.capstone.listeners;

import com.capstone.entities.RealSemesterEntity;
import com.capstone.models.Global;
import com.capstone.models.Ultilities;
import com.capstone.services.IRealSemesterService;
import com.capstone.services.RealSemesterServiceImpl;

import javax.mail.Session;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.List;

public class CustomServletContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
//        IRealSemesterService service = new RealSemesterServiceImpl();
//        List<RealSemesterEntity> list = service.getAllSemester();
//        List<RealSemesterEntity> sortedList = Ultilities.SortSemesters(list);

        List<RealSemesterEntity> sortedList =  Global.getSortedList();
        RealSemesterEntity current = sortedList.get(sortedList.size() - 1);
        Global.setCurrentSemester(current);
        Global.setTemporarySemester(current);
        System.out.println("Current Semester is " + current.getSemester());
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        System.out.println("destroyed!");
    }
}
