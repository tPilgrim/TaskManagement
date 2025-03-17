package org.example.GUI;

import org.example.Business.TaskManagement;
import org.example.Business.Utility;
import org.example.DataAcces.DataManager;
import org.example.Model.ComplexTask;
import org.example.Model.Employee;
import org.example.Model.SimpleTask;
import org.example.Model.Task;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TaskManagementGUI {

    private JFrame mainFrame;
    private JList<String> employeeList;
    private JList<String> taskList;
    private DefaultListModel<String> employeeListModel;
    private DefaultListModel<String> taskListModel;
    private TaskManagement taskManagement;
    private int employeeCounter;
    private int taskCounter;
    private JButton addTaskButton;

    public TaskManagementGUI() {
        taskManagement = DataManager.loadData();

        employeeListModel = new DefaultListModel<>();
        taskListModel = new DefaultListModel<>();
        taskCounter = taskManagement.getTasks().size() + 1;
        employeeCounter = taskManagement.taskMap.keySet().size() + 1;

        updateEmplyeeList();

        for(Task task : taskManagement.getTasks()) {
            addTaskToList(task);
            if(task instanceof ComplexTask) {
                ComplexTask complexTask = (ComplexTask) task;
                for (Task subTask : complexTask.getSubTasks()) {
                    addSubtaskToList(subTask, 1);
                }
            }
        }

        if (taskManagement == null) {
            taskManagement = new TaskManagement();
        }
        prepareGUI();
    }

    private void prepareGUI() {
        mainFrame = new JFrame("Task Management");
        mainFrame.setSize(700, 500);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                DataManager.saveData(taskManagement);
                System.exit(0);
            }
        });

        JPanel contentPane = new JPanel(new BorderLayout());

        JPanel listsPanel = new JPanel(new GridLayout(1, 2));
        listsPanel.add(employeePanel());
        listsPanel.add(taskPanel());

        JPanel buttonPanel = new JPanel(new GridLayout(4, 1));
        buttonPanel.add(deselectButton());
        buttonPanel.add(assignTaskButton());
        buttonPanel.add(modifyStatusButton());
        buttonPanel.add(viewStatisticsButton());

        contentPane.add(listsPanel, BorderLayout.CENTER);
        contentPane.add(buttonPanel, BorderLayout.SOUTH);

        mainFrame.add(contentPane);
        mainFrame.setVisible(true);
    }

    private JPanel employeePanel() {
        JPanel panel = new JPanel(new BorderLayout());

        employeeList = new JList<>(employeeListModel);
        JScrollPane employeeScrollPane = new JScrollPane(employeeList);

        employeeList.addListSelectionListener(e -> updateTaskList());

        employeeList.addListSelectionListener(e -> toggleAddTaskButton());

        JButton addEmployeeButton = new JButton("Add Employee");
        addEmployeeButton.addActionListener(e -> addEmployee());

        panel.add(new JLabel("Employees", SwingConstants.CENTER), BorderLayout.NORTH);
        panel.add(employeeScrollPane, BorderLayout.CENTER);
        panel.add(addEmployeeButton, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel taskPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        taskList = new JList<>(taskListModel);
        JScrollPane taskScrollPane = new JScrollPane(taskList);

        addTaskButton = new JButton("Add Task");
        addTaskButton.addActionListener(e -> addTask());
        addTaskButton.setEnabled(true);

        panel.add(new JLabel("Tasks", SwingConstants.CENTER), BorderLayout.NORTH);
        panel.add(taskScrollPane, BorderLayout.CENTER);

        JPanel taskButtonPanel = new JPanel(new BorderLayout());
        taskButtonPanel.add(addTaskButton, BorderLayout.CENTER);

        panel.add(taskButtonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel deselectButton() {
        JButton deselectButton = new JButton("Deselect All");
        deselectButton.setPreferredSize(new Dimension(mainFrame.getWidth(), 25));
        deselectButton.addActionListener(e -> {
            employeeList.clearSelection();
            taskList.clearSelection();
            updateTaskList();
        });

        JPanel deselectPanel = new JPanel(new BorderLayout());
        deselectPanel.add(deselectButton, BorderLayout.CENTER);
        return deselectPanel;
    }

    private JPanel assignTaskButton() {
        JButton assignTaskButton = new JButton("Assign Task");
        assignTaskButton.addActionListener(e -> assignTaskToEmployee());

        JPanel assignPanel = new JPanel(new BorderLayout());
        assignPanel.add(assignTaskButton, BorderLayout.CENTER);
        return assignPanel;
    }

    private JPanel modifyStatusButton() {
        JButton modifyStatusButton = new JButton("Modify Status");
        modifyStatusButton.addActionListener(e -> modifyTaskStatus());

        JPanel assignPanel = new JPanel(new BorderLayout());
        assignPanel.add(modifyStatusButton, BorderLayout.CENTER);
        return assignPanel;
    }

    private JPanel viewStatisticsButton() {
        JButton viewStatisticsButton = new JButton("View Statistics");
        viewStatisticsButton.addActionListener(e -> viewStatistics());

        JPanel assignPanel = new JPanel(new BorderLayout());
        assignPanel.add(viewStatisticsButton, BorderLayout.CENTER);
        return assignPanel;
    }

    private void addEmployee() {
        String name = JOptionPane.showInputDialog(mainFrame, "Enter employee name:", "Add Employee", JOptionPane.PLAIN_MESSAGE);
        if (name != null && !name.trim().isEmpty()) {
            Employee employee = new Employee(employeeCounter, name);
            employeeCounter++;
            taskManagement.addEmployee(employee);
            employeeListModel.addElement(employee.getName());
        }
        updateEmplyeeList();
    }

    private void addTask() {
        if (employeeList.getSelectedValue() == null) {
            String selectedTask = taskList.getSelectedValue();

            if (selectedTask == null) {
                addNewTask();
            } else {
                addSubTask(selectedTask);
            }
        }
    }

    private void addNewTask() {
        Object[] options = {"Simple Task", "Complex Task"};
        int choice = JOptionPane.showOptionDialog(
                mainFrame,
                "Select the type of task:",
                "Add Task",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
        );

        Task task = null;
        if (choice == JOptionPane.YES_OPTION) {
            task = createSimpleTask();
        } else if (choice == JOptionPane.NO_OPTION) {
            task = new ComplexTask(taskCounter);
        }

        if (task != null) {
            taskCounter++;
            taskManagement.addTask(task);
            updateTaskList();
        }
    }

    private Task createSimpleTask() {
        int startHour = Integer.parseInt(JOptionPane.showInputDialog(mainFrame, "Enter start hour:", "Simple Task", JOptionPane.PLAIN_MESSAGE));
        int endHour = Integer.parseInt(JOptionPane.showInputDialog(mainFrame, "Enter end hour:", "Simple Task", JOptionPane.PLAIN_MESSAGE));

        if (startHour < 0 || endHour < 0 || endHour > 23 || startHour > 23) {
            JOptionPane.showMessageDialog(mainFrame, "Invalid time.", "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }

        return new SimpleTask(taskCounter, startHour, endHour);
    }

    private void addSubTask(String selectedTask) {
        int taskId = extractTaskId(selectedTask);
        Task selectedTaskObject = taskManagement.getTaskById(taskId);

        if (selectedTaskObject instanceof ComplexTask) {
            addSubTaskToComplexTask((ComplexTask) selectedTaskObject);
        }
    }

    private void addSubTaskToComplexTask(ComplexTask selectedTaskObject) {
        Object[] options = {"Simple Task", "Complex Task"};
        int choice = JOptionPane.showOptionDialog(
                mainFrame,
                "Select the type of subtask:",
                "Add Subtask",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
        );

        Task subTask = null;
        if (choice == JOptionPane.YES_OPTION) {
            subTask = createSimpleTask();
        } else if (choice == JOptionPane.NO_OPTION) {
            subTask = new ComplexTask(taskCounter);
        }

        if (subTask != null) {
            taskCounter++;
            selectedTaskObject.addTask(subTask);
            taskManagement.addTask(subTask);
            updateTaskList();
        }
    }


    private void assignTaskToEmployee() {
        String selectedEmployeeName = employeeList.getSelectedValue();
        if (selectedEmployeeName == null) {
            JOptionPane.showMessageDialog(mainFrame, "Please select an employee.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        selectedEmployeeName = employeeList.getSelectedValue().split(" ")[0];

        Employee selectedEmployee = null;
        for (Employee employee : taskManagement.taskMap.keySet()) {
            if (employee.getName().equals(selectedEmployeeName)) {
                selectedEmployee = employee;
                break;
            }
        }

        String taskIdInput = JOptionPane.showInputDialog(mainFrame, "Enter task ID:", "Assign Task", JOptionPane.PLAIN_MESSAGE);
        if (taskIdInput != null && !taskIdInput.trim().isEmpty()) {
            int taskId = Integer.parseInt(taskIdInput);
            Task task = taskManagement.getTaskById(taskId);

            if (task != null) {
                taskManagement.assignTaskToEmployee(selectedEmployee.getIdEmployee(), task);
                updateTaskList();
                updateEmplyeeList();
            } else {
                JOptionPane.showMessageDialog(mainFrame, "Task not found.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void updateTaskList() {
        String selectedEmployeeName = employeeList.getSelectedValue();
        taskListModel.clear();

        if (selectedEmployeeName == null) {
            for (Task task : taskManagement.getTasks()) {
                addTaskToList(task);
            }
        } else {
            selectedEmployeeName = employeeList.getSelectedValue().split(" ")[0];
            for (Employee employee : taskManagement.taskMap.keySet()) {
                if (employee.getName().equals(selectedEmployeeName)) {
                    List<Task> tasks = taskManagement.taskMap.get(employee);
                    for (Task task : tasks) {
                        addTaskToList(task);
                    }
                }
            }
        }
    }

    private void updateEmplyeeList() {
        employeeListModel.clear();

        for (Employee employee : taskManagement.taskMap.keySet()) {
            employeeListModel.addElement(employee.getName() + " - " + taskManagement.calculateEmployeeWorkDuration(employee.getIdEmployee()) + "h");
        }
    }

    private void addTaskToList(Task task) {
        if (isTaskInList(task)) {
            return;
        }

        if (task instanceof SimpleTask) {
            if (task.getStatusTask()) {
                taskListModel.addElement("[S] Task " + task.getIdTask() + " Completed - " + task.estimateDuration() + "h");
            } else {
                taskListModel.addElement("[S] Task " + task.getIdTask() + " Uncompleted - " + task.estimateDuration() + "h");
            }
        } else if (task instanceof ComplexTask) {
            if (task.getStatusTask()) {
                taskListModel.addElement("[C] Task " + task.getIdTask() + " Completed - " + task.estimateDuration() + "h");
            } else {
                taskListModel.addElement("[C] Task " + task.getIdTask() + " Uncompleted - " + task.estimateDuration() + "h");
            }

            ComplexTask complexTask = (ComplexTask) task;
            for (Task subTask : complexTask.getSubTasks()) {
                addSubtaskToList(subTask, 1);
            }
        }
    }

    private void addSubtaskToList(Task task, int indentLevel) {
        String taskText = "";
        for(int i=0; i<indentLevel; i++) {
            taskText += "      ";
        }

        if (task instanceof SimpleTask) {
            if(task.getStatusTask()) {
                taskText += "[S] Task " + task.getIdTask() + " Completed - " + task.estimateDuration() + "h";
            } else{
                taskText += "[S] Task " + task.getIdTask() + " Uncompleted - " + task.estimateDuration() + "h";
            }

            if (!isTaskInList(task)) {
                taskListModel.addElement(taskText);
            }
        } else if (task instanceof ComplexTask) {
            if(task.getStatusTask()) {
                taskText += "[C] Task " + task.getIdTask() + " Completed - " + task.estimateDuration() + "h";
            } else{
                taskText += "[C] Task " + task.getIdTask() + " Uncompleted - " + task.estimateDuration() + "h";
            }

            if (!isTaskInList(task)) {
                taskListModel.addElement(taskText);
            }

            ComplexTask complexTask = (ComplexTask) task;
            for (Task subTask : complexTask.getSubTasks()) {
                addSubtaskToList(subTask, indentLevel + 1);
            }
        }
    }

    private boolean isTaskInList(Task task) {
        for (int i = 0; i < taskListModel.size(); i++) {
            String listItem = taskListModel.get(i);
            if (listItem.contains("Task " + task.getIdTask())) {
                return true;
            }
        }
        return false;
    }


    private void modifyTaskStatus() {
        String selectedTask = taskList.getSelectedValue();
        String selectedEmployee = employeeList.getSelectedValue();

        if (selectedEmployee == null) {
            JOptionPane.showMessageDialog(mainFrame, "Please select an employee.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        selectedEmployee = employeeList.getSelectedValue().split(" ")[0];

        int emplyeeId = 0;
        for (Employee employee : taskManagement.taskMap.keySet()) {
            if (employee.getName().equals(selectedEmployee)) {
                emplyeeId = employee.getIdEmployee();
            }
        }

        if (selectedTask == null) {
            JOptionPane.showMessageDialog(mainFrame, "Please select a task.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int taskId = extractTaskId(selectedTask);
        Task task = taskManagement.getTaskById(taskId);

        if (task instanceof SimpleTask) {
            taskManagement.modifyTaskStatus(emplyeeId, taskId);
        } else if (task instanceof ComplexTask) {
            ComplexTask complexTask = (ComplexTask) task;

            if (areAllSubtasksCompleted(complexTask)) {
                taskManagement.modifyTaskStatus(emplyeeId, taskId);
            } else {
                JOptionPane.showMessageDialog(mainFrame, "All subtasks must be completed before the complex task can be completed.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        for (Task tasks : taskManagement.getTasks()) {
            if(tasks instanceof ComplexTask) {
                if(!areAllSubtasksCompleted((ComplexTask) tasks)){
                    tasks.setStatusTask(false);
                }
            }
        }

        updateTaskList();
    }

    private boolean areAllSubtasksCompleted(ComplexTask complexTask) {
        for (Task subTask : complexTask.getSubTasks()) {
            if (!subTask.getStatusTask()) {
                return false;
            }
        }
        return true;
    }

    private int extractTaskId(String taskString) {
        Pattern pattern = Pattern.compile("Task (\\d+)");
        Matcher matcher = pattern.matcher(taskString);

        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        } else {
            throw new IllegalArgumentException("Task ID not found.");
        }
    }

    private void toggleAddTaskButton() {
        addTaskButton.setEnabled(employeeList.getSelectedValue() == null);
    }

    public void viewStatistics() {
        List<Employee> filteredEmployees = Utility.filterEmployees(taskManagement);
        String stats = "";

        for (Employee employee : filteredEmployees) {
            stats += employee.getName() + " - " + taskManagement.calculateEmployeeWorkDuration(employee.getIdEmployee()) + " hours\n";
        }

        if (!stats.isEmpty()) {
            JOptionPane.showMessageDialog(null, stats, "Employee Work Statistics", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, "There are no emplyees that have over 40h of work duration.", "Employee Work Statistics", JOptionPane.INFORMATION_MESSAGE);
        }

        String taskStats = "";
        Map<String, Map<String, Integer>> taskStatusMap = Utility.calculateTaskStatusCount(taskManagement);

        for (Map.Entry<String, Map<String, Integer>> entry : taskStatusMap.entrySet()) {
            String employeeName = entry.getKey();
            Map<String, Integer> statusCounts = entry.getValue();
            int completed = statusCounts.get("Completed");
            int uncompleted = statusCounts.get("Uncompleted");

            taskStats += employeeName + " - Completed: " + completed + ", Uncompleted: " + uncompleted + "\n";
        }

        if (!taskStats.isEmpty()) {
            JOptionPane.showMessageDialog(null, taskStats, "Task Status Count", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, "No tasks available to show the status.", "Task Status Count", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(TaskManagementGUI::new);
    }
}
