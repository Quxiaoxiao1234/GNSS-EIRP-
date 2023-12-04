package com.qiuwuyu.algorithm;

import com.qiuwuyu.entity.Sat;
import com.qiuwuyu.entity.Window;
import com.qiuwuyu.util.Access;
import org.joda.time.DateTime;

import java.util.List;

/**
 * @author paralog
 * @date 2023/2/7 19:30
 */
public class NoMInTimeAlgo {

    public static List<Sat> sates;


    private static long currentTime = new DateTime(2022, 9, 22, 8, 00, 00).getMillis();
    private static DateTime currentDateTime = new DateTime(2022, 9, 8, 04, 00, 00);
    private static long endTime = new DateTime(2022, 10, 14, 04, 00, 00).getMillis();
    private static long stepTime = 1000;

    //为了方便Matlab作图
    public static void executeByTimeForPicture(String filepath) {
        int size = 1;
        try {
            sates = Access.StringToObject(filepath);
            sates = svnToPrn(sates);
            size = sates.size();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        long oriTime = new DateTime(2022, 9, 22, 8, 00, 00).getMillis();
        long baseUnit = 1000 * 60 * 60;
        int duringMin = 0;
        System.out.println("********************************************");
        System.out.println("Scheduling starts");
        while (currentTime <= endTime) {
            Sat targetSat = getTargetSat();
            if (targetSat == null) {
                if (sates.size() == 0) {
                    break;
                } else {
                    while (currentTime <= endTime && targetSat == null) {
                        targetSat = getTargetSat();
                        currentTime += stepTime;
                        currentDateTime.plusSeconds(1);
                    }
                }
            }
            if(targetSat == null){
                break;
            }
            //targetWindow表示当前窗口正在被执行
            Window targetWindow = targetSat.getWindows().get(targetSat.getNowIdx());
            double start = (double) (currentTime - oriTime) / (double) baseUnit;
            double end = (double) (targetWindow.getEnd().getMillis() - oriTime) / (double) baseUnit;
            duringMin += (targetWindow.getEnd().getMillis() - currentTime) / (1000 * 60);
            System.out.println(targetSat.getId() + ",start: " + start + ",end: " + end + ",during :" + (targetWindow.getEnd().getMillis() - currentTime) / (1000 * 60));
            currentDateTime = targetWindow.getEnd();
            currentTime = targetWindow.getEnd().getMillis();
        }
        System.out.println("runningTime: " + duringMin + " , averageRunningTime: " + duringMin / (double) size);
        System.out.println("********************************************");
        System.out.println("Scheduling ends");
        System.out.println("end");
    }

    private static Sat getTargetSat() {
        Sat target = null;
        double maxPri = Integer.MIN_VALUE;
        for (int i = 0; i < sates.size(); ++i) {
            Sat tem = sates.get(i);
            if (tem.isVisible()) {
                continue;
            }
            double pri = getPriorityByHRRN(tem);
            if (pri > maxPri) {
                maxPri = pri;
                target = tem;
            }
        }
        if (target != null) {
            target.setVisible(true);
            sates.remove(target);
        }
        if (maxPri == Integer.MIN_VALUE) {
            return null;
        } else {
            return target;
        }
    }

    //利用高响应比作为优先级
    private static double getPriorityByHRRN(Sat sat) {
        List<Window> lists = sat.getWindows();
        double res = Integer.MIN_VALUE;
        for (int i = 0; i < lists.size(); ++i) {
            Window window = lists.get(i);
            long start = window.getStart().getMillis();
            long end = window.getEnd().getMillis();
            double during = end - currentTime;
            double witting = currentTime - start;
            if (start <= currentTime && start + 60 * 1000 >= currentTime) {
                sat.setNowIdx(i);
                res = 1.0 + (witting) / during;
            }
            //有一个不为默认值,证明当前窗口距离当前时间最近,立刻返回
            if (res != Integer.MIN_VALUE) {
                return res;
            }
        }
        return res;
    }

    public static void executeByTimeForPictureByFCFS(String filepath) {
        int size = 1;
        try {
            sates = Access.StringToObject(filepath);
            sates = svnToPrn(sates);
            size = sates.size();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        long oriTime = new DateTime(2022, 9, 22, 8, 00, 00).getMillis();
        long baseUnit = 1000 * 60 * 60;
        int duringMin = 0;
        System.out.println("********************************************");
        System.out.println("Scheduling starts");
        while (currentTime <= endTime) {
            Sat targetSat = getTargetSatByFCFS();
            if (targetSat == null) {
                if (sates.size() == 0) {
                    break;
                } else {
                    while (currentTime <= endTime && targetSat == null) {
                        targetSat = getTargetSatByFCFS();
                        currentTime += stepTime;
                        currentDateTime.plusSeconds(1);
                    }
                }
            }
            if(targetSat == null){
                break;
            }
            //targetWindow表示当前窗口正在被执行
            Window targetWindow = targetSat.getWindows().get(targetSat.getNowIdx());
            double start = (double)(currentTime - oriTime) / (double) baseUnit;
            double end = (double) (targetWindow.getEnd().getMillis() - oriTime) / (double) baseUnit;
            duringMin += (targetWindow.getEnd().getMillis() - currentTime) / (1000 * 60);
            System.out.println(targetSat.getId() + ",start: " + start + ",end: " + end + ",during :" + (targetWindow.getEnd().getMillis() - currentTime) / (1000 * 60));
            currentDateTime = targetWindow.getEnd();
            currentTime = targetWindow.getEnd().getMillis();
        }
        System.out.println("runningTime: " + duringMin + " , averageRunningTime: " + duringMin / (double) size);
        System.out.println("********************************************");
        System.out.println("Scheduling ends");
        System.out.println("end");
    }

    private static Sat getTargetSatByFCFS() {
        Sat target = null;
        double maxPri = Integer.MIN_VALUE;
        for (int i = 0; i < sates.size(); ++i) {
            Sat tem = sates.get(i);
            if (tem.isVisible()) {
                continue;
            }
            double pri = getPriorityByFCFS(tem);
            if (pri > maxPri) {
                maxPri = pri;
                target = tem;
            }
        }
        if (target != null) {
            target.setVisible(true);
            sates.remove(target);
        }
        if (maxPri == Integer.MIN_VALUE) {
            return null;
        } else {
            return target;
        }
    }

    //先来先服务
    private static double getPriorityByFCFS(Sat sat) {
        List<Window> lists = sat.getWindows();
        double res = Integer.MIN_VALUE;
        for (int i = 0; i < lists.size(); ++i) {
            Window window = lists.get(i);
            long start = window.getStart().getMillis();
            double witting = currentTime - start;
            //不考虑等待
            if (start <= currentTime && start + 60 * 1000 >= currentTime ) {
                sat.setNowIdx(i);
                res = (double) 100000000 / (witting + 1);
            }
            //有一个不为默认值,证明当前窗口距离当前时间最近,立刻返回
            if (res != Integer.MIN_VALUE) {
                return res;
            }
        }
        return res;
    }

    public static int executeByTimeForPictureBySJF(String filepath) {
        int size = 1;
        try {
            sates = Access.StringToObject(filepath);
            sates = svnToPrn(sates);
            size = sates.size();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        long oriTime = new DateTime(2022, 9, 22, 8, 00, 00).getMillis();
        long baseUnit = 1000 * 60 * 60;
        int duringMin = 0;
        System.out.println("********************************************");
        System.out.println("Scheduling starts");
        while (currentTime <= endTime) {
            Sat targetSat = getTargetSatBySJF();
            if (targetSat == null) {
                if (sates.size() == 0) {
                    break;
                } else {
                    while (currentTime <= endTime && targetSat == null) {
                        targetSat = getTargetSatBySJF();
                        currentTime += stepTime;
                        currentDateTime.plusSeconds(1);
                    }
                }
            }
            if(targetSat == null){
                break;
            }
            //targetWindow表示当前窗口正在被执行
            Window targetWindow = targetSat.getWindows().get(targetSat.getNowIdx());
//            double start = (double)(currentTime - oriTime) / (double) baseUnit;
            double start = (double)(targetWindow.getStart().getMillis() - oriTime) / (double) baseUnit;
            double end = (double) (targetWindow.getEnd().getMillis() - oriTime) / (double) baseUnit;
            duringMin += (targetWindow.getEnd().getMillis() - currentTime) / (1000 * 60);
            System.out.println(targetSat.getId() + ",start: " + start + ",end: " + end + ",during :" + (targetWindow.getEnd().getMillis() - currentTime) / (1000 * 60));
            currentDateTime = targetWindow.getEnd();
            currentTime = targetWindow.getEnd().getMillis();
        }
        System.out.println("runningTime: " + duringMin + " , averageRunningTime: " + duringMin / (double)size);
        System.out.println("********************************************");
        System.out.println("Scheduling ends");
        System.out.println("end");
        return size;
    }

    private static Sat getTargetSatBySJF() {
        Sat target = null;
        double maxPri = Integer.MIN_VALUE;
        for (int i = 0; i < sates.size(); ++i) {
            Sat tem = sates.get(i);
            if (tem.isVisible()) {
                continue;
            }
            double pri = getPriorityBySJF(tem);
            if (pri > maxPri) {
                maxPri = pri;
                target = tem;
            }
        }
        if (target != null) {
            target.setVisible(true);
            sates.remove(target);
        }
        if (maxPri == Integer.MIN_VALUE) {
            return null;
        } else {
            return target;
        }
    }

    //最短剩余时间
    private static double getPriorityBySJF(Sat sat) {
        List<Window> lists = sat.getWindows();
        double res = Integer.MIN_VALUE;
        for (int i = 0; i < lists.size(); ++i) {
            Window window = lists.get(i);
            long start = window.getStart().getMillis();
            long end = window.getEnd().getMillis();
            long witting = 0;
            //表示等待witting
            if((start - currentTime) > 0 && (start - currentTime <= ((end - start) / 2.0))){
                witting = start - currentTime;
            }
            if((witting > 0) || (witting == 0 && (currentTime - start >= 0 && currentTime - start < 1000 * 60))){
                sat.setNowIdx(i);
                res = 1000000000.0 / (end - start + witting);
            }
            if (res != Integer.MIN_VALUE) {
                return res;
            }
        }
        return res;
    }

    public static List<Sat> svnToPrn(List<Sat> sates) {
        String[][] mappingMap = new String[][]{
                {"svn41", "prn14"}, {"svn43", "prn13"}, {"svn45", "prn21"}, {"svn48", "prn07"}, {"svn50", "prn05"}, {"svn51", "prn20"}, {"svn52", "prn31"}, {"svn53", "prn17"},
                {"svn55", "prn15"}, {"svn56", "prn16"}, {"svn57", "prn29"}, {"svn58", "prn12"}, {"svn59", "prn19"}, {"svn61", "prn02"}, {"svn62", "prn25"}, {"svn63", "prn01"},
                {"svn64", "prn30"}, {"svn65", "prn24"}, {"svn66", "prn27"}, {"svn67", "prn06"}, {"svn68", "prn09"}, {"svn69", "prn03"}, {"svn70", "prn32"}, {"svn71", "prn26"},
                {"svn72", "prn08"}, {"svn73", "prn10"}, {"svn74", "prn04"}, {"svn75", "prn18"}, {"svn76", "prn23"}, {"svn77", "prn22"}, {"svn78", "prn11"}
        };
        for (int i = 0; i < sates.size(); ++i) {
            for (int j = 0; j < mappingMap.length; ++j) {
                String id = sates.get(i).getId();
                if (id.contains(mappingMap[j][0])) {
                    String newId = id.replaceAll(mappingMap[j][0], mappingMap[j][1]);
                    sates.get(i).setId(newId);
                    break;
                }
            }
        }
        return sates;
    }
}
