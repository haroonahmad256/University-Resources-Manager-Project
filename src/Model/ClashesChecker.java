package Model;

import Database.DBConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.util.Date;
import java.util.Objects;

public class ClashesChecker {
    DBConnection connection= new DBConnection();

    public boolean teacherClashWarning(JSpinner startTime, JSpinner endTime, JComboBox<String> day, JComboBox<String> id){
        String sqlQuery= "SELECT * FROM sechdules WHERE teacherId=? And classDay=? And ?<endTime AND ?>startTime";
        try(PreparedStatement statement = connection.getDatabaseConnection().prepareStatement(sqlQuery)){
            statement.setString(1, id.getSelectedItem().toString());
            statement.setString(2, day.getSelectedItem().toString());


            Date startDate = (Date) startTime.getValue();
            Date endDate = (Date) endTime.getValue();

            Time start = new Time(startDate.getTime());
            Time end = new Time(endDate.getTime());

            statement.setTime(3, start);
            statement.setTime(4, end);
            ResultSet resultSet= statement.executeQuery();
            if(!resultSet.next()){
                return false;
            }
            else{
                System.out.println("Clash Detected");
            }
        }
        catch(SQLException e){
            System.out.println(e.getMessage());
        }
        return true;
    }
    public boolean roomClashWarning(JSpinner startTime, JSpinner endTime, JComboBox<String> day, JComboBox<String> id){
        String sqlQuery= "SELECT * FROM sechdules WHERE roomId=? And classDay=? And ?<endTime AND ?>startTime";
        try(PreparedStatement statement = connection.getDatabaseConnection().prepareStatement(sqlQuery)){
            statement.setString(1, id.getSelectedItem().toString());
            statement.setString(2, day.getSelectedItem().toString());

            Date startDate = (Date) startTime.getValue();
            Date endDate = (Date) endTime.getValue();

            Time start = new Time(startDate.getTime());
            Time end = new Time(endDate.getTime());

            statement.setTime(3, start);
            statement.setTime(4, end);
            ResultSet resultSet= statement.executeQuery();
            if(!resultSet.next()){
                return false;
            }
            else{
                System.out.println("Clash Detected");
            }
        }
        catch(SQLException e){
            System.out.println(e.getMessage());
        }
        return true;
    }

    public boolean batchClashWarning(JSpinner startTime, JSpinner endTime, JComboBox<String> day, JComboBox<String> id){
        String sqlQuery= "SELECT * FROM sechdules WHERE batch_id=? And classDay=? And ?<endTime AND ?>startTime";
        try(PreparedStatement statement = connection.getDatabaseConnection().prepareStatement(sqlQuery)){
            statement.setString(1, id.getSelectedItem().toString());
            statement.setString(2, day.getSelectedItem().toString());

            Date startDate = (Date) startTime.getValue();
            Date endDate = (Date) endTime.getValue();

            Time start = new Time(startDate.getTime());
            Time end = new Time(endDate.getTime());

            statement.setTime(3, start);
            statement.setTime(4, end);
            ResultSet resultSet= statement.executeQuery();
            if(!resultSet.next()){
                return false;
            }
            else{
                System.out.println("Clash Detected");
            }
        }
        catch(SQLException e){
            System.out.println(e.getMessage());
        }
        return true;
    }

