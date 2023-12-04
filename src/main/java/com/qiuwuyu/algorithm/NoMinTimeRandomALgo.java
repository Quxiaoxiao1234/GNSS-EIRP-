package com.qiuwuyu.algorithm;

import com.qiuwuyu.entity.Sat;
import com.qiuwuyu.entity.Window;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author paralog
 * @date 2023/2/9 15:27
 */
public class NoMinTimeRandomALgo {



    private static long stepTime = 1000;


    public static double[] selectSates(List<Sat> sates, int size) {
        List<Sat> tempSates = new ArrayList<>();
        Random r = new Random();
        if (sates.size() == size) {
            tempSates = sates;
        } else {
            while (tempSates.size() != size) {
                int index = r.nextInt(sates.size() - 1);
                tempSates.add(sates.get(index));
                sates.remove(index);
            }
        }
        double[] doubles = executeByTimeForPictureBySJF(tempSates);
        return doubles;
    }

    public static double[] executeByTimeForPicture(List<Sat> sates) {
        long currentTime = new DateTime(2022, 9, 14, 04, 00, 00).getMillis();
        DateTime currentDateTime = new DateTime(2022, 9, 14, 04, 00, 00);
        long endTime = new DateTime(2022, 9, 21, 8, 00, 00).getMillis();
        int size = sates.size();
        double[] res = new double[2];
        double resEnd = 0;
        sates = svnToPrn(sates);
        long oriTime = new DateTime(2022, 9, 14, 04, 00, 00).getMillis();
        long baseUnit = 1000 * 60 * 60;
        int duringMin = 0;
        System.out.println("********************************************");
        System.out.println("Scheduling starts");
        while (currentTime <= endTime) {
            Sat targetSat = getTargetSat(sates, currentTime);
            if (targetSat == null) {
                if (sates.size() == 0) {
                    break;
                } else {
                    while (currentTime <= endTime && targetSat == null) {
                        targetSat = getTargetSat(sates, currentTime);
                        currentTime += stepTime;
                        currentDateTime.plusSeconds(1);
                    }
                }
            }
            //targetWindow表示当前窗口正在被执行
            Window targetWindow = targetSat.getWindows().get(targetSat.getNowIdx());
            double start = (double) (currentTime - oriTime) / (double) baseUnit;
            double end = (double) (targetWindow.getEnd().getMillis() - oriTime) / (double) baseUnit;
            resEnd = end;
            duringMin += (targetWindow.getEnd().getMillis() - currentTime) / (1000 * 60);
            System.out.println(targetSat.getId() + ",start: " + start + ",end: " + end + ",during :" + (targetWindow.getEnd().getMillis() - currentTime) / (1000 * 60));
            currentDateTime = targetWindow.getEnd();
            currentTime = targetWindow.getEnd().getMillis();
        }
        System.out.println("runningTime: " + duringMin + " , averageRunningTime: " + duringMin / (double) size);
        System.out.println("********************************************");
        System.out.println("Scheduling ends");
        System.out.println("end");
        res[0] = resEnd;
        res[1] = duringMin / (resEnd * 60);
        return res;
    }

