package com.capstone.jpa.exJpa;

import com.capstone.entities.*;
import com.capstone.jpa.ScheduleEntityJpaController;
import com.capstone.models.Logger;
import com.capstone.services.DateUtil;

import javax.persistence.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ExScheduleEntityJpaController extends ScheduleEntityJpaController {

    public ExScheduleEntityJpaController(EntityManagerFactory emf) {
        super(emf);
    }

    private int currentLine = 0;
    private int totalLine = 0;

    public int getCurrentLine() {
        return currentLine;
    }

    public int getTotalLine() {
        return totalLine;
    }

    public List<ScheduleEntity> findAllSchedules() {
        EntityManager em = getEntityManager();
        TypedQuery<ScheduleEntity> query = em.createQuery("SELECT a FROM ScheduleEntity a WHERE (a.isActive IS NULL OR a.isActive = 'true')", ScheduleEntity.class);
        return query.getResultList();
    }

    public void saveSchedule(ScheduleEntity Schedule) {
        try {
            EntityManager manager = getEntityManager();
            manager.getTransaction().begin();
            manager.merge(Schedule);
            manager.flush();
            manager.getTransaction().commit();
        } catch (Exception e) {
            Logger.writeLog(e);
            e.printStackTrace();
        }
    }

    public void createScheduleList(List<ScheduleEntity> Schedules) {
        totalLine = Schedules.size();
        currentLine = 0;

        EntityManager em = null;
        try {
            em = getEntityManager();
            for (ScheduleEntity Schedule : Schedules) {
                try {
                    em.getTransaction().begin();

                    // Create Schedule
                    TypedQuery<ScheduleEntity> querySchedule = em.createQuery(
                            "SELECT c FROM ScheduleEntity c " +
                                    "WHERE (c.dateId = :date)" +
                                    "AND (c.groupName= :groupName) AND (c.roomId= :room)", ScheduleEntity.class);
                    querySchedule.setParameter("date", Schedule.getDateId());
                    querySchedule.setParameter("groupName", Schedule.getGroupName());
                    querySchedule.setParameter("room", Schedule.getRoomId());

                    List<ScheduleEntity> std = querySchedule.getResultList();
                    if (std.isEmpty()) {
                        em.persist(Schedule);
                        em.flush();
                    } else {
                        System.out.println(std);
                    }
                    em.getTransaction().commit();
                } catch (Exception e) {
                    System.out.println("Schedule " + Schedule.getId() + "caused " + e.getMessage());
                    e.printStackTrace();
                }

                currentLine++;

                System.out.println(currentLine + "-" + totalLine);
            }
        } finally {
            if (em != null) {
                em.close();
            }
        }

        totalLine = 0;
        currentLine = 0;
    }

    public void updateSchedule(ScheduleEntity model) {
        EntityManager em = getEntityManager();

        try {
//            ScheduleEntity scheduleEntity = this.findScheduleEntity(model.getId());
//            scheduleEntity.(model.getClass1());
//            scheduleEntity = model;
            em.getTransaction().begin();
            em.merge(model);
            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ScheduleEntity findScheduleByDateSlotAndRoom(DaySlotEntity date, RoomEntity room) {
        EntityManager em = getEntityManager();
        ScheduleEntity ScheduleEntity = new ScheduleEntity();
        try {
            String sqlString = "SELECT c FROM ScheduleEntity c " +
                    "WHERE (c.dateId = :date)" +
                    "AND (c.roomId= :room) AND (c.isActive IS NULL OR c.isActive = 'true')";
            Query query = em.createQuery(sqlString);
            query.setParameter("date", date);
            query.setParameter("room", room);

            ScheduleEntity = (ScheduleEntity) query.getSingleResult();

            return ScheduleEntity;
        } catch (NoResultException nrEx) {
            return null;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public ScheduleEntity findScheduleByDateSlotAndLecture(DaySlotEntity date, EmployeeEntity lecture) {
        EntityManager em = getEntityManager();
        ScheduleEntity ScheduleEntity = new ScheduleEntity();
        try {
            String sqlString = "SELECT c FROM ScheduleEntity c " +
                    "WHERE (c.dateId = :date)" +
                    "AND (c.empId= :lecture) AND (c.isActive IS NULL OR c.isActive = 'true')";
            Query query = em.createQuery(sqlString);
            query.setParameter("date", date);
            query.setParameter("lecture", lecture);


            ScheduleEntity = (ScheduleEntity) query.getSingleResult();

            return ScheduleEntity;
        } catch (NoResultException nrEx) {
            return null;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public ScheduleEntity findScheduleByDateSlotAndLectureAndCourseDontCareIsActive(DaySlotEntity date, EmployeeEntity lecture, CourseEntity course) {
        EntityManager em = getEntityManager();
        ScheduleEntity ScheduleEntity = new ScheduleEntity();
        Query query = null;
        try {
            String sqlString = "SELECT c FROM ScheduleEntity c " +
                    "WHERE (c.dateId = :date)" +
                    "AND (c.empId= :lecture)" +
                    "AND (c.courseId= :course)";
            query = em.createQuery(sqlString);
            query.setParameter("date", date);
            query.setParameter("lecture", lecture);
            query.setParameter("course", course);


            ScheduleEntity = (ScheduleEntity) query.getSingleResult();

            return ScheduleEntity;
        } catch (Exception nrEx) {
            return null;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public ScheduleEntity findScheduleByDateSlotAndLectureAndRoomAndCourse(DaySlotEntity date, EmployeeEntity lecture, RoomEntity room, CourseEntity course) {
        EntityManager em = getEntityManager();
        ScheduleEntity ScheduleEntity = new ScheduleEntity();
        String sqlString = "SELECT c FROM ScheduleEntity c " +
                "WHERE (c.dateId = :date) " +
                "AND (c.empId= :lecture) AND (c.roomId= :room) AND (c.courseId= :course) AND (c.isActive IS NULL OR c.isActive = 'true')";
        Query query = em.createQuery(sqlString);
        query.setParameter("date", date);
        query.setParameter("lecture", lecture);
        query.setParameter("room", room);
        query.setParameter("course", course);
        try {

            List<ScheduleEntity> aList = query.getResultList();

            if (aList.size() > 1) {
//                System.out.println("");
                aList.get(0).setActive(false);
                saveSchedule(aList.get(0));

                ScheduleEntity = aList.get(1);
            }else{
                ScheduleEntity = (ScheduleEntity) query.getSingleResult();
            }

            return ScheduleEntity;
        } catch (NoResultException nrEx) {
            return null;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public ScheduleEntity findScheduleByDateSlotAndLectureAndRoomAndCourseDontCareIsActive(DaySlotEntity date, EmployeeEntity lecture, RoomEntity room, CourseEntity course) {
        EntityManager em = getEntityManager();
        ScheduleEntity ScheduleEntity = new ScheduleEntity();
        try {
            String sqlString = "SELECT c FROM ScheduleEntity c " +
                    "WHERE (c.dateId = :date) " +
                    "AND (c.empId= :lecture) AND (c.roomId= :room) AND (c.courseId= :course)  AND (c.isActive = 'false')";
            Query query = em.createQuery(sqlString);
            query.setParameter("date", date);
            query.setParameter("lecture", lecture);
            query.setParameter("room", room);
            query.setParameter("course", course);

            List<ScheduleEntity> aList = query.getResultList();

            if (aList.size() > 1) {
                aList.get(0).setActive(false);
                saveSchedule(aList.get(0));

                ScheduleEntity = aList.get(1);
            }else{
                ScheduleEntity = (ScheduleEntity) query.getSingleResult();
            }


            return ScheduleEntity;
        } catch (NoResultException nrEx) {
            return null;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }


    public ScheduleEntity createSchedule(ScheduleEntity ScheduleEntity) {
        EntityManager em = null;

        try {
            em = getEntityManager();

            em.getTransaction().begin();
            em.persist(ScheduleEntity);
            em.flush();
            em.getTransaction().commit();
        } finally {
            em.close();
        }

        return ScheduleEntity;
    }

    public List<ScheduleEntity> findScheduleByDateSlot(DaySlotEntity dateSlot) {
        EntityManager em = getEntityManager();
        try {
            String sqlString = "SELECT c FROM ScheduleEntity c " +
                    "WHERE (c.dateId = :dateSlot) AND (c.isActive IS NULL OR c.isActive = 'true')";
            Query query = em.createQuery(sqlString);
            query.setParameter("dateSlot", dateSlot);

            List<ScheduleEntity> std = query.getResultList();

            return std;
        } catch (NoResultException nrEx) {
            return null;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<ScheduleEntity> findScheduleByGroupName(String groupName) {
        EntityManager em = getEntityManager();
        try {
            String sqlString = "SELECT c FROM ScheduleEntity c " +
                    "WHERE (c.groupName = :groupName) AND (c.isActive IS NULL OR c.isActive = 'true')";
            Query query = em.createQuery(sqlString);
            query.setParameter("groupName", groupName);

            List<ScheduleEntity> std = query.getResultList();

            return std;
        } catch (NoResultException nrEx) {
            return null;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<ScheduleEntity> findScheduleByLectureHaveParentSchedule(Integer lecture) {
        EntityManager em = getEntityManager();
        try {
            if (lecture != null) {
                String sqlString = "SELECT c FROM ScheduleEntity c " +
                        "WHERE (c.parentScheduleId IS NOT NULL)";
                Query query = em.createQuery(sqlString);

                List<ScheduleEntity> std = query.getResultList();
                List<ScheduleEntity> result = new ArrayList<>();

                for (ScheduleEntity aSchedule : std) {
                    ScheduleEntity parentSchedule = findScheduleEntity(aSchedule.getParentScheduleId());
                    if (parentSchedule != null) {
                        if (parentSchedule.getEmpId() != null) {
                            if (parentSchedule.getEmpId().getId() == lecture) {
                                result.add(aSchedule);
                            }
                        }
                    }

                }

                return result;
            } else {
                String sqlString = "SELECT c FROM ScheduleEntity c " +
                        "WHERE (c.parentScheduleId IS NOT NULL)";
                Query query = em.createQuery(sqlString);

                List<ScheduleEntity> std = query.getResultList();

                return std;
            }

        } catch (NoResultException nrEx) {
            return null;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }


    public List<ScheduleEntity> findScheduleInRange(Integer lecture, int iDisplayStart, int iDisplayLength) {
        EntityManager em = getEntityManager();
        try {
            if (lecture != null) {
                String sqlString = "SELECT c FROM ScheduleEntity c " +
                        "WHERE (c.isActive IS NULL) OR (c.isActive = 'true')";
                Query query = em.createQuery(sqlString);
                query.setFirstResult(iDisplayStart);
                query.setMaxResults(iDisplayLength);
                List<ScheduleEntity> std = query.getResultList();
                List<ScheduleEntity> result = new ArrayList<>();

                for (ScheduleEntity aSchedule : std) {
                    ScheduleEntity parentSchedule = findScheduleEntity(aSchedule.getParentScheduleId());
//                    if (parentSchedule != null) {
                    if (aSchedule.getEmpId() != null) {
                        if (aSchedule.getEmpId().getId() == lecture) {
                            result.add(aSchedule);
                        }
//                        }
                    }

                }

                return result;
            } else {
                String sqlString = "SELECT c FROM ScheduleEntity c " +
                        "WHERE (c.isActive IS NULL) OR (c.isActive = 'true')";
                Query query = em.createQuery(sqlString);
                query.setFirstResult(iDisplayStart);
                query.setMaxResults(iDisplayLength);
                List<ScheduleEntity> std = query.getResultList();

                return std;
            }

        } catch (NoResultException nrEx) {
            return null;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }


    public List<ScheduleEntity> findScheduleByLecture(Integer lecture) {
        EntityManager em = getEntityManager();
        List<ScheduleEntity> std = null;
        try {
            if (lecture != null) {
                String sqlString = "SELECT c FROM ScheduleEntity c " +
                        "WHERE (c.empId.id = :lecture)";
                Query query = em.createQuery(sqlString);
                query.setParameter("lecture", lecture);

                std = query.getResultList();

            }
            return std;

        } catch (NoResultException nrEx) {
            return null;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }


    public List<ScheduleEntity> findScheduleBySubjectCodeAndGroupNameBeforeNowTime(String subjectCode, String groupName) {
        EntityManager em = getEntityManager();
        List<ScheduleEntity> std = null;
        Date now = new Date();
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String nowStr = format.format(now);
        try {
            String sqlString = "SELECT * FROM Schedule s" +
                    " INNER JOIN  Day_Slot d ON s.DateId=d.Id " +
                    " INNER JOIN Course c ON s.CourseId=c.Id " +
                    "WHERE (s.isActive IS NULL OR s.isActive = 'true') " +
                    "AND c.SubjectCode LIKE '" + subjectCode + "' " +
                    "AND s.GroupName LIKE '%" + groupName + "%' " +
                    "AND CONVERT(nvarchar(50), CONVERT(SMALLDATETIME, d.Date, 105), 23) <='" + nowStr + "'";
            Query query = em.createNativeQuery(sqlString, ScheduleEntity.class);
            std = query.getResultList();


            return std;

        } catch (NoResultException nrEx) {
            return null;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }


    public List<ScheduleEntity> findScheduleByGroupnameAndCourseAndLecture(CourseEntity course, String groupname, EmployeeEntity emp) {
        EntityManager em = getEntityManager();
        try {
            String sqlString = "SELECT c FROM ScheduleEntity c " +
                    "WHERE (c.courseId = :course) AND (c.groupName = :groupname) AND (c.empId = :emp) AND (c.isActive IS NULL OR c.isActive = 'true')";
            Query query = em.createQuery(sqlString);
            query.setParameter("course", course);
            query.setParameter("groupname", groupname);
            query.setParameter("emp", emp);

            List<ScheduleEntity> std = query.getResultList();
            return std;

        } catch (Exception nrEx) {
            return null;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }


    public ScheduleEntity findScheduleByDateSlotAndGroupName(DaySlotEntity dateSlot, String groupName) {
        EntityManager em = getEntityManager();
        ScheduleEntity ScheduleEntity = new ScheduleEntity();
        try {
            String sqlString = "SELECT c FROM ScheduleEntity c " +
                    "WHERE (c.dateId = :date) " +
                    "AND (c.groupName= :groupName) AND (c.isActive IS NULL OR c.isActive = 'true')";
            Query query = em.createQuery(sqlString);
            query.setParameter("date", dateSlot);
            query.setParameter("groupName", groupName);

            ScheduleEntity = (ScheduleEntity) query.getSingleResult();

            return ScheduleEntity;
        } catch (NoResultException nrEx) {
            return null;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<ScheduleEntity> findScheduleByGroupNameAndCourse(String groupName, CourseEntity course) {
        EntityManager em = getEntityManager();
        try {
            String sqlString = "SELECT c FROM ScheduleEntity c " +
                    "WHERE (c.courseId = :course) AND (c.groupName = :groupname) AND (c.isActive IS NULL OR c.isActive = 'true')";
            Query query = em.createQuery(sqlString);
            query.setParameter("course", course);
            query.setParameter("groupname", groupName);

            List<ScheduleEntity> std = query.getResultList();
            return std;

        } catch (Exception nrEx) {
            return null;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

}
