public class WorkDay {
    public static final int max_hours = 8;
    private int hoursWorked; // отработанные часы
    private int idleHours;    // часы простоя

    public WorkDay() {
        this.hoursWorked = 0;
        this.idleHours = 0; // новый рабочий день
    }

    public int getHoursWorked() {
        return hoursWorked;
    }

    public int getIdleHours() {
        return idleHours;
    }

    public void addHoursWorked(int hours) {
        hoursWorked += hours;
    }

    public void addIdleHours(int hours) {
        idleHours += hours;
    }

    public int getRemainingHours() { // сколько осталось до конца
        // рабочего дня
        return max_hours - hoursWorked;
    }

    public boolean isDayOver() {
        return max_hours <= hoursWorked;
    }

    public void addWorkHours(int hours) {
        hoursWorked += hours;
    }

}
