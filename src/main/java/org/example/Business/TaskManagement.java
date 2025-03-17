package org.example.Business;

import org.example.Model.ComplexTask;
import org.example.Model.Employee;
import org.example.Model.SimpleTask;
import org.example.Model.Task;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskManagement implements Serializable {
    public Map<Employee, List<Task>> taskMap = new HashMap<>();
    private List<Task> tasks = new ArrayList<>();

    public void addEmployee(Employee employee) {
        taskMap.put(employee, new ArrayList<>());
    }

    public void addTask(Task task) {
        tasks.add(task);
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void assignTaskToEmployee(int idEmployee, Task task) {
        Employee employee = findEmployee(idEmployee);

        if (employee == null) {
            System.out.println("Employee not found");
            return;
        }

        if (!taskMap.containsKey(employee)) {
            taskMap.put(employee, new ArrayList<>());
        }

        if (!taskMap.get(employee).contains(task)) {
            taskMap.get(employee).add(task);
            if (task instanceof ComplexTask) {
                assignComplexTask(idEmployee, (ComplexTask) task);
            }
        }
    }

    private void assignComplexTask(int idEmployee, ComplexTask complexTask) {
        for (Task task : complexTask.getSubTasks()) {
            if (task instanceof ComplexTask) {
                assignComplexTask(idEmployee, (ComplexTask) task);
            }
            taskMap.get(findEmployee(idEmployee)).add(task);
        }
    }

    public int calculateEmployeeWorkDuration(int idEmployee) {
        Employee employee = findEmployee(idEmployee);

        if (employee == null) {
            System.out.println("Employee not found");
            return 0;
        }

        List<Task> tasks = taskMap.get(employee);
        int totalDuration = 0;
        for (Task task : tasks) {
            if (task instanceof SimpleTask) {
                totalDuration += task.estimateDuration();
            }
        }

        return totalDuration;
    }

    public void modifyTaskStatus(int idEmployee, int idTask) {
        Employee employee = findEmployee(idEmployee);

        if (employee == null) {
            System.out.println("Employee not found");
            return;
        }

        List<Task> tasks = taskMap.get(employee);
        Task taskToModify = null;
        for (Task task : tasks) {
            if (task.getIdTask() == idTask) {
                taskToModify = task;
                break;
            }
        }

        if (taskToModify == null) {
            System.out.println("Task not found");
            return;
        }

        taskToModify.setStatusTask(!taskToModify.getStatusTask());
    }

    private Employee findEmployee(int idEmployee) {
        Employee employee = null;
        for (Employee emp : taskMap.keySet()) {
            if (emp.getIdEmployee() == idEmployee) {
                employee = emp;
                break;
            }
        }

        return employee;
    }

    public Task getTaskById(int idTask) {
        for (Task task : tasks) {
            if (task.getIdTask() == idTask) {
                return task;
            }
        }

        return null;
    }
}
