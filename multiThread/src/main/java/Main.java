import java.util.*;
    import java.util.concurrent.*;

    public class Main {
        public static void main(String[] args) throws Exception {


            Map<String, List<Task>> employeeTasks = ExcelHandler.readTasks("src/main/resources/multiThread.xlsx");

            List<Employee> employees = new ArrayList<>(); //  список сотрудников
            Map<String, BlockingQueue<Task>> employeeTaskQueues = new HashMap<>();
            // ключ - имя сотрудника , значение - его задача

            employeeTasks.forEach((name, tasks) -> {
                employees.add(new Employee(name, "должность")); // создаем список сотрудников
                BlockingQueue<Task> personalQueue = new LinkedBlockingQueue<>(); // у каждого сотрудника своя очередь задач
                personalQueue.addAll(tasks); // добавляем в персональную очередь все задачи определенного сотрудника
                employeeTaskQueues.put(name, personalQueue); // сохраняем очередь сотрудника в общую очередь
            });

            // создаём map для хранения суммарных часов работы и простоя по сотрудникам
            Map<String, Integer> totalHoursWorked = new HashMap<>();
            Map<String, Integer> totalIdleHours = new HashMap<>();
            employees.forEach(emp -> {
                totalHoursWorked.put(emp.getName(), 0);
                totalIdleHours.put(emp.getName(), 0);
            });


            while (hasTasks(employeeTaskQueues)) {
                List<WorkDay> workDays = new ArrayList<>();
                for (int i = 0; i < employees.size(); i++) {
                    workDays.add(new WorkDay());
                }


                ExecutorService executor = Executors.newFixedThreadPool(employees.size());
                // создаем пул потоков по одному на каждого сотрудника , каждый поток обрабатывает одного сотрудника
                // пулы переспользуют созданные ранее потоки в целях экономии ресурсов ,
                // а также  контролирует выполнение задач
                // одновременно может выполняться не более employees.size потоков

                for (int i = 0; i < employees.size(); i++) {
                    Employee currentEmployee = employees.get(i);
                    executor.execute(new TaskProcessor( // создаем экземпляр TaskProcessor
                            // он представляет задачу , которую нужно выполнить в потоке
                            // задача отправляется в пул потоков
                            // но так как у меня количество потоков == количество задач ,
                            // все задачи выполняются незамедлительно
                            currentEmployee,
                            employeeTaskQueues.get(currentEmployee.getName()),
                            workDays.get(i)
                    ));
                }

                executor.shutdown(); // переводим пул потоков в закрытие , больше нет возможности принимать новые задачи
                executor.awaitTermination(1, TimeUnit.DAYS);
                // блокирует текущий поток и ждет , пока закончится рабочий день

                // после конца рабочего дня обновляем суммарные часы
                for (int i = 0; i < employees.size(); i++) {
                    Employee emp = employees.get(i);
                    WorkDay day = workDays.get(i);

                    totalHoursWorked.put(emp.getName(),
                            totalHoursWorked.getOrDefault(emp.getName(), 0) + day.getHoursWorked());
                    totalIdleHours.put(emp.getName(),
                            totalIdleHours.getOrDefault(emp.getName(), 0) + day.getIdleHours());
                }

                // передаём суммарные часы в saveResults
                List<WorkDay> totalWorkDays = new ArrayList<>();
                for (Employee emp : employees) {
                    WorkDay totalDay = new WorkDay();
                    totalDay.addHoursWorked(totalHoursWorked.get(emp.getName()));
                    totalDay.addIdleHours(totalIdleHours.get(emp.getName()));
                    totalWorkDays.add(totalDay);
                }


                String filePath = "src/main/resources/multiThread.xlsx";

                ExcelHandler.saveResults("src/main/resources/multiThread.xlsx", employees, totalWorkDays); // сохранение результатов
            }
        }

        private static boolean hasTasks(Map<String, BlockingQueue<Task>> queues) {
            return queues.values().stream().anyMatch(q -> !q.isEmpty());
        } // если какие-то задачи остались (если лямбда-выражение возвращает true) ,
        // они переходят на другой рабочий день

    }