    public void checkResources(JComboBox<String> resourceList, JSpinner startTime, JSpinner endTime, JComboBox<String> day, JTextField id, JLabel statusLabel){
        String resources = (String) resourceList.getSelectedItem();
        if (resources.equalsIgnoreCase("Teacher")){
            String sqlQuerySchedule = "SELECT * FROM sechdules WHERE teacherId=?";

            try (PreparedStatement statementSchedule =
                         connection.getDatabaseConnection().prepareStatement(sqlQuerySchedule)) {

                statementSchedule.setString(1, id.getText());
                ResultSet resultSet = statementSchedule.executeQuery();

                if (!resultSet.next()) {
                    JOptionPane.showMessageDialog(null, "Teacher doesn't exist!");
                    return;
                }

                Date startDate = (Date) startTime.getValue();
                Date endDate = (Date) endTime.getValue();

                Time start = new Time(startDate.getTime());
                Time end = new Time(endDate.getTime());

                boolean isAvailable = true;

                do {
                    Time dbStart = resultSet.getTime("startTime");
                    Time dbEnd = resultSet.getTime("endTime");

                    String dayFromDatabase= resultSet.getString("classDay").trim();
                    String daySelected= day.getSelectedItem().toString().trim();

                    System.out.println("Db day: "+dayFromDatabase);
                    System.out.println("selected Day: "+daySelected);
                    if (start.before(dbEnd) && end.after(dbStart) && daySelected.equalsIgnoreCase(dayFromDatabase)) {
                        isAvailable = false;
                        break;
                    }

                } while (resultSet.next());

                if (!isAvailable) {
                    statusLabel.setText("Not Available");
                    JOptionPane.showMessageDialog(null, "Teacher is not available");
                } else {
                    statusLabel.setText("Available");
                    JOptionPane.showMessageDialog(null, "Teacher is available");
                }

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, e.getMessage());
            }
        }
        else if(resources.equalsIgnoreCase("Room")){
            String sqlQuerySchedule= "SELECT * FROM sechdules WHERE roomId=?";
            try (PreparedStatement statementSchedule =
                         connection.getDatabaseConnection().prepareStatement(sqlQuerySchedule)) {

                statementSchedule.setString(1, id.getText());
                ResultSet resultSet = statementSchedule.executeQuery();

                if (!resultSet.next()) {
                    JOptionPane.showMessageDialog(null, "Room doesn't exist!");
                    return;
                }

                Date startDate = (Date) startTime.getValue();
                Date endDate = (Date) endTime.getValue();

                Time start = new Time(startDate.getTime());
                Time end = new Time(endDate.getTime());

                boolean isAvailable = true;

                do {
                    Time dbStart = resultSet.getTime("startTime");
                    Time dbEnd = resultSet.getTime("endTime");
                    String dayFromDatabase= resultSet.getString("classDay").trim();
                    String daySelected= day.getSelectedItem().toString().trim();

                    if (start.before(dbEnd) && end.after(dbStart) && daySelected.equalsIgnoreCase(dayFromDatabase)) {
                        isAvailable = false;
                        break;
                    }
                } while (resultSet.next());

                if (!isAvailable) {
                    statusLabel.setText("Not Available");
                    JOptionPane.showMessageDialog(null, "Room is not available");
                } else {
                    statusLabel.setText("Available");
                    JOptionPane.showMessageDialog(null, "Room is available");
                }

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, e.getMessage());
            }
        }
        else if(resources.equalsIgnoreCase("Batch")) {
            String sqlQuerySchedule = "SELECT * FROM sechdules WHERE batch_id=?";
            try (PreparedStatement statementSchedule =
                         connection.getDatabaseConnection().prepareStatement(sqlQuerySchedule)) {

                statementSchedule.setString(1, id.getText());
                ResultSet resultSet = statementSchedule.executeQuery();

                if (!resultSet.next()) {
                    JOptionPane.showMessageDialog(null, "Batch doesn't exist!");
                    return;
                }

                Date startDate = (Date) startTime.getValue();
                Date endDate = (Date) endTime.getValue();

                Time start = new Time(startDate.getTime());
                Time end = new Time(endDate.getTime());

                boolean isAvailable = true;

                do {
                    Time dbStart = resultSet.getTime("startTime");
                    Time dbEnd = resultSet.getTime("endTime");

                    String dayFromDatabase= resultSet.getString("classDay").trim();
                    String daySelected= day.getSelectedItem().toString().trim();

                    if (start.before(dbEnd) && end.after(dbStart) && daySelected.equalsIgnoreCase(dayFromDatabase)) {
                        isAvailable = false;
                        break;
                    }

                } while (resultSet.next());

                if (!isAvailable) {
                    statusLabel.setText("Not Available");
                    JOptionPane.showMessageDialog(null, "Batch is not available");
                } else {
                    statusLabel.setText("Available");
                    JOptionPane.showMessageDialog(null, "Batch is available");
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, e.getMessage());
            }
        }
    }

    public void setClashesTable(JTable table, JLabel countLabel){
        connection= new DBConnection();

        String[] columns= {"Type", "Resource ID", "Day", "Time Slot", "Event A", "Event B"};
        DefaultTableModel model= new DefaultTableModel(columns, 0);

        String sqlQuery= "SELECT 'Room' AS ClashType, a.roomId AS Resource, a.classDay, a.startTime, a.endTime, a.batch_id AS EventA, b.batch_id AS EventB\n" +
                "FROM sechdules a\n" +
                "JOIN sechdules b ON a.roomId = b.roomId\n" +
                "WHERE a.schedule_id < b.schedule_id \n" +
                "  AND a.classDay = b.classDay\n" +
                "  AND (a.startTime < b.endTime AND a.endTime > b.startTime)\n" +
                "\n" +
                "UNION ALL\n" +
                "\n" +
                "-- Teacher Clashes\n" +
                "SELECT 'Teacher' AS ClashType, a.teacherId AS Resource, a.classDay, a.startTime, a.endTime, a.batch_id AS EventA, b.batch_id AS EventB\n" +
                "FROM sechdules a\n" +
                "JOIN sechdules b ON a.teacherId = b.teacherId\n" +
                "WHERE a.schedule_id < b.schedule_id \n" +
                "  AND a.classDay = b.classDay\n" +
                "  AND (a.startTime < b.endTime AND a.endTime > b.startTime)\n" +
                "\n" +
                "UNION ALL\n" +
                "\n" +
                "-- Batch Clashes\n" +
                "SELECT 'Batch' AS ClashType, a.batch_id AS Resource, a.classDay, a.startTime, a.endTime, a.teacherId AS EventA, b.teacherId AS EventB\n" +
                "FROM sechdules a\n" +
                "JOIN sechdules b ON a.batch_id = b.batch_id\n" +
                "WHERE a.schedule_id < b.schedule_id \n" +
                "  AND a.classDay = b.classDay\n" +
                "  AND (a.startTime < b.endTime AND a.endTime > b.startTime);\n";

        try(PreparedStatement statement= connection.getDatabaseConnection().prepareStatement(sqlQuery)){
            ResultSet resultSet= statement.executeQuery();
            if(!resultSet.next()){
                JOptionPane.showMessageDialog(null, "No Clash Exist!");
                return;
            }
            int count=0;
            do{
                count++;
                String type, resourceId, day, slot, eventA, eventB;
                type= resultSet.getString("ClashType");
                resourceId = resultSet.getString("Resource");
                day= resultSet.getString("classDay");
                slot= resultSet.getTime("startTime")+"--"+resultSet.getTime("endTime");
                eventA= resultSet.getString("EventA");
                eventB= resultSet.getString("EventB");

                Object[] row= {type, resourceId, day, slot, eventA, eventB};
                model.addRow(row);
            }while(resultSet.next());
            countLabel.setText(String.valueOf(count));
            table.setModel(model);
        }
        catch(SQLException e){
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }
}
