import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExcelHandler {
    public static Map<String, List<Task>> readTasks(String filePath) throws Exception {
        // возвращает мапу , где ключ - имя сотрудника , значение - список его задач
        Map<String, List<Task>> tasksByEmployee = new HashMap<>(); // самая оптимальная реализация map

        try (Workbook workbook = new XSSFWorkbook(new FileInputStream(filePath))) { // для работы с xlsx файлами
            Sheet sheet = workbook.getSheet("Tasks"); // берём первый лист

            for (Row row : sheet) { // перебор строк
                if (row.getRowNum() == 0) {
                    continue;
                } // пропускаем заголовок

                String name = row.getCell(0).getStringCellValue(); // имя сотрудника
                String description = row.getCell(1).getStringCellValue(); // описание задачи
                int hours = (int) row.getCell(2).getNumericCellValue(); // время , отведенное на задачу ,
                // преобразовано в int приведением типов

                // группируем задачи по сотрудникам
                tasksByEmployee
                        .computeIfAbsent(name, k -> new ArrayList<>())
                        .add(new Task(description, hours));
                // если сотрудника нет в группировке , создаем новый список его задач
                // и добавляем туда задачу
            }
        }

        return tasksByEmployee; // возращаем мапу с распределенными задачами
    }

    public static void saveResults(String filePath, List<Employee> employees, List<WorkDay> workDays) throws Exception {
        try (Workbook workbook = new XSSFWorkbook(new FileInputStream(filePath))) {
            Sheet resultsSheet = workbook.getSheet("Results"); // открываем лист results c индексом 1

            int lastRowNum = resultsSheet.getLastRowNum();
            for (int i = 1; i <= lastRowNum; i++) { // начинаем с 1, чтобы оставить заголовок
                Row row = resultsSheet.getRow(i);
                if (row != null) {
                    resultsSheet.removeRow(row); // удаляем старые данные
                }
            }

            // заполняем данные со 2-й строки с индексом 1
            for (int i = 0; i < employees.size(); i++) {
                Row row = resultsSheet.getRow(i + 1); // получаем или создаем строку
                if (row == null) {
                    row = resultsSheet.createRow(i + 1);
                }

                Employee emp = employees.get(i); // i-тый сотрудник из списка сотрудников
                WorkDay day = workDays.get(i); // рабочий день i-го сотрудника , так как индексация в списках совпадает

                row.createCell(0).setCellValue(emp.getName());
                row.createCell(1).setCellValue(emp.getCompletedTasks());
                row.createCell(2).setCellValue(day.getHoursWorked());
                row.createCell(3).setCellValue(day.getIdleHours());

            } // мы заполняем каждую ячейку в строке определенного сотрудника

            // перезаписываем файл excel c новыми данными
            try (FileOutputStream out = new FileOutputStream(filePath)) {
                workbook.write(out);
            }
        }
    }
}