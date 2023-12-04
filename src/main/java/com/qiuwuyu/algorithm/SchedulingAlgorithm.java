package com.qiuwuyu.algorithm;

import com.qiuwuyu.entity.Sat;
import com.qiuwuyu.entity.Window;
import com.qiuwuyu.util.Access;
import org.joda.time.DateTime;
import java.util.List;

/**
 * @author paralog
 * @date 2022/11/11 14:51
 * 根据地面站可见时间窗口集合得到观测顺序
 */
public class SchedulingAlgorithm {
    public static List<Sat> sates;


    private static long currentTime = new DateTime(2022, 9, 14, 04, 00, 00).getMillis();
    private static DateTime currentDateTime = new DateTime(2022, 9, 14, 04, 00, 00);
    private static long endTime = new DateTime(2022, 9, 21, 8, 00, 00).getMillis();
    private static long stepTime = 1000;
    private static long minExecuteTime = 1000 * 3600;

    static {
        try {
            sates = Access.StringToObject("D:\\Code\\Satellite\\src\\main\\resources\\Access.txt");
            sates = svnToPrn(sates);
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

    public static void executeByTime() {
        System.out.println("*****************");
        while (currentTime <= endTime) {
            Sat targetSat = getTargetSat();
            if (targetSat == null) {
                while (currentTime <= endTime && targetSat == null) {
                    targetSat = getTargetSat();
                    currentTime += stepTime;
                    currentDateTime.plusSeconds(1);
                }
            }
            if (targetSat == null) {
                break;
            }
            Window targetWindow = targetSat.getWindows().get(targetSat.getNowIdx() - 1);
            //nowIdx指的是第几个窗口,假设第一个窗口就是我们的目标.但对于getPri而言,setNowIdx为了方便for循环,是在窗口数+1,故此需要减1
            long millis = targetSat.getWindows().get(targetSat.getNowIdx() - 1).getStart().getMillis();
            if (currentTime >= millis) {
                System.out.println("running: " + targetSat.getId() + ",start: " + currentDateTime + ",end: " + targetSat.getWindows().get(targetSat.getNowIdx() - 1).getEnd() + ",during :" + (targetSat.getWindows().get(targetSat.getNowIdx() - 1).getEnd().getMillis() - currentTime) / (1000 * 60));
                currentDateTime = targetSat.getWindows().get(targetSat.getNowIdx() - 1).getEnd();
                currentTime = targetSat.getWindows().get(targetSat.getNowIdx() - 1).getEnd().getMillis();
            } else {
                long during = (targetSat.getWindows().get(targetSat.getNowIdx() - 1).getEnd().getMillis() - targetSat.getWindows().get(targetSat.getNowIdx() - 1).getStart().getMillis()) / (1000 * 60);
                System.out.println("waiting: " + targetSat.getId() + ",start: " + targetSat.getWindows().get(targetSat.getNowIdx() - 1).getStart() + ",end: " + targetSat.getWindows().get(targetSat.getNowIdx() - 1).getEnd() + "during: " + during);
                currentDateTime = targetSat.getWindows().get(targetSat.getNowIdx() - 1).getEnd();
                currentTime = targetSat.getWindows().get(targetSat.getNowIdx() - 1).getEnd().getMillis();
            }
        }
        System.out.println("*****************");
        System.out.println("end");
    }

    //为了方便Matlab作图
    public static void executeByTimeForPicture(String filepath) {
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
        System.out.println("*****************ForPicture");
        long oriTime = new DateTime(2022, 9, 14, 04, 00, 00).getMillis();
        int duringMin = 0;
        long baseUnit = 1000 * 60 * 60;
        while (currentTime <= endTime) {
            Sat targetSat = getTargetSat();
            if (targetSat == null) {
                while (currentTime <= endTime && targetSat == null) {
                    targetSat = getTargetSat();
                    currentTime += stepTime;
                    currentDateTime.plusSeconds(1);
                }
            }
            if (targetSat == null) {
                break;
            }
            Window targetWindow = targetSat.getWindows().get(targetSat.getNowIdx() - 1);
            //nowIdx指的是第几个窗口,假设第一个窗口就是我们的目标.但对于getPri而言,setNowIdx为了方便for循环,是在窗口数+1,故此需要减1
            long millis = targetSat.getWindows().get(targetSat.getNowIdx() - 1).getStart().getMillis();
            if (currentTime >= millis) {
                double start = (double) (currentTime - oriTime) / (double) baseUnit;
                double end = (targetSat.getWindows().get(targetSat.getNowIdx() - 1).getEnd().getMillis() - oriTime) / (double) baseUnit;
                duringMin += (targetSat.getWindows().get(targetSat.getNowIdx() - 1).getEnd().getMillis() - currentTime) / (1000 * 60);
                System.out.println("running: " + targetSat.getId() + ",start: " + start + ",end: " + end + ",during :" + (targetSat.getWindows().get(targetSat.getNowIdx() - 1).getEnd().getMillis() - currentTime) / (1000 * 60));
                currentDateTime = targetSat.getWindows().get(targetSat.getNowIdx() - 1).getEnd();
                currentTime = targetSat.getWindows().get(targetSat.getNowIdx() - 1).getEnd().getMillis();
            } else {
                double start = (double) (targetSat.getWindows().get(targetSat.getNowIdx() - 1).getStart().getMillis() - oriTime) / (double) baseUnit;
                double end = (targetSat.getWindows().get(targetSat.getNowIdx() - 1).getEnd().getMillis() - oriTime) / (double) baseUnit;
                long during = (targetSat.getWindows().get(targetSat.getNowIdx() - 1).getEnd().getMillis() - targetSat.getWindows().get(targetSat.getNowIdx() - 1).getStart().getMillis()) / (1000 * 60);
                duringMin += during;
                System.out.println("waiting: " + targetSat.getId() + ",start: " + start + ",end: " + end + "during: " + during);
                currentDateTime = targetSat.getWindows().get(targetSat.getNowIdx() - 1).getEnd();
                currentTime = targetSat.getWindows().get(targetSat.getNowIdx() - 1).getEnd().getMillis();
            }
        }
        System.out.println("*****************");
        System.out.println(duringMin);
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
            double pri = getPriorityByAbsoluteTime(tem);
            if (pri > maxPri) {
                maxPri = pri;
                target = tem;
            }
        }
        if (target != null) {
            target.setVisible(true);
        }
        if (maxPri == Integer.MIN_VALUE) {
            return null;
        } else {
            return target;
        }
    }

    //计算sat卫星距离当前时间最近的可见时间窗口的优先级
    private static double getPriority(Sat sat) {
        List<Window> lists = sat.getWindows();
        double res = Integer.MIN_VALUE;
        for (int i = sat.getNowIdx(); i < lists.size(); ++i) {
            Window window = lists.get(i);
            long start = window.getStart().getMillis();
            long end = start + window.getDuring();
            if (start <= currentTime && currentTime + minExecuteTime <= end) {
                sat.setNowIdx(i + 1);
                res = (double) (currentTime - start) / (double) (end - currentTime);
            } else if (currentTime < start && currentTime + minExecuteTime >= start) {
                sat.setNowIdx(i + 1);
                res = (double) (currentTime - start) / (double) (end - currentTime);
            }
            //有一个不为默认值,证明当前窗口距离当前时间最近,立刻返回
            if (res != Integer.MIN_VALUE) {
                return res;
            }
        }
        return res;
    }

    //最短剩余时间优先
    private static double getPriorityByAbsoluteTime(Sat sat) {
        List<Window> lists = sat.getWindows();
        double res = Integer.MIN_VALUE;
        for (int i = sat.getNowIdx(); i < lists.size(); ++i) {
            Window window = lists.get(i);
            long start = window.getStart().getMillis();
            long end = start + window.getDuring();
            if (start <= currentTime && currentTime + minExecuteTime <= end) {
                sat.setNowIdx(i + 1);
                res = 100000000.0 / (double) (end - currentTime);
            } else if (currentTime < start && currentTime + minExecuteTime >= start) {
                sat.setNowIdx(i + 1);
                res = 100000000.0 / (double) (end - currentTime);
            }
            //有一个不为默认值,证明当前窗口距离当前时间最近,立刻返回
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

}
