/*
package dev.lumentae.logkeepr.data;

import java.util.ArrayList;
import java.util.List;

import dev.lumentae.logkeepr.data.classes.Project;

public class ProjectManager {
    private static final List<Project> projects = new ArrayList<>();

    public static Project createProject(String name, String description) {
        Project project = new Project();
        project.id = String.valueOf(System.currentTimeMillis()); // Simple ID generation
        project.name = name;
        project.description = description;

        projects.add(project);
        return project;
    }

    public static void addProject(Project project) {
        if (getProject(project.id) != null) {
            // Optionally throw an exception if the project already exists
            return;
        }
        projects.add(project);
    }

    public static void removeProject(String projectId) {
        for (int i = 0; i < projects.size(); i++) {
            if (projects.get(i).id.equals(projectId)) {
                projects.remove(i);
                return;
            }
        }
        // Optionally throw an exception if the project is not found
    }

    public static Project getProject(String projectId) {
        for (Project project : projects) {
            if (project.id.equals(projectId)) {
                return project;
            }
        }
        return null; // or throw an exception if preferred
    }

    public static List<Project> getAllProjects() {
        // Implementation to retrieve all projects
        return projects;
    }

    public static void updateProject(Project project) {
        for (int i = 0; i < projects.size(); i++) {
            if (projects.get(i).id.equals(project.id)) {
                projects.set(i, project);
                return;
            }
        }
        // Optionally throw an exception if the project is not found
    }

    public static void saveProjects() {

    }

    public static void loadProjects() {

    }
}
*/