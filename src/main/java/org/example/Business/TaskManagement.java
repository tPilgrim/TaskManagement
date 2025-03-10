package org.example.Business;

import org.example.Model.Employee;
import org.example.Model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskManagement {
    private Map<Employee, List<Task>> taskMap = new HashMap<>();
    private List<Employee> employees = new ArrayList<>();
    private List<Task> tasks = new ArrayList<>();

    public void addEmployee(Employee employee) {
        employees.add(employee);
        taskMap.put(employee, new ArrayList<>());
    }

    public void addTask(Task task) {
        tasks.add(task);
    }

    public void assignTaskToEmployee(int idEmployee, Task task) {
        Employee employee = null;
        for (Employee emp : employees) {
            if (emp.getIdEmployee() == idEmployee) {
                employee = emp;
                break;
            }
        }

        if (employee == null) {
            System.out.println("Employee not found");
            return;
        }

        taskMap.get(employee).add(task);
    }

    public int calculateEmployeeWorkDuration(int idEmployee) {
        Employee employee = null;
        for (Employee emp : employees) {
            if (emp.getIdEmployee() == idEmployee) {
                employee = emp;
                break;
            }
        }

        if (employee == null) {
            System.out.println("Employee not found");
            return 0;
        }

        List<Task> tasks = taskMap.get(employee);

        int totalDuration = 0;
        for (Task task : tasks) {
            totalDuration += task.estimateDuration();
        }

        return totalDuration;
    }

    public void modifyTaskStatus(int idEmployee, int idTask) {
        Employee employee = null;
        for (Employee emp : employees) {
            if (emp.getIdEmployee() == idEmployee) {
                employee = emp;
                break;
            }
        }

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

        taskToModify.setStatusTask(true);
    }
}
