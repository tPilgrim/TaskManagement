package org.example.DataAcces;

import org.example.Business.TaskManagement;

import java.io.*;

public class DataManager {
    private static final String FILE_NAME = "data.txt";

    public static void saveData(TaskManagement taskManagement) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(taskManagement);
        } catch (IOException e) {
            System.out.println("Error saving data.");
        }
    }

    public static TaskManagement loadData() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            TaskManagement taskManagement = (TaskManagement) ois.readObject();
            return taskManagement;
        } catch (FileNotFoundException e) {
            System.out.println("No save file found.");
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error loading data.");
        }
        return new TaskManagement();
    }
}
