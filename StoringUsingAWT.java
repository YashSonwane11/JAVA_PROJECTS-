import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class StoringUsingAWT extends Applet implements ActionListener {

    Label l1, l2;
    TextField t1, t2;
    Button b1, b2, b3;
    Panel p;

    String msg = "";
    String data = "";

    private static final String DB_URL = "jdbc:sqlite:studentinfo.db";

    public void init() {
        createTable();

        p = new Panel();
        p.setLayout(new GridLayout(4, 2, 5, 5));

        l1 = new Label("Student Name");
        t1 = new TextField(15);

        l2 = new Label("Subject");
        t2 = new TextField(15);

        b1 = new Button("Save Data");
        b2 = new Button("Reset Data");
        b3 = new Button("Show Data");

        p.add(l1);
        p.add(t1);
        p.add(l2);
        p.add(t2);
        p.add(b1);
        p.add(b2);
        p.add(b3);

        add(p);

        b1.addActionListener(this);
        b2.addActionListener(this);
        b3.addActionListener(this);
    }

    public void paint(Graphics g) {
        g.setFont(new Font("Arial", Font.BOLD, 18));
        g.drawString(msg, 100, 180);

        g.setFont(new Font("Arial", Font.PLAIN, 15));
        g.drawString(data, 100, 220);
    }

    public void createTable() {
        try {
            Connection con = DriverManager.getConnection(DB_URL);

            String sql = "CREATE TABLE IF NOT EXISTS student ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "student_name TEXT, "
                    + "subject TEXT)";

            Statement stmt = con.createStatement();
            stmt.execute(sql);

            con.close();

        } catch (Exception e) {
            msg = e.getMessage();
        }
    }

    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == b1) {
            String sname = t1.getText();
            String sub = t2.getText();

            try {
                Connection con = DriverManager.getConnection(DB_URL);

                String sql = "INSERT INTO student(student_name, subject) VALUES (?, ?)";

                PreparedStatement pst = con.prepareStatement(sql);
                pst.setString(1, sname);
                pst.setString(2, sub);

                pst.executeUpdate();

                msg = "Record Has Been Saved";

                con.close();
                repaint();

            } catch (Exception ex) {
                msg = ex.getMessage();
                repaint();
            }
        }

        else if (e.getSource() == b2) {
            t1.setText("");
            t2.setText("");
            msg = "";
            data = "";
            repaint();
        }

        else if (e.getSource() == b3) {
            try {
                Connection con = DriverManager.getConnection(DB_URL);

                String sql = "SELECT * FROM student";

                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(sql);

                data = "";

                while (rs.next()) {
                    data += rs.getInt("id") + ". "
                            + rs.getString("student_name") + " - "
                            + rs.getString("subject") + "   ";
                }

                msg = "Stored Student Records:";

                con.close();
                repaint();

            } catch (Exception ex) {
                msg = ex.getMessage();
                repaint();
            }
        }
    }
}