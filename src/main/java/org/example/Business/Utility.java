package org.example.Business;

import org.example.Model.Employee;
import org.example.Model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Utility {
    private static List<Employee> filteredEmployees;

    public static List<Employee> filterEmployees(TaskManagement taskManagement) {
        filteredEmployees = new ArrayList<>();

        for (Employee employee : taskManagement.taskMap.keySet()) {
            if (taskManagement.calculateEmployeeWorkDuration(employee.getIdEmployee()) > 40) {
                filteredEmployees.add(employee);
            }
        }

        for (int i = 0; i < filteredEmployees.size() - 1; i++) {
            for (int j = 0; j < filteredEmployees.size() - i - 1; j++) {
                if (taskManagement.calculateEmployeeWorkDuration(filteredEmployees.get(j).getIdEmployee()) >
                        taskManagement.calculateEmployeeWorkDuration(filteredEmployees.get(j + 1).getIdEmployee())) {
                    Employee temp = filteredEmployees.get(j);
                    filteredEmployees.set(j, filteredEmployees.get(j + 1));
                    filteredEmployees.set(j + 1, temp);
                }
            }
        }

        return filteredEmployees;
    }

    public static Map<String, Map<String, Integer>> calculateTaskStatusCount(TaskManagement taskManagement) {
        Map<String, Map<String, Integer>> taskStatusMap = new HashMap<>();

        for (Employee employee : taskManagement.taskMap.keySet()) {
            int completedTasks = 0;
            int uncompletedTasks = 0;

            for (Task task : taskManagement.taskMap.get(employee)) {
                if (task.getStatusTask()) {
                    completedTasks++;
                } else {
                    uncompletedTasks++;
                }
            }

            Map<String, Integer> statusCount = new HashMap<>();
            statusCount.put("Completed", completedTasks);
            statusCount.put("Uncompleted", uncompletedTasks);

            taskStatusMap.put(employee.getName(), statusCount);
        }

        return taskStatusMap;
    }
}