    private static Sat getTargetSat(List<Sat> sates, long currenTime) {
        Sat target = null;
        double maxPri = Integer.MIN_VALUE;
        for (int i = 0; i < sates.size(); ++i) {
            Sat tem = sates.get(i);
            if (tem.isVisible()) {
                continue;
            }
            double pri = getPriorityByHRRN(tem, currenTime);
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
    private static double getPriorityByHRRN(Sat sat, long currentTime) {
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
            if (res != Integer.MIN_VALUE) {
                return res;
            }
        }
        return res;
    }

    //由于prn14对应两颗正在运行的卫星,定义svn-41对应prn14,svn77对应prn22
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


    public static double[] executeByTimeForPictureByFCFS(List<Sat> sates) {
        long currentTime = new DateTime(2022, 9, 14, 04, 00, 00).getMillis();
        DateTime currentDateTime = new DateTime(2022, 9, 14, 04, 00, 00);
        long endTime = new DateTime(2022, 9, 21, 8, 00, 00).getMillis();
        int size = sates.size();
        double[] res = new double[2];
        double resEnd = 0;
        sates = svnToPrn(sates);
        long oriTime = new DateTime(2022, 9, 14, 04, 00, 00).getMillis();
        long baseUnit = 1000 * 60 * 60;
        int duringMin = 0;
        System.out.println("********************************************");
        System.out.println("Scheduling starts");
        while (currentTime <= endTime) {
            Sat targetSat = getTargetSatByFCFS(sates, currentTime);
            if (targetSat == null) {
                if (sates.size() == 0) {
                    break;
                } else {
                    while (currentTime <= endTime && targetSat == null) {
                        targetSat = getTargetSatByFCFS(sates, currentTime);
                        currentTime += stepTime;
                        currentDateTime.plusSeconds(1);
                    }
                }
            }
            //targetWindow表示当前窗口正在被执行
            Window targetWindow = targetSat.getWindows().get(targetSat.getNowIdx());
            double start = (double) (currentTime - oriTime) / (double) baseUnit;
            double end = (double) (targetWindow.getEnd().getMillis() - oriTime) / (double) baseUnit;
            resEnd = end;
            duringMin += (targetWindow.getEnd().getMillis() - currentTime) / (1000 * 60);
            System.out.println(targetSat.getId() + ",start: " + start + ",end: " + end + ",during :" + (targetWindow.getEnd().getMillis() - currentTime) / (1000 * 60));
            currentDateTime = targetWindow.getEnd();
            currentTime = targetWindow.getEnd().getMillis();
        }
        System.out.println("runningTime: " + duringMin + " , averageRunningTime: " + duringMin / (double) size);
        System.out.println("********************************************");
        System.out.println("Scheduling ends");
        System.out.println("end");
        res[0] = resEnd;
        res[1] = duringMin / (resEnd * 60);
        return res;
    }

    private static Sat getTargetSatByFCFS(List<Sat> sates, long currenTime) {
        Sat target = null;
        double maxPri = Integer.MIN_VALUE;
        for (int i = 0; i < sates.size(); ++i) {
            Sat tem = sates.get(i);
            if (tem.isVisible()) {
                continue;
            }
            double pri = getPriorityByFCFS(tem, currenTime);
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
    private static double getPriorityByFCFS(Sat sat, long currentTime) {
        List<Window> lists = sat.getWindows();
        double res = Integer.MIN_VALUE;
        for (int i = 0; i < lists.size(); ++i) {
            Window window = lists.get(i);
            long start = window.getStart().getMillis();
            double witting = currentTime - start;
            if (start <= currentTime && start + 60 * 1000 >= currentTime ) {
                sat.setNowIdx(i);
                res = (double) 100000000 / (witting + 1);
            }
            if (res != Integer.MIN_VALUE) {
                return res;
            }
        }
        return res;
    }

    public static double[] executeByTimeForPictureBySJF(List<Sat> sates) {
        long currentTime = new DateTime(2022, 9, 14, 04, 00, 00).getMillis();
        DateTime currentDateTime = new DateTime(2022, 9, 14, 04, 00, 00);
        long endTime = new DateTime(2022, 9, 21, 8, 00, 00).getMillis();
        int size = sates.size();
        double[] res = new double[2];
        double resEnd = 0;
        sates = svnToPrn(sates);
        long oriTime = new DateTime(2022, 9, 14, 04, 00, 00).getMillis();
        long baseUnit = 1000 * 60 * 60;
        int duringMin = 0;
        System.out.println("********************************************");
        System.out.println("Scheduling starts");
        while (currentTime <= endTime) {
            Sat targetSat = getTargetSatBySJF(sates, currentTime);
            if (targetSat == null) {
                if (sates.size() == 0) {
                    break;
                } else {
                    while (currentTime <= endTime && targetSat == null) {
                        targetSat = getTargetSatBySJF(sates, currentTime);
                        currentTime += stepTime;
                        currentDateTime.plusSeconds(1);
                    }
                }
            }
            //targetWindow表示当前窗口正在被执行
            Window targetWindow = targetSat.getWindows().get(targetSat.getNowIdx() - 1);
            double start = (double) (currentTime - oriTime) / (double) baseUnit;
            double end = (double) (targetWindow.getEnd().getMillis() - oriTime) / (double) baseUnit;
            resEnd = end;
            duringMin += (targetWindow.getEnd().getMillis() - currentTime) / (1000 * 60);
            System.out.println(targetSat.getId() + ",start: " + start + ",end: " + end + ",during :" + (targetWindow.getEnd().getMillis() - currentTime) / (1000 * 60));
            currentDateTime = targetWindow.getEnd();
            currentTime = targetWindow.getEnd().getMillis();
        }
        System.out.println("runningTime: " + duringMin + " , averageRunningTime: " + duringMin / (double) size);
        System.out.println("********************************************");
        System.out.println("Scheduling ends");
        System.out.println("end");
        res[0] = resEnd;
        res[1] = duringMin / (resEnd * 60);
        return res;
    }

    private static Sat getTargetSatBySJF(List<Sat> sates, long currenTime) {
        Sat target = null;
        double maxPri = Integer.MIN_VALUE;
        for (int i = 0; i < sates.size(); ++i) {
            Sat tem = sates.get(i);
            if (tem.isVisible()) {
                continue;
            }
            double pri = getTargetSatBySJF(tem, currenTime);
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

    //最短剩余时间优先
    private static double getTargetSatBySJF(Sat sat, long currentTime) {
        List<Window> lists = sat.getWindows();
        double res = Integer.MIN_VALUE;
        for (int i = sat.getNowIdx(); i < lists.size(); ++i) {
            Window window = lists.get(i);
            long start = window.getStart().getMillis();
            long end = start + window.getDuring();
            if (start <= currentTime && start + 60 * 1000 >= currentTime) {
                sat.setNowIdx(i + 1);
                res = 100000000.0 / (double) (end - currentTime);
            }
            if (res != Integer.MIN_VALUE) {
                return res;
            }
        }
        return res;
    }
}

