package com.lang.hospital.utils;

import android.widget.TextView;
import android.widget.Toast;
import com.lang.hospital.bean.DayWork;
import com.lang.hospital.bean.WorkPerson;
import com.lang.hospital.fragment.ArrangeWorkFra;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.format.Colour;
import jxl.write.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author dmrfcoder
 * @date 2019/2/14
 */

public class ExcelUtil {

    private static WritableFont arial14font = null;

    private static WritableCellFormat arial14format = null;
    private static WritableFont arial10font = null;
    private static WritableCellFormat arial10format = null;
    private static WritableFont arial12font = null;
    private static WritableCellFormat arial12format = null;
    private final static String UTF8_ENCODING = "UTF-8";


    /**
     * 单元格的格式设置 字体大小 颜色 对齐方式、背景颜色等...
     */
    private static void format() {
        try {
            arial14font = new WritableFont(WritableFont.ARIAL, 14, WritableFont.BOLD);
            arial14font.setColour(Colour.LIGHT_BLUE);
            arial14format = new WritableCellFormat(arial14font);
            arial14format.setAlignment(jxl.format.Alignment.CENTRE);
            arial14format.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN);
            arial14format.setBackground(Colour.VERY_LIGHT_YELLOW);

            arial10font = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD);
            arial10format = new WritableCellFormat(arial10font);
            arial10format.setAlignment(jxl.format.Alignment.CENTRE);
            arial10format.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN);
            arial10format.setBackground(Colour.GRAY_25);

            arial12font = new WritableFont(WritableFont.ARIAL, 10);
            arial12format = new WritableCellFormat(arial12font);
            //对齐格式
            arial10format.setAlignment(jxl.format.Alignment.CENTRE);
            //设置边框
            arial12format.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN);

        } catch (WriteException e) {
            e.printStackTrace();
        }
    }


    /**
     * 初始化Excel表格
     *
     * @param filePath  存放excel文件的路径（path/demo.xls）
     * @param sheetName Excel表格的表名
     * @param colName   excel中包含的列名（可以有多个）
     */
    public static void initExcel(String filePath, String sheetName, String[] colName) {
        format();
        WritableWorkbook workbook = null;
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                file.createNewFile();
            } else {
                return;
            }
            workbook = Workbook.createWorkbook(file);
            //设置表格的名字
            WritableSheet sheet = workbook.createSheet(sheetName, 0);
            //创建标题栏
            for (int col = 0; col < colName.length; col++) {
                sheet.addCell(new Label(col, 0, "", arial10format));
            }
            sheet.mergeCells(2, 0, 5, 0);
            sheet.mergeCells(6, 0, 9, 0);
            sheet.addCell(new Label(2, 0, "临江门", arial10format));
            sheet.addCell(new Label(6, 0, "江南", arial10format));
            for (int col = 0; col < colName.length; col++) {
                sheet.addCell(new Label(col, 1, colName[col], arial10format));
            }
            //设置行高
            sheet.setRowView(0, 340);
            workbook.write();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (workbook != null) {
                try {
                    workbook.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 将制定类型的List写入Excel中
     */
    public static void writeObjListToExcel(final String fileName, final TextView view) {
        WritableWorkbook writebook = null;
        InputStream in = null;
        try {
            WorkbookSettings setEncode = new WorkbookSettings();
            setEncode.setEncoding(UTF8_ENCODING);

            in = new FileInputStream(new File(fileName));
            Workbook workbook = Workbook.getWorkbook(in);
            writebook = Workbook.createWorkbook(new File(fileName), workbook);
            WritableSheet sheet = writebook.getSheet(0);

            for (int i = 0; i < ArrangeWorkFra.Companion.getDayWorkList().size(); i++) {
                DayWork dayWork = ArrangeWorkFra.Companion.getDayWorkList().get(i);
                List<String> list = new ArrayList<>();
                list.add(dayWork.getDay());
                list.add(ArrangeWorkFra.Companion.getWeeks()[dayWork.getWeek()]);
                for (int j = 0; j < dayWork.getPersons().size(); j++) {
                    if (dayWork.getPersons().get(j) != -1)
                        list.add(ArrangeWorkFra.Companion.getWorkPersonList().get(dayWork.getPersons().get(j)).getName());
                    else
                        list.add(" ");
                }

                for (int j = 0; j < list.size(); j++) {
                    sheet.addCell(new Label(j, i + 2, list.get(j), arial12format));
                    if (list.get(j).length() <= 4) {
                        //设置列宽
                        sheet.setColumnView(j, list.get(j).length() + 9);
                    } else {
                        //设置列宽
                        sheet.setColumnView(j, list.get(j).length() + 6);
                    }
                }
                //设置行高
                sheet.setRowView(i + 2, 350);
            }

            String s1 = "周末夜班:", s2 = "周末白班:", s3 = "夜班:", s4 = "白班:", s5 = "总班:";
            int startLine = ArrangeWorkFra.Companion.getDayWorkList().size() + 4;
            for (int i = 0; i < ArrangeWorkFra.Companion.getWorkPersonList().size() + 1; i++) {
                if (i == 0) {
                    sheet.addCell(new Label(i, startLine, "", arial12format));
                    sheet.addCell(new Label(i, startLine + 1, s1.substring(0, s1.length() - 1), arial12format));
                    sheet.addCell(new Label(i, startLine + 2, s2.substring(0, s2.length() - 1), arial12format));
                    sheet.addCell(new Label(i, startLine + 3, s3.substring(0, s3.length() - 1), arial12format));
                    sheet.addCell(new Label(i, startLine + 4, s4.substring(0, s4.length() - 1), arial12format));
                    sheet.addCell(new Label(i, startLine + 5, s5.substring(0, s5.length() - 1), arial12format));
                } else {
                    WorkPerson workPerson = ArrangeWorkFra.Companion.getWorkPersonList().get(i - 1);
                    sheet.addCell(new Label(i, startLine, workPerson.getName(), arial12format));
                    sheet.addCell(new Label(i, startLine + 1, String.valueOf(workPerson.getWeekNight()), arial12format));
                    sheet.addCell(new Label(i, startLine + 2, String.valueOf(workPerson.getWeekday()), arial12format));
                    sheet.addCell(new Label(i, startLine + 3, String.valueOf(workPerson.getWorkTimes()[3] + workPerson.getWorkTimes()[7]), arial12format));
                    sheet.addCell(new Label(i, startLine + 4, String.valueOf(workPerson.getCountWork() - workPerson.getWorkTimes()[3] - workPerson.getWorkTimes()[7]), arial12format));
                    sheet.addCell(new Label(i, startLine + 5, String.valueOf(workPerson.getCountWork()), arial12format));
                    s1 += workPerson.getName().replace(" ", "") + "(" + workPerson.getWeekNight() + ")";
                    s2 += workPerson.getName().replace(" ", "") + "(" + workPerson.getWeekday() + ")";
                    s3 += workPerson.getName().replace(" ", "") + "(" + (workPerson.getWorkTimes()[3] + workPerson.getWorkTimes()[7]) + ")";
                    s4 += workPerson.getName().replace(" ", "") + "(" + (workPerson.getCountWork() - workPerson.getWorkTimes()[3] - workPerson.getWorkTimes()[7]) + ")";
                    s5 += workPerson.getName().replace(" ", "") + "(" + workPerson.getCountWork() + ")";
                }
                sheet.setRowView(startLine, 350);
                sheet.setRowView(startLine + 1, 350);
                sheet.setRowView(startLine + 2, 350);
                sheet.setRowView(startLine + 3, 350);
                sheet.setRowView(startLine + 4, 350);
                sheet.setRowView(startLine + 5, 350);
            }

            writebook.write();
            workbook.close();
            final String finalS1 = s1;
            final String finalS2 = s2;
            final String finalS3 = s3;
            final String finalS4 = s4;
            final String finalS5 = s5;
            view.post(new Runnable() {
                @Override
                public void run() {
                    view.setText(finalS1 + "\n" + finalS2 + "\n" + finalS3 + "\n" + finalS4 + "\n" + finalS5 + "\n");
                    Toast.makeText(view.getContext(), "导出表成功", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (writebook != null) {
                try {
                    writebook.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }

}
