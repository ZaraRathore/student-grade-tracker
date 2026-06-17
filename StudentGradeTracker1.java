import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import javax.swing.table.DefaultTableModel;//ye table ka data manage kare gi 

public class StudentGradeTracker1 extends JFrame {
    private final ArrayList<Student> students = new ArrayList<>();// jisme saare students store honge.
    private JTextField nameField;
    private JTextField gradeField;
    private DefaultTableModel tableModel;
    private JTable table;
    private JLabel avgLabel;
    private JLabel highestLabel;
    private JLabel lowestLabel;
    private JLabel countLabel;
    private JTextArea summaryArea;

    public StudentGradeTracker1() {
        setTitle("Student Grade Tracker - CodeAlpha");// ye title hoga window ka.
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        JPanel inputPanel = new JPanel(new FlowLayout());
        inputPanel.setBorder(BorderFactory.createTitledBorder("Add Student"));

        nameField = new JTextField(10);
        gradeField = new JTextField(5);
        JButton addButton = new JButton("Add Student");
        JButton removeButton = new JButton("Remove Student");
        JButton clearButton = new JButton("Clear All");
        inputPanel.add(new JLabel("Name:"));
        inputPanel.add(nameField);
        inputPanel.add(new JLabel("Grade: 0-100"));
        inputPanel.add(gradeField);
        inputPanel.add(addButton);
        inputPanel.add(removeButton);
        inputPanel.add(clearButton);

        add(inputPanel, BorderLayout.NORTH);
        addButton.addActionListener(this::onAddStudent);
        removeButton.addActionListener(this::onRemoveStudent);
        clearButton.addActionListener(this::onClearAll);
        gradeField.addActionListener(this::onAddStudent);
        add(buildTablePanel(), BorderLayout.CENTER);
        add(buildSummaryPanel(), BorderLayout.SOUTH);
    }

    private JScrollPane buildTablePanel() {
        tableModel = new DefaultTableModel(new Object[] { "Name", "Grade" }, 0) {
            public boolean isCellEditable(int row, int column) {// table mein direct editing nahi krne deni.
                return false;
            }
        };
        table = new JTable(tableModel);
        table.setRowHeight(25);
        table.getTableHeader().setReorderingAllowed(false);
        return new JScrollPane(table);
    }

    private JPanel buildSummaryPanel() {
        JPanel summaryPanel = new JPanel();
        summaryPanel.setLayout(new GridLayout(5, 1));
        summaryPanel.setBorder(BorderFactory.createTitledBorder("Summary"));
        avgLabel = new JLabel("Average Grade: N/A");
        highestLabel = new JLabel("Highest Grade: N/A");
        lowestLabel = new JLabel("Lowest Grade: N/A");
        countLabel = new JLabel("Total Students: 0");
        summaryArea = new JTextArea();
        summaryArea.setEditable(false);
        summaryArea.setLineWrap(true);
        summaryArea.setWrapStyleWord(true);
        summaryPanel.add(avgLabel);
        summaryPanel.add(highestLabel);
        summaryPanel.add(lowestLabel);
        summaryPanel.add(countLabel);
        summaryPanel.add(new JScrollPane(summaryArea));
        return summaryPanel;
    }

    private void onAddStudent(ActionEvent e) {
        String name = nameField.getText().trim();
        String gradeText = gradeField.getText().trim();
        if (name.isEmpty() || gradeText.isEmpty()) {// check karni hogi agr field khali hai to.
            JOptionPane.showMessageDialog(this, "Enter name and grade:", " Error invalid input",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            double grade = Double.parseDouble(gradeText);
            if (grade < 0 || grade > 100) {// grade isi ke beech honi chahiye samjho
                JOptionPane.showMessageDialog(this, "Grade must be between 0 and 100.", "Input Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            Student student = new Student(name, grade);
            students.add(student);
            tableModel.addRow(new Object[] { student.name, student.grade });
            updateSummary();
            nameField.setText("");
            gradeField.setText("");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid number for the grade.", "Input Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onRemoveStudent(ActionEvent e) {
        int row = table.getSelectedRow();
        if (row == -1) {// agr row koi bhi select kiya hua to
            JOptionPane.showMessageDialog(this, "Please select a student to remove.", "Selection Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        students.remove(row);
        tableModel.removeRow(row);
        updateSummary();// kuch bhi calculate nahi krna hai agr list blkl khali hui to
    }

    private void onClearAll(ActionEvent e) {
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to clear all students?",
                "Confirm Clear", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            students.clear();
            tableModel.setRowCount(0);
            updateSummary();
        }
    }

    private void updateSummary() {
        if (students.isEmpty()) {
            avgLabel.setText("Average Grade: N/A");
            highestLabel.setText("Highest Grade: N/A");
            lowestLabel.setText("Lowest Grade: N/A");
            countLabel.setText("Total Students: 0");
            summaryArea.setText("");
            return;
        }
        double total = 0;
        double highest = -Double.MAX_VALUE;
        double lowest = Double.MAX_VALUE;
        StringBuilder summaryBuilder = new StringBuilder();
        summaryBuilder.append("----Student Summary:-----\n");
        summaryBuilder.append("Total Students: ").append(students.size()).append("\n");
        Student topStudent = null;
        Student bottomStudent = null;
        for (Student student : students) {// har student ka grade check kr aur sb kuch us ke baare mein bata
            total += student.grade;
            if (student.grade > highest) {
                highest = student.grade;
                topStudent = student;
            }
            if (student.grade < lowest) {
                lowest = student.grade;
                bottomStudent = student;
            }
        }
        double average = total / students.size();
        avgLabel.setText(String.format("Average Grade: %.2f", average));
        highestLabel.setText(String.format("Highest Grade: %.2f", highest));
        lowestLabel.setText(String.format("Lowest Grade: %.2f", lowest));
        countLabel.setText("Total Students: " + students.size());
        summaryBuilder.append("Average Grade: ").append(String.format("%.2f", average)).append("\n");
        summaryBuilder.append("Highest Grade: ").append(String.format("%.2f", highest)).append("\n");
        if (topStudent != null) {
            summaryBuilder.append("Top Student: ").append(topStudent.name).append(" (").append(topStudent.grade)
                    .append(")\n");
        }
        summaryBuilder.append("Lowest Grade: ").append(String.format("%.2f", lowest)).append("\n");
        if (bottomStudent != null) {
            summaryBuilder.append("Bottom Student: ").append(bottomStudent.name).append(" (")
                    .append(bottomStudent.grade).append(")\n");
        }
        summaryBuilder.append("-------------------------\n");
        summaryArea.setText(summaryBuilder.toString());
    }

    static class Student {
        String name;
        double grade;

        Student(String name, double grade) {
            this.name = name;
            this.grade = grade;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            StudentGradeTracker1 tracker = new StudentGradeTracker1();
            tracker.setVisible(true);
        });
    }

}
