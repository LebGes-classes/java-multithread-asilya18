import java.util.concurrent.BlockingQueue;

public class TaskProcessor implements Runnable {
    private final Employee employee;
    private final BlockingQueue<Task> taskQueue;
    private final WorkDay workDay;


    public TaskProcessor(Employee employee, BlockingQueue<Task> taskQueue, WorkDay workDay) {
        this.employee = employee;
        this.taskQueue = taskQueue;
        this.workDay = workDay;
    }

    public WorkDay getWorkDay() {
        return this.workDay;
    }

    @Override
    public void run() { // начало рабочего дня
        final String threadName = Thread.currentThread().getName();
        System.out.printf("[%s] %s's day started%n", threadName, employee.getName());

        while (!workDay.isDayOver()) {
            try {
                Task task = taskQueue.peek(); // не забираем задачу сразу , просто смотрим

                if (task == null) {
                    break;
                }

                int hoursToWork = Math.min(
                        task.getRemainingHours(),
                        workDay.getRemainingHours()
                ); // часы работы - минимальное из оставшихся часов по задаче
                // и оставшихся часов по рабочему дню

                if (hoursToWork > 0) {
                    Thread.sleep(hoursToWork * 200); // имитируем работу
                    // пока наш работник занят задачей , наш поток ждет
                    task.addHoursSpent(hoursToWork); // увеличиваем количество часов , потраченных на задачу
                    workDay.addWorkHours(hoursToWork); // увеличиваем счетчик отработанных часов в целом за рабочий день

                    System.out.printf("[%s] %s worked %dh on '%s'%n",
                            threadName, employee.getName(), hoursToWork, task.getDescription());

                    if (task.isCompleted()) {
                        taskQueue.take(); // забираем задачу после ее выполнения
                        employee.addCompletedTask(); // добавляем в выполненные задачи работника
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        int idle = workDay.getRemainingHours(); // сколько часов до конца рабочего дня
        workDay.addIdleHours(idle); // увеличиваем часы простоя в целом за рабочий день
        System.out.printf("[%s] %s's day ended. Idle: %dh%n",
                threadName, employee.getName(), idle); // вывод сообщения об окончании рабочего дня и часах простоя
    }
}