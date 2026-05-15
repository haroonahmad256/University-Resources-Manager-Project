package Model;

import Database.DBConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Teacher {
    private String teacherID;
    private String name;
    private String department;
    private String email;
    private String specilization;
    private int weeklyHours;

    public Teacher(){}

    public Teacher(String teacherID, String name, String department, String email, String specilization, int weeklyHours){
        this.teacherID= teacherID;
        this.name= name;
        this.department= department;
        this.email= email;
        this.specilization= specilization;
        this.weeklyHours= weeklyHours;
    }

    //GetterSetter
    String getTeacherID(){
        return this.teacherID;
    }
    String getName(){
        return this.name;
    }
    String getDepartment(){
        return this.department;
    }
    String getEmail(){
        return this.email;
    }
    String getSpecilization(){
        return this.specilization;
    }
    int getWeeklyHours(){
        return this.weeklyHours;
    }
    void setTeacherID(String teacherID){
        this.teacherID= teacherID;
    }
    void setName(String name){
        this.name= name;
    }
    void setDepartment(String department){
        this.department= department;
    }
    void setEmail(String email){
        this.email= email;
    }
    void setSpecilization(String specilization){
        this.specilization= specilization;
    }
    void setWeeklyHours(int weeklyHours){
        this.weeklyHours= weeklyHours;
    }
    DBConnection connection;

    public void showTeachersDataInGUITable(JTable table){
        connection= new DBConnection();
        String[] columnsName= {"Teacher ID", "Name", "Department", "Email", "Specialization", "Weekly Hours"};
        DefaultTableModel model= new DefaultTableModel(columnsName, 0);
        String sqlQuery= "SELECT * FROM teachers";
        try(PreparedStatement statement= connection.getDatabaseConnection().prepareStatement(sqlQuery)){
            ResultSet resultSet= statement.executeQuery();
            while(resultSet.next()){
                this.teacherID= resultSet.getString("teacherId");
                this.name= resultSet.getString("name");
                this.department= resultSet.getString("department");
                this.email= resultSet.getString("emailAddress");
                this.specilization= resultSet.getString("specialization");
                this.weeklyHours= resultSet.getInt("weeklyHours");

                Object[] row= {teacherID, name, department, email, specilization, weeklyHours};
                model.addRow(row);
            }
            table.setModel(model);
        }
        catch(SQLException e){
            System.out.println(e.getMessage());
        }
    }

    public void totalTeachers(JLabel label){
        connection= new DBConnection();
        String sqlQuery= "SELECT COUNT(teacherId) FROM teachers";
        try(PreparedStatement statement = connection.getDatabaseConnection().prepareStatement(sqlQuery)){
            ResultSet resultSet= statement.executeQuery();
            resultSet.next();
            int count= resultSet.getInt(1);
            label.setText(String.valueOf(count));
            System.out.println("Success || count: "+count);
        }
        catch(SQLException e){
            System.out.println(e.getMessage());
        }
    }

    public void addTeacher(JTextField teacherId, JTextField teacherFullName, JTextField department, JTextField specialization, JTextField email, JSpinner weeklyHours){
        connection= new DBConnection();
        int rowsAffected;
        this.teacherID= teacherId.getText();
        this.name= teacherFullName.getText();
        this.department= department.getText();
        this.specilization= specialization.getText();
        this.email= email.getText();
        this.weeklyHours= (Integer) weeklyHours.getValue();
        String sqlQuery= "INSERT INTO teachers (teacherId, name, department, emailAddress, specialization, weeklyHours) VALUES (?, ?, ?, ?, ?, ?)";
        try(PreparedStatement statement= connection.getDatabaseConnection().prepareStatement(sqlQuery)){
            statement.setString(1, this.teacherID);
            statement.setString(2, this.name);
            statement.setString(3, this.department);
            statement.setString(4, this.email);
            statement.setString(5, this.specilization);
            statement.setInt(6, this.weeklyHours);
            rowsAffected= statement.executeUpdate();
            System.out.println("Teacher Added || rows Affected: "+rowsAffected);
            JOptionPane.showMessageDialog(null, "Teacher Added Successfully");
        }
        catch(SQLException e){
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }

    public void FetchTeacherInfo(JTextField teacherId, JTextField teacherFullName, JTextField department, JTextField specialization, JTextField email, JSpinner weeklyHours){
        this.teacherID= teacherId.getText();
        connection= new DBConnection();
        String sqlQuery= "Select * FROM teachers WHERE teacherId= ?";
        try(PreparedStatement statement= connection.getDatabaseConnection().prepareStatement(sqlQuery)){
            statement.setString(1, this.teacherID);
            ResultSet resultSet = statement.executeQuery();
            if(!resultSet.next()){
                JOptionPane.showMessageDialog(null, "Invalid Teacher ID! Please Enter Valid Teacher ID");
                return;
            }
            teacherFullName.setText(resultSet.getString("name"));
            department.setText(resultSet.getString("department"));
            specialization.setText(resultSet.getString("specialization"));
            email.setText(resultSet.getString("emailAddress"));
            weeklyHours.setValue(resultSet.getInt("weeklyHours"));
            System.out.println("All value Fetched");

        }
        catch (SQLException e){
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }

    public void updateTeacherInfo(JTextField teacherId, JTextField teacherFullName, JTextField department, JTextField specialization, JTextField email, JSpinner weeklyHours){
        connection= new DBConnection();
        this.teacherID=teacherId.getText();
        int rowsAffected;
        String updateNameSqlQuery = "UPDATE teachers SET name=?, department=?, emailAddress=?, specialization=?, weeklyHours=? WHERE teacherId=?";
        try(PreparedStatement statementUpdate = connection.getDatabaseConnection().prepareStatement(updateNameSqlQuery))
        {
            statementUpdate.setString(1, teacherFullName.getText());
            statementUpdate.setString(2, department.getText());
            statementUpdate.setString(3, email.getText());
            statementUpdate.setString(4, specialization.getText());
            statementUpdate.setInt(5, (Integer) weeklyHours.getValue());
            statementUpdate.setString(6, this.teacherID);
            rowsAffected= statementUpdate.executeUpdate();
            if(rowsAffected<1){
                JOptionPane.showMessageDialog(null, "Couldn't Update Teacher! Please Try Again");
                return;
            }
            System.out.println("All Done || Rows Affected: "+rowsAffected);
            JOptionPane.showMessageDialog(null, "Teacher Updated Successfully");
        }
        catch(SQLException e){
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }

    public void fetchDeleteTeacherInfo(JTextField teacherId, JLabel teacherName, JLabel teacherDepartment, JLabel teacherSpecilization, JLabel warning, JLabel teacherFoundNOt){
        connection= new DBConnection();
        String sqlQuery= "SELECT * FROM teachers WHERE teacherId= ?";
        try(PreparedStatement statement= connection.getDatabaseConnection().prepareStatement(sqlQuery)){
            statement.setString(1, teacherId.getText());
            ResultSet resultSet= statement.executeQuery();
            if(!resultSet.next()){
                JOptionPane.showMessageDialog(null, "Invalid Teacher ID! Please Enter Valid Teacher ID");
                return;
            }

            teacherFoundNOt.setText("Teacher Found");
            teacherFoundNOt.setForeground(Color.green);
            teacherName.setText("Teacher Name: "+resultSet.getString("name"));
            teacherDepartment.setText("Department: "+resultSet.getString("department"));
            teacherSpecilization.setText("Specilization: "+resultSet.getString("specialization"));
            warning.setText("Are you sure you want to delete Teacher? This Action can not be undone...");
            System.out.println("All done");
        }
        catch(SQLException e){
            System.out.println(e.getMessage());
        }
    }

    public void deleteTeacher(JTextField tId){
        connection= new DBConnection();
        int rowsAffected;
        String sqlQuery= "DELETE FROM teachers WHERE teacherId= ?";
        try(PreparedStatement statement= connection.getDatabaseConnection().prepareStatement(sqlQuery)){
            statement.setString(1, tId.getText());
            rowsAffected = statement.executeUpdate();
            if(rowsAffected<1){
                JOptionPane.showMessageDialog(null, "Couldn't Delete Teacher! Please Try Again");
            }
            System.out.println("All Done || rows Affected: "+rowsAffected);
            JOptionPane.showMessageDialog(null, "Teacher Data Deleted");
        }
        catch(SQLException e){
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }

    public boolean searchTeacher(JTextField idField, JLabel id, JLabel name, JLabel department, JLabel email, JLabel specilization, JLabel weeklyHours){
        connection= new DBConnection();
        String sqlQuery= "SELECT * FROM teachers WHERE teacherId=? OR name=?";
        try(PreparedStatement statement= connection.getDatabaseConnection().prepareStatement(sqlQuery)){
            statement.setString(1, idField.getText());
            statement.setString(2, idField.getText());
            ResultSet resultSet= statement.executeQuery();
            if(!resultSet.next()){
                JOptionPane.showMessageDialog(null, "Teacher doesn't exist. Please enter a valid id");
                return false;
            }

            name.setText(resultSet.getString("name"));
            department.setText(String.valueOf(resultSet.getString("department")));
            email.setText(String.valueOf(resultSet.getString("emailAddress")));
            specilization.setText(resultSet.getString("specialization"));
            weeklyHours.setText(String.valueOf(resultSet.getInt("weeklyHours")));
            id.setText(resultSet.getString("teacherId"));

        }
        catch(SQLException e){
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
        return true;
    }
}
