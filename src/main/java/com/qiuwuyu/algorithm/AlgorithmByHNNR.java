package com.qiuwuyu.algorithm;

import com.qiuwuyu.entity.Sat;
import com.qiuwuyu.entity.Window;
import com.qiuwuyu.util.Access;
import org.joda.time.DateTime;
import java.util.List;

/**
 * @author paralog
 * @date 2023/1/12 11:49
 */
public class AlgorithmByHNNR {
    public static List<Sat> sates;

    private static long currentTime = new DateTime(2022, 9, 14, 04, 00, 00).getMillis();
    private static DateTime currentDateTime = new DateTime(2022, 9, 14, 04, 00, 00);
    private static long endTime = new DateTime(2022, 9, 21, 8, 00, 00).getMillis();
    private static long stepTime = 1000;
    private static long minExecuteTime = 1000 * 3600;

    static {
        try {
            sates = Access.StringToObject("D:\\Code\\Satellite\\src\\main\\resources\\Access.txt");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static void executeByTime(String filepath) {
        try {
            sates = Access.StringToObject(filepath);
            sates = svnToPrn(sates);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        executeByTime();
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

    public static void executeByTime() {
        System.out.println("********************************************");
        System.out.println("Scheduling starts");
        while (currentTime <= endTime) {
            boolean flag = false;
            Sat targetSat = getTargetSat();
            if (targetSat == null) {
                flag = true;
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
            //targetWindow表示当前窗口正在被执行
            Window targetWindow = targetSat.getWindows().get(targetSat.getNowIdx());
            if(flag){
                System.out.println(targetSat.getId() + ",start: " + targetWindow.getStart() + ",end: " + targetWindow.getEnd() + ",during :" + (targetWindow.getEnd().getMillis() - currentTime) / (1000 * 60));
            }else {
                System.out.println(targetSat.getId() + ",start: " + currentDateTime + ",end: " + targetWindow.getEnd() + ",during :" + (targetWindow.getEnd().getMillis() - currentTime) / (1000 * 60));
            }
            currentDateTime = targetWindow.getEnd();
            currentTime = targetWindow.getEnd().getMillis();
        }
        System.out.println("********************************************");
        System.out.println("Scheduling ends");
        System.out.println("end");
    }

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
        long oriTime = new DateTime(2022, 9, 14, 04, 00, 00).getMillis();
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
            //targetWindow表示当前窗口正在被执行
            Window targetWindow = targetSat.getWindows().get(targetSat.getNowIdx());
            double start = (double)(currentTime - oriTime) / (double) baseUnit;
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
            //不考虑等待
            if (start <= currentTime && currentTime + minExecuteTime <= end) {
                sat.setNowIdx(i);
                res = 1.0 + (witting + 60 * 1000) / during;
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
            size = sates.size();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        long oriTime = new DateTime(2022, 9, 14, 04, 00, 00).getMillis();
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
            //targetWindow表示当前窗口正在被执行
            Window targetWindow = targetSat.getWindows().get(targetSat.getNowIdx());
            double start = (double)(currentTime - oriTime) / (double) baseUnit;
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
            long end = window.getEnd().getMillis();
            double witting = currentTime - start;
            //不考虑等待
            if (start <= currentTime && currentTime + minExecuteTime <= end) {
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
}
