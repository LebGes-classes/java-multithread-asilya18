public class Employee   {
    private final String name;
    private final String position;
    private int completedTasks; // количество выполненных задач


    public Employee(String name, String position) {
        this.name = name;
        this.position = position;
    }

    public String getName() {
        return name;
    }

    public String getPosition() {
        return position;
    }

    public int getCompletedTasks() {
        return completedTasks;
    }

    public void addCompletedTask() {
        completedTasks++;
    }
}
