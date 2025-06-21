public class Task {
    private final String description;
    private final int hoursToCompleteTask; // сколько часов нужно на выполнение
    private int hoursSpent;  // сколько часов потрачено на выполнение

    public Task(String description, int hoursToCompleteTask) {
        this.description = description;
        this.hoursToCompleteTask = hoursToCompleteTask;
        this.hoursSpent= 0;
    }

    public String getDescription() {
        return description;
    }

    public int getHoursToCompleteTask() {
        return hoursToCompleteTask;
    }

    public int getHoursSpent() {
        return hoursSpent;
    }
        public void addHoursSpent(int hours) {
        hoursSpent += hours;
    }

    public boolean isCompleted() {
        return hoursSpent >= hoursToCompleteTask;
    }

    public int getRemainingHours() { // оставшиеся часы для
        // выполнения задачи
        return hoursToCompleteTask - hoursSpent;
    }
}
