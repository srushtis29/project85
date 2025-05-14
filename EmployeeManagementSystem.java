import java.awt.*;
import java.io.*;
import javax.swing.*;

// Custom Exception
class InvalidSalaryException extends Exception {
    public InvalidSalaryException(String message) {
        super(message);
    }
}

// Interfaces
interface Payable {
    double calculateSalary();
}

interface ReportGenerator {
    String generateReport();
}

// Base class
class Employee implements Payable, ReportGenerator {
    String id, name;
    double salary;

    public Employee(String id, String name, double salary) throws InvalidSalaryException {
        if (salary < 0) throw new InvalidSalaryException("Salary cannot be negative");
        this.id = id;
        this.name = name;
        this.salary = salary;
    }

    public double calculateSalary() {
        return salary;
    }

    public String generateReport() {
        return "ID: " + id + ", Name: " + name + ", Salary: " + salary;
    }

    public String toFileString() {
        return id + "," + name + "," + salary;
    }
}

// Inheritance
class Manager extends Employee {
    public Manager(String id, String name, double salary) throws InvalidSalaryException {
        super(id, name, salary + 10000);
    }
}

class Developer extends Employee {
    public Developer(String id, String name, double salary) throws InvalidSalaryException {
        super(id, name, salary + 5000);
    }
}

class Intern extends Employee {
    public Intern(String id, String name, double salary) throws InvalidSalaryException {
        super(id, name, salary);
    }
}

public class EmployeeManagementSystem extends JFrame {

    JTextField idField, nameField, salaryField;
    JComboBox<String> roleBox;
    JTextArea outputArea;

    File file = new File("employees.txt");

    public EmployeeManagementSystem() {
        setTitle("Employee Management System");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Tabs
        JTabbedPane tabs = new JTabbedPane();

        // Add Employee Panel
        JPanel addPanel = new JPanel(new GridLayout(6, 2));
        idField = new JTextField();
        nameField = new JTextField();
        salaryField = new JTextField();
        roleBox = new JComboBox<>(new String[]{"Manager", "Developer", "Intern"});
        JButton addBtn = new JButton("Add Employee");
        JLabel message = new JLabel();

        addPanel.add(new JLabel("Employee ID:"));
        addPanel.add(idField);
        addPanel.add(new JLabel("Name:"));
        addPanel.add(nameField);
        addPanel.add(new JLabel("Base Salary:"));
        addPanel.add(salaryField);
        addPanel.add(new JLabel("Role:"));
        addPanel.add(roleBox);
        addPanel.add(addBtn);
        addPanel.add(message);

        addBtn.addActionListener(e -> {
            try {
                String id = idField.getText();
                String name = nameField.getText();
                double salary = Double.parseDouble(salaryField.getText());
                String role = (String) roleBox.getSelectedItem();
                Employee emp;

                switch (role) {
                    case "Manager":
                        emp = new Manager(id, name, salary);
                        break;
                    case "Developer":
                        emp = new Developer(id, name, salary);
                        break;
                    default:
                        emp = new Intern(id, name, salary);
                }

                FileWriter fw = new FileWriter(file, true);
                fw.write(emp.toFileString() + "\n");
                fw.close();
                message.setText("Employee added successfully!");
            } catch (InvalidSalaryException ex) {
                message.setText("Error: " + ex.getMessage());
            } catch (Exception ex) {
                message.setText("Invalid input!");
            }
        });

        // View Employees Panel
        JPanel viewPanel = new JPanel(new BorderLayout());
        outputArea = new JTextArea();
        JButton viewBtn = new JButton("View All Employees");
        viewPanel.add(new JScrollPane(outputArea), BorderLayout.CENTER);
        viewPanel.add(viewBtn, BorderLayout.SOUTH);

        viewBtn.addActionListener(e -> {
            outputArea.setText("");
            try {
                BufferedReader br = new BufferedReader(new FileReader(file));
                String line;
                while ((line = br.readLine()) != null) {
                    String[] data = line.split(",");
                    outputArea.append("ID: " + data[0] + ", Name: " + data[1] + ", Salary: " + data[2] + "\n");
                }
                br.close();
            } catch (Exception ex) {
                outputArea.setText("Error reading file.");
            }
        });

        // Delete Employee Panel
        JPanel deletePanel = new JPanel(new GridLayout(3, 2));
        JTextField delIdField = new JTextField();
        JButton delBtn = new JButton("Delete Employee");
        JLabel delMsg = new JLabel();

        deletePanel.add(new JLabel("Enter ID to Delete:"));
        deletePanel.add(delIdField);
        deletePanel.add(delBtn);
        deletePanel.add(delMsg);

        delBtn.addActionListener(e -> {
            String delId = delIdField.getText();
            try {
                File tempFile = new File("temp.txt");
                BufferedReader br = new BufferedReader(new FileReader(file));
                BufferedWriter bw = new BufferedWriter(new FileWriter(tempFile));

                String line;
                boolean found = false;
                while ((line = br.readLine()) != null) {
                    if (!line.startsWith(delId + ",")) {
                        bw.write(line + "\n");
                    } else {
                        found = true;
                    }
                }

                br.close();
                bw.close();

                file.delete();
                tempFile.renameTo(file);

                delMsg.setText(found ? "Employee deleted." : "Employee not found.");
            } catch (Exception ex) {
                delMsg.setText("Error deleting employee.");
            }
        });

        // Add tabs
        tabs.add("Add Employee", addPanel);
        tabs.add("View Employees", viewPanel);
        tabs.add("Delete Employee", deletePanel);

        add(tabs);

        setVisible(true);
    }

    public static void main(String[] args) {
        new EmployeeManagementSystem();
    }
}
